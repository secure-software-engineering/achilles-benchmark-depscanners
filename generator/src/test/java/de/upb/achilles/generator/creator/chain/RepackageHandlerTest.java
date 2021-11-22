package de.upb.achilles.generator.creator.chain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class RepackageHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.REPACKAGE);

    RepackageHandler repackageHandler = new RepackageHandler();
    assertTrue(repackageHandler.canHandle(model));
  }

  @Test
  public void canHandleNOT() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.ORG);

    RepackageHandler repackageHandler = new RepackageHandler();
    assertFalse(repackageHandler.canHandle(model));
  }

  @Test
  public void handle() throws IOException {

    TestFixtureModel testFixtureModel = HandlerTestUtils.getTestFixtureWithFile();
    testFixtureModel.setByteCodeModification(ByteCodeModification.REPACKAGE);

    RepackageHandler repackageHandler = new RepackageHandler();
    repackageHandler.handle(testFixtureModel);

    Path repackagedJar = testFixtureModel.getJarFile();

    boolean exists = Files.exists(repackagedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(repackagedJar);
    assertTrue(regularFile);

    try (FileSystem zipFileSys = FileSystems.newFileSystem(repackagedJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      DirectoryStream<Path> paths = Files.newDirectoryStream(archiveRoot);
      for (Path p : paths) {
        System.out.println(p.toAbsolutePath());
        if (Files.isDirectory(p)) {
          // if the packages are correctly repackged then the org packages is changed
          String fileName = p.getFileName().toString().replace("/", "");
          assertFalse(fileName.startsWith("org"));
        }
      }
    }

    System.out.println(testFixtureModel.getGAV4Pom().getGroupId());
  }
}
