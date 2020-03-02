package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.creator.chain.HandlerTestUtils;
import de.upb.achilles.generator.creator.helper.TruthCreator;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;




public class DefaultTestCaseCreatorTest {

  @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();


  /** Test to check the complete chain */
  @Test
  public void createTestCase() throws IOException, InterruptedException {
    List<TestFixtureModel> testFixtureModelList = new ArrayList<>();

    // add test fixtures...

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.RANDOM);
    model.setByteCodeModification(ByteCodeModification.RECOMPILE);
    model.setByteCodeModification(ByteCodeModification.REPACKAGE);

    testFixtureModelList.add(model);

    Path tempDirectory = Files.createTempDirectory(null);

    System.out.println(tempDirectory.toAbsolutePath().toString());

    assertTrue(Files.isDirectory(tempDirectory));

    DefaultTestCaseCreator testCaseCreator =
        new DefaultTestCaseCreator(
            testFixtureModelList, tempDirectory.toAbsolutePath().toString(), false, false);


    Thread thread = new Thread(testCaseCreator);
    thread.start();
    thread.join();


    // pom exists
    Path pomFile = tempDirectory.resolve("pom.xml");
    assertTrue(Files.exists(pomFile));
    assertTrue(Files.isRegularFile(pomFile));

    Path truthFile = tempDirectory.resolve(TruthCreator.TRUTH_JSON_FILE_NAME);
    assertTrue(Files.isRegularFile(truthFile));
  }
}
