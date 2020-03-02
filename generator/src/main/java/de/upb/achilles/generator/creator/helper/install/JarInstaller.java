package de.upb.achilles.generator.creator.helper.install;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** @author Andreas Dann created on 21.03.19 */
public class JarInstaller {

  public static void installJarIntoLocalRepo(
      TestFixtureModel testFixtureModel, Path jarFile, Path project)
      throws IOException, InterruptedException {
    installJarIntoLocalRepo(testFixtureModel.getGAV4Pom(), jarFile, project);
  }

  public static void installJarIntoLocalRepo(GAV gav, Path jarFile, Path project)
      throws IOException, InterruptedException {
    String[] mvnInstallCommand = mvnInstallCommand(gav, jarFile, project);

    ProcessBuilder processBuilder = new ProcessBuilder(mvnInstallCommand);
    Process start = processBuilder.start();
    start.waitFor();
  }

  private static String[] mvnInstallCommand(
      TestFixtureModel testFixtureModel, Path jarFile, Path project) {
    return mvnInstallCommand(testFixtureModel.getGAV4Pom(), jarFile, project);
  }

  public static String[] mvnInstallCommand(GAV gav, Path jarFile, Path projectPath) {
    String pathToJarFile = jarFile.toAbsolutePath().toString();
    if (projectPath != null) {
      // resolve the jar file relative to the project
      Path newPath = projectPath.resolve(jarFile.getFileName());

      if (!Files.exists(newPath)) {
        throw new IllegalArgumentException(
            String.format("Cannot find file %s in folder %s", jarFile, projectPath));
      }

      // create the command relative to the project
      pathToJarFile = projectPath.relativize(newPath).toString();
    }

    return new String[] {
      "mvn install:install-file",
      "-Dfile=" + pathToJarFile,
      "-DgroupId=" + gav.getGroupId(),
      "-DartifactId=" + gav.getArtifactId(),
      "-Dversion=" + gav.getVersion(),
      "-Dpackaging=" + "jar",
      "-DgeneratePom=" + true
    };
  }
}
