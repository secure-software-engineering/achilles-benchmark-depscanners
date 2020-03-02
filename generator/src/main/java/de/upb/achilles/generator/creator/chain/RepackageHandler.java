package de.upb.achilles.generator.creator.chain;

import com.tonicsystems.jarjar.classpath.ClassPath;
import com.tonicsystems.jarjar.transform.JarTransformer;
import com.tonicsystems.jarjar.transform.config.ClassRename;
import com.tonicsystems.jarjar.transform.jar.DefaultJarProcessor;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RepackageHandler extends Handler {

  // use the concat jar files into one
  private static final String ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static final String NUMERIC_STRING = "0123456789";

  private static final String ALPHA_NUMERIC_STRING = ALPHA_STRING + NUMERIC_STRING;

  private final HashMap<GAV, TestFixtureModel> repackagedArtefacts = new HashMap<>();

  private static String randomIdentifier(int count) {
    if (count < 1) {
      return "";
    }

    StringBuilder builder = new StringBuilder();

    // valid java identifier must start with an alpha character
    int character = (int) (Math.random() * ALPHA_STRING.length());
    builder.append(ALPHA_STRING.charAt(character));
    count--;

    while (count-- != 0) {
      character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();
  }

  private static String fixedRepackageName() {
    return "com.repackage";
  }

  private static void addClassRenameRules(
      Path jarFile, DefaultJarProcessor processors, final String prependPackage) {
    ArrayList<String> patternNames = new ArrayList<>();
    try (FileSystem zipFileSys = FileSystems.newFileSystem(jarFile, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      DirectoryStream<Path> paths = Files.newDirectoryStream(archiveRoot);
      for (Path p : paths) {
        if (!(p.getFileName().toString().contains("META-INF"))) {
          if (Files.isDirectory(p)) {
            patternNames.add(p.getFileName().toString().replace("/", "."));
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (String pattern : patternNames) {
      String patternText = pattern + "**";
      ClassRename prependPackageName =
          new ClassRename(patternText, prependPackage + "." + pattern + "@1");
      processors.addClassRename(prependPackageName);
    }
  }

  private static void adaptFQNToRepackageName(
      TestFixtureModel testFixtureModel, String prependPackageName) {

    for (TestFixtureDetailModel detailModel : testFixtureModel.getTestFixtureDetailModel()) {
      String orgName = detailModel.getQname();

      String repackagedName = prependPackageName + "." + orgName;

      detailModel.setRepackagedQName(repackagedName);
    }
  }

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return request.getByteCodeModification() == ByteCodeModification.REPACKAGE;
  }

  @Override
  protected void handle(TestFixtureModel testFixtureModel) throws JarModificationException {
    String prependPackage = fixedRepackageName();

    GAV testFixGav = testFixtureModel.getOrgGav();
    if (repackagedArtefacts.containsKey(testFixGav)) {
      // share the repackaged artifact for all fixtures targeting the same dependency/artefact
      Path recompiledJAr = repackagedArtefacts.get(testFixGav).getJarFile();
      testFixtureModel.setJarFile(recompiledJAr);
      // adapt the names in the ground truth
      adaptFQNToRepackageName(testFixtureModel, prependPackage);
      return;
    }

    File parentFolder = Objects.requireNonNull(testFixtureModel.getJarFile()).getParent().toFile();

    // String prependPackage = randomIdentifier(4);
    try {
      File outputFile =
          new File(
              parentFolder
                  + File.separator
                  + "repackage_"
                  + testFixtureModel.getJarFile().getFileName().toString());
      DefaultJarProcessor processors = new DefaultJarProcessor();

      addClassRenameRules(testFixtureModel.getJarFile(), processors, prependPackage);
      JarTransformer transformer = new JarTransformer(outputFile, processors);

      ClassPath classPath =
          new ClassPath(
              parentFolder, new File[] {testFixtureModel.getJarFile().getFileName().toFile()});

      transformer.transform(classPath);

      testFixtureModel.setJarFile(outputFile.toPath());
      // if repackaging was successful prepend name to ground truth
      adaptFQNToRepackageName(testFixtureModel, prependPackage);

      // adapt the GAV
      // adaptGAV(testFixtureModel);

      Path output = outputFile.toPath();
      testFixtureModel.setJarFile(output);

      repackagedArtefacts.put(testFixGav, testFixtureModel);

    } catch (IOException e) {
      throw new JarModificationException(
          "Failed to repackage " + testFixtureModel.getOrgGav(), e, testFixtureModel);
    }
  }
}
