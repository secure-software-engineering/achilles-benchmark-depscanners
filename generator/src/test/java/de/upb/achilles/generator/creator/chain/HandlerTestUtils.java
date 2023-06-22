package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixture;
import de.upb.achilles.generator.model.TestFixtureDetail;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class HandlerTestUtils {

  public static TestFixtureModel getEmptyTestFixtureModel() {

    GAV randomGav = new GAV("org.apache.commons", "commons-lang3", "3.9");
    TestFixtureDetail testFixtureDetail =
        new TestFixtureDetail(
            "/org/apache/commons/lang3/AnnotationUtils.class",
            "org.apache.commons.lang3.AnnotationUtils",
            true);
    TestFixtureDetail models[] = new TestFixtureDetail[] {testFixtureDetail};

    TestFixture testFixture = new TestFixture("dummyCVE", "dummyCVE", randomGav, true, models);

    TestFixtureModel testFixtureModel = new TestFixtureModel(testFixture);

    return testFixtureModel;
  }

  public static TestFixtureModel getEmptyTestFixtureModel(GAV gav) {

    TestFixtureDetail testFixtureDetail =
        new TestFixtureDetail(
            "/org/apache/commons/lang3/AnnotationUtils.class",
            "org.apache.commons.lang3.AnnotationUtils",
            true);
    TestFixtureDetail models[] = new TestFixtureDetail[] {testFixtureDetail};

    TestFixture testFixture = new TestFixture("dummyCVE", "dummyCVE", gav, true, models);

    TestFixtureModel testFixtureModel = new TestFixtureModel(testFixture);

    return testFixtureModel;
  }

  public static TestFixtureModel getTestFixtureWithFile() throws IOException {
    ClassLoader classLoader = HandlerTestUtils.class.getClassLoader();

    Path jarFile = Paths.get(classLoader.getResource("soot.jar").getFile());

    Path targetFile = Files.createTempDirectory("dummy").resolve(jarFile.getFileName());
    Files.copy(jarFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
    TestFixtureModel testFixtureModel = getEmptyTestFixtureModel();
    testFixtureModel.setJarFile(targetFile);

    return testFixtureModel;
  }
}
