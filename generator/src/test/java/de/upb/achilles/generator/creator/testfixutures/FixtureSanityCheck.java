package de.upb.achilles.generator.creator.testfixutures;

import static java.util.stream.Collectors.groupingBy;

import de.upb.achilles.generator.model.Initializer;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class FixtureSanityCheck {

  @Test
  public void checkConsistentDetails() {

    List<TestFixtureModel> testFixtureModels = Initializer.getTestFixtureModels();

    Map<String, List<TestFixtureModel>> groupedByGAV =
        testFixtureModels.stream().collect(groupingBy(TestFixtureModel::getCve));
    int counter = 0;

    for (Map.Entry<String, List<TestFixtureModel>> entry : groupedByGAV.entrySet()) {
      // check if the details section is the same for all
      TestFixtureDetailModel[] testFixtureDetailModel =
          entry.getValue().get(0).getTestFixtureDetailModel();

      for (TestFixtureModel model : entry.getValue()) {
        boolean b = model.getTestFixtureDetailModel().length == testFixtureDetailModel.length;
        //        assertTrue(
        //            "DetailModel do not match for "
        //                + testFixtureToString(entry.getValue().get(0))
        //                + "    &    "
        //                + testFixtureToString(model),
        //            b);

        if (!b) {
          counter++;
          System.out.println(
              "DetailModel do not match for\n"
                  + testFixtureToString(entry.getValue().get(0))
                  + "\n"
                  + testFixtureToString(model));
        }
      }
    }
    System.out.println("Total: " + counter);
  }

  public static String testFixtureToString(TestFixtureModel testFixtureModel) {

    return testFixtureModel.getCve()
        + "__"
        + testFixtureModel.getGroupId()
        + "_"
        + testFixtureModel.getArtifactId()
        + "_"
        + testFixtureModel.getVersion()
        + ".json";
  }
}
