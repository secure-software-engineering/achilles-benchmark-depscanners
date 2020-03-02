package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.creator.Util;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteCodeModifierHandler extends JarTrimmerHandler {

  private static byte[] removeMethodFromClass(String qname, InputStream classInputStream) {
    String methodName = Util.getMethodName(qname);

    ClassReader cr;
    try { // use resource lookup to get the class bytes
      cr = new ClassReader(classInputStream);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
    // passing the ClassReader to the writer allows internal optimizations
    ClassWriter cw = new ClassWriter(cr, 0);
    cr.accept(new MethodRemover(cw, methodName), 0);

    return cw.toByteArray();
  }

  @Override
  protected void modifyJar(TestFixtureModel requTestFixtureModel) throws JarModificationException {
    this.modifyFilesInJar(requTestFixtureModel, requTestFixtureModel.getJarFile());
  }

  private void modifyFilesInJar(TestFixtureModel testFixtureModel, Path jarFile)
      throws JarModificationException {

    try (FileSystem zipfs = FileSystems.newFileSystem(jarFile, null)) {
      for (TestFixtureDetailModel detailModel : testFixtureModel.getTestFixtureDetailModel()) {

        if (!detailModel.isInclude()) {
          String fileName = Util.getFileName(detailModel.getQname());
          Path path = zipfs.getPath(fileName);
          Util.QNameType type = Util.getType(detailModel.getQname());
          switch (type) {
            case CLASS:
              Files.deleteIfExists(path);
              testFixtureModel.getFilesDeletedFromJar().add(detailModel.getFile());
              break;
            case METHOD:
              byte[] modifiedClassFile =
                  removeMethodFromClass(detailModel.getQname(), Files.newInputStream(path));
              Files.deleteIfExists(path);
              Files.write(path, modifiedClassFile);
              break;
            case STATIC_INIT:
              byte[] modifiedClassFile2 =
                  removeMethodFromClass("<clinit>", Files.newInputStream(path));
              Files.deleteIfExists(path);
              Files.write(path, modifiedClassFile2);
              break;
          }
        }
      }

    } catch (IOException e) {
      LOGGER.error("Cannot open/modify file", e);
      throw new JarModificationException("Cannot open/modify file", e, testFixtureModel);
    }
  }

  static class MethodRemover extends ClassVisitor {
    private final String hotMethodName;

    MethodRemover(ClassWriter cw, String methodName) {
      super(Opcodes.ASM7, cw);
      hotMethodName = methodName;
    }

    // invoked for every method
    @Override
    public MethodVisitor visitMethod(
        int access, String name, String desc, String signature, String[] exceptions) {

      if (!name.equals(hotMethodName))
        // reproduce the methods we're not interested in, unchanged
        return super.visitMethod(access, name, desc, signature, exceptions);

      // alter the behavior for the specific method
      return null;
    }
  }
}
