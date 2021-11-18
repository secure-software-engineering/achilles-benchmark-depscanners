package de.upb.achilles.generator.creator.chain;

import static org.junit.Assert.*;

import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class RecompileHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.FORCE_RECOMPILE);

    RecompileHandler recompileHandler = new RecompileHandler();
    assertTrue(recompileHandler.canHandle(model));
  }

  @Test
  public void canHandleNot() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.ORG);

    RecompileHandler recompileHandler = new RecompileHandler();
    assertFalse(recompileHandler.canHandle(model));
  }

  @Test
  public void handle() throws IOException {

    GAV gav = new GAV("com.google.guava", "guava", "28.0-jre");
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel(gav);
    model.setByteCodeModification(ByteCodeModification.FORCE_RECOMPILE);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    boolean exists = Files.exists(downloadedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(downloadedJar);
    assertTrue(regularFile);

    RecompileHandler recompileHandler = new RecompileHandler();

    recompileHandler.handle(model);

    Path recompiledJar = model.getJarFile();

    boolean recompiledExists = Files.exists(recompiledJar);
    assertTrue(recompiledExists);
    boolean regularRecFile = Files.isRegularFile(recompiledJar);
    assertTrue(regularRecFile);

    String orgFilemd5 = "";
    String modFilemd5 = "";

    // hashes should be different
    try (InputStream is = Files.newInputStream(downloadedJar)) {
      orgFilemd5 = DigestUtils.md5Hex(is);
    }

    try (InputStream is = Files.newInputStream(recompiledJar)) {
      modFilemd5 = DigestUtils.md5Hex(is);
    }

    assertNotEquals(orgFilemd5, modFilemd5);
  }
}
