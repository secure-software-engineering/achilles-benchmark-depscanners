package de.upb.achilles.generator.creator.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/** @author Andreas Dann created on 07.01.19 */
public class TruthCreator {

  private final Iterable<TestFixtureModel> models;
  private final String truthFileFullPath;
  private final GAV projectGAV;
  public static final String TRUTH_JSON_FILE_NAME = "truth.json";

  public TruthCreator(GAV projectGAV, Iterable<TestFixtureModel> models, Path projectPath) {
    this.projectGAV = projectGAV;
    this.models = models;

    this.truthFileFullPath = projectPath.resolve(TRUTH_JSON_FILE_NAME).toAbsolutePath().toString();
  }

  public void createGroundTruth() throws IOException {
    Path file = Paths.get(truthFileFullPath);
    String truthString = this.createTruthAsString();
    Files.write(file, truthString.getBytes(), StandardOpenOption.CREATE);
  }

  protected String createTruthAsString() throws JsonProcessingException {

    return "{"
        + "\"project\":"
        + createProjectDescription(this.projectGAV)
        + ","
        + "\"dependencies\":"
        + createTestFixturesTruths(this.models)
        + "}";
  }

  private String createProjectDescription(GAV gav) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(gav);
  }

  protected String createTestFixturesTruths(Iterable<TestFixtureModel> testFixtureModels)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    CustomTestFixtureModelSerializer customTestFixtureModelSerializer =
        new CustomTestFixtureModelSerializer(TestFixtureModel.class);

    SimpleModule module =
        new SimpleModule(
            CustomTestFixtureModelSerializer.class.getName(),
            new Version(2, 1, 3, null, null, null));
    module.addSerializer(customTestFixtureModelSerializer);
    mapper.registerModule(module);

    // Object to JSON in String
    return mapper.writeValueAsString(testFixtureModels);
  }
}
