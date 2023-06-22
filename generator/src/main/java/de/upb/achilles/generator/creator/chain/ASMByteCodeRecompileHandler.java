package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.creator.Util;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.LoggerFactory;

/** Simulate re-compiling the jar by re-writing all class files with ASM */
public class ASMByteCodeRecompileHandler extends OneTimeHandler {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RecompileHandler.class);

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return request.getByteCodeModification() == ByteCodeModification.RECOMPILE;
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) throws JarModificationException {

    try {

      Path jarFile = requTestFixtureModel.getJarFile();

      // unzip the source jar file
      Path sourceDir = Files.createTempDirectory("jar_to_modify");

      Files.createDirectories(sourceDir);

      // unzip the file into the source dir
      Util.unzip(jarFile, sourceDir);

      //  modify the class files with  ASM bytecode framework

      Path targetDir = Files.createTempDirectory("modified_jar");

      Files.walkFileTree(sourceDir, new ASMModifier(sourceDir, targetDir));

      //  re-zip into jar file

      Path modifiedJarFile = Files.createTempDirectory("new_zip");
      String filename = Objects.requireNonNull(jarFile).getFileName().toString();
      modifiedJarFile = modifiedJarFile.resolve(filename);
      Util.zipFolder(targetDir, modifiedJarFile);

      // set the new compiled jar file as test fixuture Jar fie
      requTestFixtureModel.setJarFile(modifiedJarFile);

    } catch (IOException e) {
      throw new JarModificationException(
          "Failed to recompile " + requTestFixtureModel.getOrgGav(), e, requTestFixtureModel);
    }
  }

  private static class ASMModifier extends SimpleFileVisitor<Path> {
    private final Path sourceDir;
    private final Path targetDir;

    public ASMModifier(Path sourceDir, Path targetDir) {
      this.sourceDir = sourceDir;
      this.targetDir = targetDir;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

      try {
        Path targetFile = targetDir.resolve(sourceDir.relativize(file));
        // modify the class files
        if (file.getFileName().toString().endsWith(".class")) {
          InputStream inputStream = null;
          OutputStream outputStream = null;
          try {
            // rewrite the class
            inputStream = Files.newInputStream(file);

            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cr.accept(cw, 0);
            outputStream = Files.newOutputStream(targetFile);
            outputStream.write(cw.toByteArray());
          } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
          }
        } else {
          // simply copy all other class files...
          Files.copy(file, targetFile);
        }
      } catch (IOException ex) {
        LOGGER.error("Failed to copy file", ex);
      }

      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {
      if (dir == sourceDir) {
        // do not copy the top-level source folder
        return FileVisitResult.CONTINUE;
      }
      try {
        Path newDir = targetDir.resolve(sourceDir.relativize(dir));
        Files.createDirectory(newDir);
      } catch (IOException ex) {
        LOGGER.error("Failed to create directory", ex);
      }

      return FileVisitResult.CONTINUE;
    }
  }
}
