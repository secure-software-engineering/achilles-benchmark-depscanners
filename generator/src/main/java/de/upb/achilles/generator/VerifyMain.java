package de.upb.achilles.generator;

import java.io.FileInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class VerifyMain {

  @SuppressWarnings("resource")
  public static void getCrunchifyClassNamesFromJar(String crunchifyJarName) {
    try {
      JarInputStream jarInputStream = new JarInputStream(new FileInputStream(crunchifyJarName));
      JarEntry jarEntry;

      while (true) {
        jarEntry = jarInputStream.getNextJarEntry();
        if (jarEntry == null) {
          break;
        }
        if ((jarEntry.getName().endsWith(".class"))) {
          String className = jarEntry.getName().replaceAll("/", "\\.");
          String myClass = className.substring(0, className.lastIndexOf('.'));
        }
      }
    } catch (Exception e) {
      System.out.println("Oops.. Encounter an issue while parsing jar" + e.toString());
    }
  }

  public static void main(String[] args) {

    getCrunchifyClassNamesFromJar(args[0]);
  }
}
