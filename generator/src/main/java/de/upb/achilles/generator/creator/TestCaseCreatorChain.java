package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.creator.chain.ASMByteCodeRecompileHandler;
import de.upb.achilles.generator.creator.chain.ByteCodeModifierHandler;
import de.upb.achilles.generator.creator.chain.DownloadHandler;
import de.upb.achilles.generator.creator.chain.GAVModifierHandler;
import de.upb.achilles.generator.creator.chain.Handler;
import de.upb.achilles.generator.creator.chain.JarRemoveFilesTrimmer;
import de.upb.achilles.generator.creator.chain.MetaDataModifierHandler;
import de.upb.achilles.generator.creator.chain.MetaDataRemoveHandler;
import de.upb.achilles.generator.creator.chain.RecompileHandler;
import de.upb.achilles.generator.creator.chain.RepackageHandler;
import de.upb.achilles.generator.creator.chain.UberGAVHandler;
import de.upb.achilles.generator.creator.helper.PomCreator;
import de.upb.achilles.generator.creator.helper.TruthCreator;
import de.upb.achilles.generator.creator.helper.install.BashInstallScriptCreator;
import de.upb.achilles.generator.creator.helper.install.BatInstallScriptCreator;
import de.upb.achilles.generator.creator.helper.install.JarInstaller;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates the test cases as input for vulnerability scanner
 *
 * @author Andreas Dann created on 19.03.19
 */
