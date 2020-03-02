package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ASMByteCodeRecompileHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.RECOMPILE);

    ASMByteCodeRecompileHandler recompileHandler = new ASMByteCodeRecompileHandler();
    assertTrue(recompileHandler.canHandle(model));
  }

  @Test
  public void canHandleNot() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setByteCodeModification(ByteCodeModification.ORG);

    ASMByteCodeRecompileHandler recompileHandler = new ASMByteCodeRecompileHandler();
    assertFalse(recompileHandler.canHandle(model));
  }

  @Test
  public void handle() throws IOException {

    GAV gav = new GAV("com.google.guava", "guava", "28.0-jre");
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel(gav);
    model.setByteCodeModification(ByteCodeModification.RECOMPILE);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    boolean exists = Files.exists(downloadedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(downloadedJar);
    assertTrue(regularFile);

    ASMByteCodeRecompileHandler recompileHandler = new ASMByteCodeRecompileHandler();

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

    // the files should be the same in both jars
    HashSet<String> filesInDownloadedJar = new HashSet<>();

    try (FileSystem zipFileSys = FileSystems.newFileSystem(downloadedJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");

      Files.walk(archiveRoot).forEach(p -> filesInDownloadedJar.add(p.toString()));
    }

    try (FileSystem zipFileSys = FileSystems.newFileSystem(recompiledJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      Files.walk(archiveRoot)
          .forEach(
              p -> {
                assertTrue(filesInDownloadedJar.contains(p.toString()));
                filesInDownloadedJar.remove(p.toString());
              });
    }

    assertTrue(filesInDownloadedJar.isEmpty());
  }

  @Test
  public void handle2() throws IOException {

    GAV gav = new GAV("commons-fileupload", "commons-fileupload", "1.2");
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel(gav);
    model.setByteCodeModification(ByteCodeModification.RECOMPILE);

    DownloadHandler downloadHandler = new DownloadHandler();
    downloadHandler.handle(model);

    Path downloadedJar = model.getJarFile();

    boolean exists = Files.exists(downloadedJar);
    assertTrue(exists);
    boolean regularFile = Files.isRegularFile(downloadedJar);
    assertTrue(regularFile);
    System.out.println("Downloaded Jar: " + downloadedJar);

    ASMByteCodeRecompileHandler recompileHandler = new ASMByteCodeRecompileHandler();
    recompileHandler.handle(model);

    Path recompiledJar = model.getJarFile();

    boolean recompiledExists = Files.exists(recompiledJar);
    assertTrue(recompiledExists);
    System.out.println("Recompiled Jar: " + recompiledJar);

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

    // the files should be the same in both jars
    HashSet<String> filesInDownloadedJar = new HashSet<>();

    try (FileSystem zipFileSys = FileSystems.newFileSystem(downloadedJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");

      Files.walk(archiveRoot).forEach(p -> filesInDownloadedJar.add(p.toString()));
    }

    try (FileSystem zipFileSys = FileSystems.newFileSystem(recompiledJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      Files.walk(archiveRoot)
          .forEach(
              p -> {
                assertTrue(filesInDownloadedJar.contains(p.toString()));
                filesInDownloadedJar.remove(p.toString());
              });
    }

    assertTrue(filesInDownloadedJar.isEmpty());
  }
}
