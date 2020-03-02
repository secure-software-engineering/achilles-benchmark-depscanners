package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.creator.Util;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecompileHandler extends OneTimeHandler {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RecompileHandler.class);

  private static final List<String> MAVEN_GOALS = Arrays.asList("clean", "compile", "package");

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return request.getByteCodeModification() == ByteCodeModification.FORCE_RECOMPILE;
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) throws JarModificationException {

    try {


      Path sourceJarFile = requTestFixtureModel.getJarFile();

      // unzip the source jar file
      Path tmpDir = Files.createTempDirectory("source_jar");

      Path srcDir = tmpDir.resolve(Paths.get("src", "main", "java"));
      Files.createDirectories(srcDir);

      // unzip the file into the source dir
      Util.unzip(sourceJarFile, srcDir);

      // download the pom file from Maven Central
      // get the pom
      Path pomFile =
          Objects.requireNonNull(sourceJarFile)
              .getParent()
              .resolve(Util.getPOMFileName(requTestFixtureModel.getOrgGav()));

      pomFile = Files.move(pomFile, tmpDir.resolve("pom.xml"));

      // compile the jar file

      InvocationRequest request = new DefaultInvocationRequest();
      //    Path pomFile =
      //        Paths.get(
      //            "META-INF",
      //            "maven",
      //            requTestFixtureModel.getOrgGav().getGroupId(),
      //            requTestFixtureModel.getOrgGav().getArtifactId(),
      //            "pom.xml");
      request.setBaseDirectory(tmpDir.toFile());

      if (!Files.exists(tmpDir.resolve(pomFile))) {
        throw new IOException("Could not find pom.xml in " + tmpDir.resolve(pomFile));
      }

      request.setGoals(RecompileHandler.MAVEN_GOALS);

      Invoker invoker = new DefaultInvoker();
      InvocationResult result;
      try {
        result = invoker.execute(request);
      } catch (MavenInvocationException e) {
        LOGGER.debug("Maven Compilation failed with ", e);
        throw new RuntimeException(e);
      }

      if (result.getExitCode() != 0) {
        throw new IllegalStateException("Build failed.");
      }

      // GET/find THE created jar file
      Path targetDir = tmpDir.resolve("target");
      String outputFileName = Util.getJARFileName(requTestFixtureModel.getOrgGav());
      Optional<Path> buildJarOptional =
          Files.walk(targetDir)
              .filter(p -> p.getFileName().toString().equals(outputFileName))
              .findFirst();
      Path compiledJarOutPut =
          buildJarOptional.orElseThrow(
              () -> new IOException("Could not build" + requTestFixtureModel.getOrgGav()));

      // set the new compiled jar file as test fixuture Jar fie
      requTestFixtureModel.setJarFile(compiledJarOutPut);

    } catch (IOException e) {
      throw new JarModificationException(
          "Failed to recompile " + requTestFixtureModel.getOrgGav(), e, requTestFixtureModel);
    }
  }

}
