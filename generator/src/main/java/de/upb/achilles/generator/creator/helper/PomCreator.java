package de.upb.achilles.generator.creator.helper;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Andreas Dann created on 07.01.19 */
public class PomCreator {

  private static final Logger logger = LoggerFactory.getLogger(PomCreator.class);
  private final Iterable<TestFixtureModel> models;
  private final String pomFileFullPath;
  private final HashSet<GAV> doneGAVs = new HashSet<>();
  private final GAV projectGAV;

  public PomCreator(GAV projectGAV, Iterable<TestFixtureModel> models, Path projectPath) {
    this.projectGAV = projectGAV;
    this.models = models;
    this.pomFileFullPath = projectPath.resolve("pom.xml").toAbsolutePath().toString();
  }

  public void createPomXml() throws IOException {
    Path file = Paths.get(pomFileFullPath);
    String pomTest = this.createPomAsString();
    Files.write(file, pomTest.getBytes(), StandardOpenOption.CREATE);
  }

  protected String createPomAsString() {

    return ("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "  xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n"
            + "                      http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n\n")
        + "<modelVersion>4.0.0</modelVersion>\n"
        + createGAVInfo(this.projectGAV)
        + "\n\n"
        + createDependencies(this.models)
        + "</project>\n";
  }

  private String createDependencies(Iterable<TestFixtureModel> testFixtureModels) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<dependencies>\n");

    for (TestFixtureModel fixture : testFixtureModels) {
      stringBuilder.append(createSingleDependencyEntry(fixture));
    }
    stringBuilder.append("</dependencies>\n");
    return stringBuilder.toString();
  }

  private String createSingleDependencyEntry(TestFixtureModel testFixtureModel) {
    GAV gav = testFixtureModel.getGAV4Pom();
    if (this.doneGAVs.contains(gav)) {
      logger.warn(
          String.format("The pom.xml %s already contains the gav %s", this.pomFileFullPath, gav));
      return "\n";
    }
    this.doneGAVs.add(gav);

    return "<dependency>\n" + createGAVInfo(testFixtureModel) + "</dependency>\n";
  }

  private String createGAVInfo(TestFixtureModel testFixtureModel) {
    return createGAVInfo(testFixtureModel.getGAV4Pom());
  }

  protected String createGAVInfo(GAV gav) {

    return createGAVInfo(gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
  }

  private String createGAVInfo(String group, String artifact, String version) {
    return "<groupId>"
        + group
        + "</groupId>\n"
        + "<artifactId>"
        + artifact
        + "</artifactId>\n"
        + "<version>"
        + version
        + "</version>\n";
  }
}
