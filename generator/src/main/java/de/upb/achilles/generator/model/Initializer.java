package de.upb.achilles.generator.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** @author Andreas Dann created on 05.01.19 */
public class Initializer {

  public static final String ENVIRONMENT_PATH_TO_FIXTURES = "PATHFIX";
  private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

  public static List<TestFixtureModel> getTestFixtureModels() {
    Iterable<TestFixture> testFixtures = readInTestFixtures();
    ArrayList<TestFixtureModel> testFixtureModels = new ArrayList<>();
    for (TestFixture testFixture : testFixtures) {
      TestFixtureModel testFixtureDetailModel = new TestFixtureModel(testFixture);
      testFixtureModels.add(testFixtureDetailModel);
    }
    return testFixtureModels;
  }

  public static Iterable<TestFixture> readInTestFixtures() {
    String dir = "." + File.separator + "detection";
    String evnVar = System.getenv(ENVIRONMENT_PATH_TO_FIXTURES);

    if (evnVar != null && evnVar.length() != 0) {
      dir = evnVar;
    }

    return readInTestFixtures(dir);
  }

  private static Iterable<TestFixture> readInTestFixtures(String directory) {
    ArrayList<TestFixture> testFixtureArrayList = new ArrayList<>();

    ObjectMapper mapper = new ObjectMapper();

    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
      for (Path path : directoryStream) {
        // JSON from file to Object

        try {

          TestFixture testFixture = mapper.readValue(Files.newInputStream(path), TestFixture.class);
          testFixtureArrayList.add(testFixture);
        } catch (JsonParseException | JsonMappingException ex) {
          logger.error(
              "Failed to convert file {} to {} with {}.",
              path.getFileName().toString(),
              TestFixture.class,
              ex);
        }
      }
    } catch (IOException ex) {
      logger.error("Failed to open dir {} with {}.", directory, ex);
    }

    return testFixtureArrayList;
  }

  public static List<TestFixtureModel> dummyTestFixtures() {
    List<TestFixtureModel> dummyList = new ArrayList<>();

    TestFixtureDetail testFixtureDetail = new TestFixtureDetail("A.class", "A.method()", true);

    TestFixture testFixture =
        new TestFixture(
            "a", "n", new GAV("g", "a", "1"), true, new TestFixtureDetail[] {testFixtureDetail});

    dummyList.add(new TestFixtureModel(testFixture));


    TestFixture testFixtureA1 =
            new TestFixture(
                    "a1", "n", new GAV("g", "a", "1"), false, new TestFixtureDetail[] {testFixtureDetail});

    dummyList.add(new TestFixtureModel(testFixtureA1));

    TestFixture testFixture2 = new TestFixture("b", "n", new GAV("g", "a", "2"), false);
    dummyList.add(new TestFixtureModel(testFixture2));

    TestFixture testFixture3 = new TestFixture("c", "n", new GAV("g", "a", "2"), false);
    dummyList.add(new TestFixtureModel(testFixture3));

    TestFixtureDetail testFixtureDetail2 = new TestFixtureDetail("W.class", "W.method()", false);

    TestFixture testFixture4 =
        new TestFixture(
            "d", "n", new GAV("g", "a", "2"), false, new TestFixtureDetail[] {testFixtureDetail2});
    dummyList.add(new TestFixtureModel(testFixture4));

    TestFixture testFixture5 = new TestFixture("e", "n", new GAV("g", "a", "2"), false);
    dummyList.add(new TestFixtureModel(testFixture5));
    return dummyList;
  }
}
