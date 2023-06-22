package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixture;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* @author Andreas Dann created on 07.01.19 */
public class UberJarTestCaseCreator extends TestCaseCreatorChain {

  private final GAV uberGAV;
  private ArrayList<TestFixtureModel> selectedWordModels;

  private TestFixtureModel frankenJarTestFixture;
  private boolean maintainTimeAttributes;

  public UberJarTestCaseCreator(
      Collection<TestFixtureModel> selectedWordModels,
      String outputFolder,
      boolean installJar,
      boolean removeMetaData,
      boolean maintainTimeAttributes) {
    super(installJar, outputFolder, removeMetaData);
    this.selectedWordModels = new ArrayList<>(selectedWordModels);
    this.maintainTimeAttributes = maintainTimeAttributes;

    this.uberGAV = RandomGavCreator.getRandomGave();
  }

  @Override
  protected boolean useUberGAV() {
    return true;
  }

  @Override
  protected boolean createUberJar() {
    return true;
  }

  @Override
  public ArrayList<TestFixtureModel> getSelectedTestFixtures() {
    return selectedWordModels;
  }

  private void selectConflictFreeTestFixtures() {
    // only select test fixtures without conflicting files
    ArrayList<TestFixtureModel> conflictFreeFixtures = new ArrayList<>();
    HashSet<String> filesInAllFixture = new HashSet<>();

    for (TestFixtureModel testFixtureModel : getSelectedTestFixtures()) {

      // add all included files to the list
      Set<String> includedFilesOfTestFixtures =
          Arrays.stream(testFixtureModel.getTestFixtureDetailModel())
              .filter(TestFixtureDetailModel::isInclude)
              .map(TestFixtureDetailModel::getFile)
              .collect(Collectors.toSet());

      Set<String> mutualFiles = new HashSet<>(filesInAllFixture);
      mutualFiles.retainAll(includedFilesOfTestFixtures);

      if (mutualFiles.isEmpty()) {
        // there are no conflicts
        filesInAllFixture.addAll(includedFilesOfTestFixtures);
        conflictFreeFixtures.add(testFixtureModel);
      }
    }

    this.selectedWordModels = conflictFreeFixtures;
  }

  @Override
  protected void prepareTestCases() {
    // use one name for all JARs to create fitting ProjectPom and GroundTruth
    // merge all TestFixtures into OneLarge one to reuse the other creator
    this.selectConflictFreeTestFixtures();
  }

  @Override
  protected void finalizeJarModification() {
    // merge into one franken JAR
    List<Path> jarsToMerge =
        this.getSelectedTestFixtures().stream()
            .map(TestFixtureModel::getJarFile)
            .collect(Collectors.toList());

    Path mergedUberJar = null;
    try {
      mergedUberJar = mergeJARFilesIntoFrankenJAR(jarsToMerge);
    } catch (IOException e) {
      e.printStackTrace();
    }
    TestFixture frankenTestFixture = new TestFixture("fakeCVE", "dummyUberJar", this.uberGAV, true);
    this.frankenJarTestFixture = new TestFixtureModel(frankenTestFixture);
    this.frankenJarTestFixture.setJarFile(mergedUberJar);
  }

  @Override
  protected Collection<TestFixtureModel> getTestFixturesToInstall() {
    return Collections.singletonList(this.frankenJarTestFixture);
  }

  /*protected Path mergeJARFilesIntoFrankenJAR(Collection<Path> jarFiles) throws IOException {

    String fileName = "franken.jar";
    Path tmpPath = Files.createTempDirectory("tmpfiles");
    File outputFile = tmpPath.resolve(fileName).toFile();

    DefaultJarProcessor processors = new DefaultJarProcessor();

    JarTransformer transformer = new JarTransformer(outputFile, processors);

    // move all jars to same tmpdir
    Path tempDirWithPrefix = Files.createTempDirectory(null);

    for (Path jarFile : jarFiles) {
      Path TO = tempDirWithPrefix.resolve(jarFile.getFileName());
      // overwrite the destination file if it exists, and copy
      // the file attributes, including the rwx permissions
      CopyOption[] options =
          new CopyOption[] {
            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES
          };
      Files.copy(jarFile, TO, options);
    }

    File[] inputJARs = jarFiles.stream().map(x -> x.getFileName().toFile()).toArray(File[]::new);

    ClassPath classPath = new ClassPath(tempDirWithPrefix.toFile(), inputJARs);

    transformer.transform(classPath);

    return outputFile.toPath();
  }*/

  protected Path mergeJARFilesIntoFrankenJAR(Collection<Path> jarFiles) throws IOException {
    String fileName = "franken.jar";
    Path tmpPath = Files.createTempDirectory("tmpfiles");
    Path frankenFile = tmpPath.resolve(fileName);

    int i = 0;
    while (Files.exists(frankenFile)) {
      fileName = "franken_" + i + ".jar";
      frankenFile = tmpPath.resolve(fileName);
    }

    // merge the jar files

    OutputStream fos = Files.newOutputStream(frankenFile);
    ZipOutputStream zos = new ZipOutputStream(fos);

    HashSet<String> copiedFiles = new HashSet<>();

    for (Path jarFile : jarFiles) {
      try (FileSystem fs = FileSystems.newFileSystem(jarFile, (java.lang.ClassLoader) null)) {
        final Path archiveRoot = fs.getPath("/");

        Files.walk(archiveRoot)
            .forEach(
                zipFileEntry -> {
                  try {

                    // begin writing a new ZIP entry, positions the stream to the start of the
                    // entry
                    // data

                    String name = zipFileEntry.toAbsolutePath().toString().replaceFirst("/", "");
                    if (copiedFiles.contains(name)) {
                      return;
                    }
                    copiedFiles.add(name);

                    if (Files.isRegularFile(zipFileEntry)) {
                      ZipEntry newZipEntry = new ZipEntry(name);
                      // FIXME: maintain the time?
                      if (this.maintainTimeAttributes) {
                        BasicFileAttributes fatr =
                            Files.readAttributes(zipFileEntry, BasicFileAttributes.class);
                        if (fatr.creationTime() != null) {
                          newZipEntry.setCreationTime(fatr.creationTime());
                        }
                        if (fatr.lastModifiedTime() != null) {
                          newZipEntry.setLastModifiedTime(fatr.lastModifiedTime());
                        }
                        if (fatr.lastAccessTime() != null) {
                          newZipEntry.setLastAccessTime(fatr.lastAccessTime());
                        }
                      }

                      zos.putNextEntry(newZipEntry);

                      byte[] buffer = new byte[1024];

                      int length;
                      InputStream inputStream = Files.newInputStream(zipFileEntry);

                      while ((length = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                      }
                      zos.closeEntry();
                      inputStream.close();
                    }

                  } catch (IOException e) {
                    e.printStackTrace();
                    // if we have a duplicate entry
                  }
                });

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    zos.finish();

    zos.close();

    fos.flush();

    fos.close();

    return frankenFile;
  }
}