public abstract class TestCaseCreatorChain extends Task {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseCreatorChain.class);
  final boolean removeFiles = true;
  @Nullable private GAV projectGAV;
  private final Path projectPath;
  private final boolean installJARs;

  private final Handler handlerChain;
  private final boolean removeMetaData;

  protected TestCaseCreatorChain(boolean installJARs, String outputFolder, boolean removeMetaData) {
    this.installJARs = installJARs;
    this.removeMetaData = removeMetaData;

    this.projectPath = Paths.get(outputFolder);

    // Setup Chain of Responsibility
    Handler h1 = new DownloadHandler();
    Handler h2 = new RecompileHandler();
    Handler h3 = new ASMByteCodeRecompileHandler();
    Handler h4 = new GAVModifierHandler();
    Handler h5 = new UberGAVHandler(useUberGAV());

    Handler h6;
    if (this.removeFiles) {
      h6 = new JarRemoveFilesTrimmer();
    } else {
      h6 = new ByteCodeModifierHandler();
    }

    Handler h7 = new RepackageHandler();
    Handler h8 = new MetaDataModifierHandler();
    Handler h9 = new MetaDataRemoveHandler(removeMetaData);
    h1.setNext(h2);
    h2.setNext(h3);
    h3.setNext(h4);
    h4.setNext(h5);
    h5.setNext(h6);
    h6.setNext(h7);
    h7.setNext(h8);
    if (removeMetaData) {
      h8.setNext(h9);
    }
    handlerChain = h1;
  }

  public boolean isRemoveMetaData() {
    return removeMetaData;
  }

  protected abstract boolean useUberGAV();

  protected String prependProjectGAV() {
    String repackageMode = ByteCodeModification.ORG.getText();
    String gavMode = GAVModification.ORG.getText();
    for (TestFixtureModel testFixtureModel : this.getSelectedTestFixtures()) {
      if (testFixtureModel.getByteCodeModification() != ByteCodeModification.ORG) {
        repackageMode = testFixtureModel.getByteCodeModification().getText();
      }

      if (testFixtureModel.getChangeGAV() != GAVModification.ORG) {
        repackageMode = testFixtureModel.getChangeGAV().getText();
      }
    }

    String append = "";
    append += "class-" + repackageMode;
    append += ".";

    append += "gav-" + gavMode;
    append += ".";

    append += "metadata-" + !this.removeMetaData;
    append += ".";

    append += "uberjar-" + this.createUberJar();

    return append;
  }

  protected abstract boolean createUberJar();

  public final void copyJarFilesIntoProject(Collection<Path> jarFiles) throws IOException {
    for (Path jarFile : jarFiles) {
      Path targetFile = getProjectPath().resolve(jarFile.getFileName());
      Files.copy(jarFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  @Override
  protected Object call() throws IOException, InterruptedException {
    this.createTestCase();
    return null;
  }

  public final void modifyJarFiles() {
    // update the GAVs

    for (Iterator<TestFixtureModel> iterator = this.getSelectedTestFixtures().iterator();
        iterator.hasNext(); ) {

      TestFixtureModel testFixtureModel = iterator.next();

      try {
        handlerChain.handleRequest(testFixtureModel);
      } catch (IOException e) {

        LOGGER.warn(
            "Removed " + testFixtureModel.getOrgGav() + " from selected list because chain failed.",
            e);
        iterator.remove();
      }
    }

    this.finalizeJarModification();
  }

  /**
   * Can be used modify the jar files, after deleting unnecessary classes, e.g., merging into a
   * single jar file
   */
  protected void finalizeJarModification() {}

  private final void createTestCase() throws IOException, InterruptedException {
    this.updateProgress(0,10);
    GAV randomGave = RandomGavCreator.getRandomGave();

    randomGave.setGroupId(this.prependProjectGAV() + "." + randomGave.getGroupId());

    this.projectGAV = randomGave;

    this.prepareTestCases();

    this.updateProgress(1,10);


    this.modifyJarFiles();

    this.updateProgress(2,10);


    this.createGroundTruth();
    this.updateProgress(3,10);

    this.createProjectPom();

    this.updateProgress(4,10);

    Collection<TestFixtureModel> testFixturesToInstall = this.getTestFixturesToInstall();

    if (installJARs) {
      for (TestFixtureModel entry : testFixturesToInstall) {
        JarInstaller.installJarIntoLocalRepo(entry, entry.getJarFile(), null);
        this.updateProgress(8,10);

      }
    } else {
      List<Path> collect =
          testFixturesToInstall.stream()
              .map(TestFixtureModel::getJarFile)
              .collect(Collectors.toList());
      this.copyJarFilesIntoProject(collect);
      this.updateProgress(8,10);

      this.createInstallationScripts(testFixturesToInstall);
      this.updateProgress(9,10);
    }
    this.updateProgress(10,10);

    LOGGER.info("Test Case Creation Done!!!");


  }

  protected abstract Collection<TestFixtureModel> getTestFixturesToInstall();

  protected abstract void prepareTestCases();

  private void createGroundTruth() throws IOException {
    TruthCreator truthCreator =
        new TruthCreator(getProjectGAV(), getSelectedTestFixtures(), getProjectPath());
    truthCreator.createGroundTruth();
  }

  private void createProjectPom() throws IOException {
    Collection<TestFixtureModel> testFixturesToInstall = getTestFixturesToInstall();

    // create only one install command per file/or per gav
    Collection<TestFixtureModel> filteredModels = new ArrayList<>();
    HashSet<GAV> seenGAVs = new HashSet<>();

    for (TestFixtureModel testFixtureModel : testFixturesToInstall) {
      if (seenGAVs.contains(testFixtureModel.getGAV4Pom())) {
        continue;
      }
      filteredModels.add(testFixtureModel);
      seenGAVs.add(testFixtureModel.getGAV4Pom());
    }

    PomCreator pomCreator = new PomCreator(getProjectGAV(), filteredModels, getProjectPath());
    pomCreator.createPomXml();
  }

  public final void createInstallationScripts(Collection<TestFixtureModel> testFixtureModels) {

    createInstallationScriptsFromGAVs(testFixtureModels);
  }

  public final void createInstallationScriptsFromGAVs(
      Collection<TestFixtureModel> testFixtureModels) {

    // create only one install command per file/or per gav
    Collection<TestFixtureModel> filteredModels = new ArrayList<>();
    HashSet<Path> seenJarFileName = new HashSet<>();

    for (TestFixtureModel testFixtureModel : testFixtureModels) {
      if (seenJarFileName.contains(testFixtureModel.getJarFile())) {
        continue;
      }
      filteredModels.add(testFixtureModel);
      seenJarFileName.add(testFixtureModel.getJarFile());
    }

    BashInstallScriptCreator bashInstallScriptCreator =
        new BashInstallScriptCreator(filteredModels, getProjectPath());

    BatInstallScriptCreator batInstallScriptCreator =
        new BatInstallScriptCreator(filteredModels, getProjectPath());
    try {
      bashInstallScriptCreator.createScript();
      batInstallScriptCreator.createScript();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public abstract ArrayList<TestFixtureModel> getSelectedTestFixtures();

  public Path getProjectPath() {
    return projectPath;
  }

  public GAV getProjectGAV() {
    return this.projectGAV;
  }
}
