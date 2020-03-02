package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class MetaDataModifierHandlerTest {

  @Test
  public void canHandle() {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.MOD);
    model.setGAV4Pom(RandomGavCreator.getRandomGave());

    MetaDataModifierHandler downloadHandler = new MetaDataModifierHandler();
    assertTrue(downloadHandler.canHandle(model));
  }

  @Test
  public void canHandleNot() {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.ORG);

    MetaDataModifierHandler downloadHandler = new MetaDataModifierHandler();
    assertFalse(downloadHandler.canHandle(model));
  }



  @Test
  public void handleModify() throws IOException {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.MOD);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    GAVModifierHandler modifierHandler = new GAVModifierHandler();
    modifierHandler.handle(model);

    MetaDataModifierHandler metaDataModifierHandler = new MetaDataModifierHandler();
    metaDataModifierHandler.handle(model);

    Path modifiedJar = model.getJarFile();

    boolean exists = Files.exists(modifiedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(modifiedJar);
    assertTrue(regularFile);
    try (FileSystem zipFileSys = FileSystems.newFileSystem(modifiedJar, null)) {
      final Path maven =
          zipFileSys.getPath(
              "/", "META-INF", "maven", "org.apache.commons", "commons-lang3", "pom.xml");
      boolean exists1 = Files.exists(maven) && Files.isRegularFile(maven);
      assertTrue(exists1);
    }

    String orgFilemd5 = "";
    String modFilemd5 = "";

    // hashes should be different
    try (InputStream is = Files.newInputStream(downloadedJar)) {
      orgFilemd5 = DigestUtils.md5Hex(is);
    }

    try (InputStream is = Files.newInputStream(modifiedJar)) {
      modFilemd5 = DigestUtils.md5Hex(is);
    }

    assertNotEquals(orgFilemd5, modFilemd5);
  }


}
