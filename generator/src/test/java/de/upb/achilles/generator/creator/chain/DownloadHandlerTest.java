package de.upb.achilles.generator.creator.chain;

import static org.junit.Assert.assertTrue;

import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class DownloadHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();

    DownloadHandler downloadHandler = new DownloadHandler();
    assertTrue(downloadHandler.canHandle(model));
  }

  @Test
  public void handle() throws IOException {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    boolean exists = Files.exists(downloadedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(downloadedJar);
    assertTrue(regularFile);
  }

  @Test
  public void handleSrc() throws IOException {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.FORCE_RECOMPILE);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    boolean exists = Files.exists(downloadedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(downloadedJar);
    assertTrue(regularFile);

    assertTrue(downloadedJar.getFileName().toString().endsWith("-sources.jar"));
  }
}
