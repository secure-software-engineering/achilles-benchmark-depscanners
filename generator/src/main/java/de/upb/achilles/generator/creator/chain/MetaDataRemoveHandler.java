package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;

public class MetaDataRemoveHandler extends OneTimeHandler {

  static final Logger LOGGER = LoggerFactory.getLogger(JarTrimmerHandler.class);
  private final boolean removeAllMetaData;

  public MetaDataRemoveHandler(boolean removeAllMetaData) {

    this.removeAllMetaData = removeAllMetaData;
  }

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return removeAllMetaData;
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) throws JarModificationException {

    try {
      Path jarFile = requTestFixtureModel.getJarFile();
      Path tempDirectory = Files.createTempDirectory(null);
      Path copiedFile = tempDirectory.resolve(Objects.requireNonNull(jarFile).getFileName());

      // copy the original file
      Files.copy(jarFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);

      requTestFixtureModel.setJarFile(copiedFile);

      removeMetaData(requTestFixtureModel);
    } catch (JarModificationException e) {
      // modify the zip file digest in any case
      createEmptyZipEntry(requTestFixtureModel);
      LOGGER.debug(
          "The file " + requTestFixtureModel.getJarFile() + " does not contain a META-INF");
    } catch (IOException e) {
      LOGGER.error("Failed to copy jar file " + requTestFixtureModel.getJarFile());
      throw new JarModificationException(
          "Failed to remove metadata from " + requTestFixtureModel.getJarFile(),
          e,
          requTestFixtureModel);
    }
  }

  /**
   * Remove the META-INF folder
   *
   * @param testFixtureModel the fixture for which to remove the metadata
   * @throws JarModificationException if the META-INF folder could not be deleted
   */
  private void removeMetaData(TestFixtureModel testFixtureModel) throws JarModificationException {
    Path jarFile = testFixtureModel.getJarFile();
    try (FileSystem zipfs = FileSystems.newFileSystem(Objects.requireNonNull(jarFile), null)) {
      Path metaInfFolder = zipfs.getPath("/", "META-INF");
      boolean folderExistss = Files.exists(metaInfFolder);
      if (!folderExistss) {
        LOGGER.debug("Could not remove the file " + metaInfFolder);
        throw new JarModificationException(
            "Failed to remove META-INF with", null, testFixtureModel);
      } else {

        Files.walk(metaInfFolder)
            .sorted(Comparator.reverseOrder())
            .peek(x -> LOGGER.debug("Removed file " + x.toString()))
            .forEach(
                path -> {
                  try {
                    Files.delete(path);
                  } catch (IOException e) {
                    LOGGER.debug("Failed to remove the file " + path);
                  }
                });

        LOGGER.debug("Removed  the file " + metaInfFolder);
      }
    } catch (IOException e) {

      LOGGER.debug("Failed to remove META-INF with", e);

      throw new JarModificationException("Failed to remove META-INF with", e, testFixtureModel);
    }
  }

  /**
   * Create an empty zipfile entry in order to change the jar's digest in any case
   *
   * @param testFixtureModel
   */
  private void createEmptyZipEntry(TestFixtureModel testFixtureModel) {

    Path jarFile = testFixtureModel.getJarFile();
    try (FileSystem zipfs = FileSystems.newFileSystem(Objects.requireNonNull(jarFile), null)) {
      Path newEmtpyFiles = Files.createFile(zipfs.getPath("/", ".emptyFile"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
