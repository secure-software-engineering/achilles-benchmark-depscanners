package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.creator.Util;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class JarRemoveFilesTrimmer extends JarTrimmerHandler {
  @Override
  protected void modifyJar(TestFixtureModel requTestFixtureModel) {
    Path jarFile = requTestFixtureModel.getJarFile();
    try (FileSystem zipfs = FileSystems.newFileSystem(Objects.requireNonNull(jarFile), null)) {
      for (TestFixtureDetailModel detailModel : requTestFixtureModel.getTestFixtureDetailModel()) {
        if (!detailModel.isInclude()) {
          String fileName = Util.getFileName(detailModel.getQname());
          Path path = zipfs.getPath(fileName);
          boolean deleteIfExists = Files.deleteIfExists(path);
          if (!deleteIfExists) {
            LOGGER.debug("Could not remove the file " + path);
          } else {
            requTestFixtureModel.getFilesDeletedFromJar().add(detailModel.getFile());
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Cannot open/modify file", e);
    }
  }
}
