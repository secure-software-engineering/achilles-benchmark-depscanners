package de.upb.achilles.generator.creator;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;

/** @author Andreas Dann created on 20.03.19 */
public class OneFrankenJarTestCaseCreatorTest {

  @Test
  public void mergeJARFilesIntoFrankenJAR() throws IOException {
    Collection<Path> files = new ArrayList<>();

    ClassLoader classLoader = getClass().getClassLoader();

    Path path = Paths.get(classLoader.getResource("1.zip").getFile());
    Path path1 = Paths.get(classLoader.getResource("2.zip").getFile());
    files.add(path);
    files.add(path1);

    Path tempDirWithPrefix = Files.createTempDirectory(null);

    Path frankenJar =
        new UberJarTestCaseCreator(
                Collections.emptyList(),
                tempDirWithPrefix.toAbsolutePath().toString(),
                false,
                false,
                false)
            .mergeJARFilesIntoFrankenJAR(files);
    System.out.println(frankenJar.toAbsolutePath());

    try (FileSystem zipFileSys = FileSystems.newFileSystem(frankenJar, null)) {
      final Path archiveRoot = zipFileSys.getPath("/");
      Path path2 = archiveRoot.resolve("1.txt");
      Path path3 = archiveRoot.resolve("2.txt");
      assertTrue(Files.exists(path2));
      assertTrue(Files.exists(path3));
    }
  }
}
