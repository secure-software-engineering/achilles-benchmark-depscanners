package de.upb.achilles.generator.creator.chain;

import static org.junit.Assert.*;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class MetaDataRemoveHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.MOD);
    MetaDataRemoveHandler metaDataRemover = new MetaDataRemoveHandler(true);
    assertTrue(metaDataRemover.canHandle(model));
  }

  @Test
  public void handleRemove() throws IOException {
    GAV gav = new GAV("commons-fileupload", "commons-fileupload", "1.2");

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel(gav);
    model.setChangeGAV(GAVModification.MOD);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    GAVModifierHandler modifierHandler = new GAVModifierHandler();
    modifierHandler.handle(model);

    MetaDataRemoveHandler metaDataRemover = new MetaDataRemoveHandler(true);
    metaDataRemover.handle(model);

    Path modifiedJar = model.getJarFile();

    boolean exists = Files.exists(modifiedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(modifiedJar);
    assertTrue(regularFile);
    boolean containedMetaInf = false;
    try (FileSystem zipFileSys = FileSystems.newFileSystem(modifiedJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      DirectoryStream<Path> paths = Files.newDirectoryStream(archiveRoot);
      for (Path p : paths) {
        System.out.println(p.toAbsolutePath());
        if (Files.isDirectory(p) && p.getFileName().toString().contains("META-INF")) {
          containedMetaInf = true;
        }
      }
    }
    assertFalse(containedMetaInf);

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

  @Test
  public void alwaysChangeSHAIfNoMetaDataIsPresent() throws IOException {

    GAV gav = new GAV("org.apache.commons", "commons-lang3", "3.9");
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel(gav);
    model.setChangeGAV(GAVModification.ORG);

    URL resource =
        MetaDataRemoveHandlerTest.class
            .getClassLoader()
            .getResource("commons-lang3-3.9_rm_metainf.jar");
    Path path = Paths.get(resource.getFile());

    model.setJarFile(path);

    Path downloadedJar = model.getJarFile();
    assertTrue(Files.isRegularFile(downloadedJar));

    System.out.println("Downloaded Jar: " + downloadedJar);

    GAVModifierHandler modifierHandler = new GAVModifierHandler();
    modifierHandler.handle(model);

    MetaDataRemoveHandler metaDataRemoveHandler = new MetaDataRemoveHandler(true);
    metaDataRemoveHandler.handle(model);

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
      assertFalse(exists1);
    }

    // the file does not contain a meta-inf folder

    boolean containedMetaInf = false;
    try (FileSystem zipFileSys = FileSystems.newFileSystem(modifiedJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      DirectoryStream<Path> paths = Files.newDirectoryStream(archiveRoot);
      for (Path p : paths) {
        System.out.println(p.toAbsolutePath());
        if (Files.isDirectory(p) && p.getFileName().toString().contains("META-INF")) {
          containedMetaInf = true;
        }
      }
    }
    assertFalse(containedMetaInf);

    System.out.println("Modified Jar: " + modifiedJar);

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
