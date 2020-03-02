package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.model.GAV;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** @author Andreas Dann created on 10.01.19 */
public class Util {

  private static final int BUFFER_SIZE = 4096;

  public static String getJARFileName(GAV gav) {
    return gav.getArtifactId() + "-" + gav.getVersion() + ".jar";
  }

  public static String getSourceJARFileName(GAV gav) {
    return gav.getArtifactId() + "-" + gav.getVersion() + "-sources.jar";
  }

  public static String getPOMFileName(GAV gav) {
    return gav.getArtifactId() + "-" + gav.getVersion() + ".pom";
  }

  public static String getClassName(final String qname) {
    String className = qname;
    if (className.contains("(")) {
      className = className.substring(0, className.indexOf("("));
      // cut of the method name
      className = className.substring(0, className.lastIndexOf("."));

    } else if (className.endsWith("INIT")) {
      className = className.substring(0, className.length() - ".INIT".length());
    } else if (className.endsWith("<clinit>")) {
      className = className.substring(0, className.length() - ".<clinit>".length());

    } else if (className.endsWith("<init>")) {
      className = className.substring(0, className.length() - ".<init>\"".length());
    }
    return className;
  }

  public static QNameType getType(final String qname) {
    if (qname.contains("(")) {
      return QNameType.METHOD;
    } else if (qname.endsWith("INIT")) {
      return QNameType.STATIC_INIT;
    }
    return QNameType.CLASS;
  }

  public static String getMethodName(String qname) {
    QNameType qNameType = Util.getType(qname);
    if (qNameType != QNameType.METHOD) {
      throw new IllegalArgumentException("FQN " + qname + " is not a method identifier");
    }
    String className = Util.getClassName(qname);
    String methodName = qname.substring(0, className.length());
    methodName = methodName.substring(0, methodName.indexOf("("));

    return methodName;
  }

  public static String getFileName(String qname) {
    String className = getClassName(qname);
    className = className.replace(".", "/");
    className += ".class";
    return className;
  }

  /**
   * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory
   * (will be created if does not exists)
   *
   * @param zipFilePath the path to the zipfile
   * @param destDirectory where to store the unzipped content
   * @throws IOException if unzip fails
   */
  public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
    if (!Files.exists(destDirectory)) {
      Files.createDirectory(destDirectory);
    }

    ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath));

    ZipEntry entry = zipIn.getNextEntry();
    // iterates over entries in the zip file
    while (entry != null) {
      String filePath = destDirectory + File.separator + entry.getName();
      if (!entry.isDirectory()) {
        // if the entry is a file, extracts it
        extractFile(zipIn, filePath);
      } else {
        // if the entry is a directory, make the directory
        File dir = new File(filePath);
        dir.mkdir();
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  /**
   * Extracts a zip entry (file or folder entry)
   *
   * @param zipIn the inputstream of the zip file
   * @param filePath where to store the file
   * @throws IOException if file creation files
   */
  private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {

    File parent = new File(filePath).getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }

    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  // Uses java.util.zip to create zip file
  public static void zipFolder(Path sourceFolderPath, Path zipPath) {
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
      Files.walkFileTree(
          sourceFolderPath,
          new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
              Files.copy(file, zos);
              zos.closeEntry();
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public enum QNameType {
    METHOD,
    CLASS,
    STATIC_INIT
  }
}
