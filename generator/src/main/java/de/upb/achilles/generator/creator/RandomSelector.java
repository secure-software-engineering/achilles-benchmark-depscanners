package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.Main;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.SessionModel;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/** @author Andreas Dann created on 13.01.19 */
public class RandomSelector {

  private static final Random random = new Random();
  private static final int LOWERBOUND = 0;
  private static final Logger logger = LoggerFactory.getLogger(Main.class);
  private final SessionModel sessionModel;
  private final int upperBound;

  private final int numberOfSamples;

  public RandomSelector(SessionModel sessionModel) {

    this.sessionModel = sessionModel;

    upperBound = this.sessionModel.getWordListSize() - 1;

    // how much dependencies do we want to include
    this.numberOfSamples = random.nextInt(upperBound - LOWERBOUND) + LOWERBOUND;
    logger.info("Select Random Nr: " + numberOfSamples);
  }

  public RandomSelector(SessionModel sessionModel, int numberOfSamples) {

    this.sessionModel = sessionModel;
    upperBound = this.sessionModel.getWordListSize() - 1;

    // how much dependencies do we want to include
    this.numberOfSamples = numberOfSamples;
    logger.info("Select Random Nr: " + numberOfSamples);
  }

  public void setSessionModelRandomly() {

    // clear current selection
    for (TestFixtureModel testFixtureModel : sessionModel.getWordList()) {
      testFixtureModel.setInclude(false);
    }

    // first select the fixtures
    this.selectTestFixturesRandomly();

    for (TestFixtureModel testFixtureModel : this.sessionModel.getSelectedWords()) {
      setChangeGAVRandomly(testFixtureModel);
      for (TestFixtureDetailModel testFixtureDetailModel :
          testFixtureModel.getTestFixtureDetailModel()) {

        setTestFixtureDetailsRandomly(testFixtureDetailModel);
      }
    }
  }

  /** Select the testfixtures randomly based on the number of samples */
  private void selectTestFixturesRandomly() {
    for (int i = 0; i < numberOfSamples; i++) {

      GAV orgGav = getRandomTestFixtureModel().getOrgGav();

      for (TestFixtureModel testFixtureModel : sessionModel.groupByGAV().get(orgGav)) {
        testFixtureModel.setInclude(true);
      }
    }
  }

  private void setChangeGAVRandomly(TestFixtureModel testFixtureModel) {
    // choose random from enum
    int i = random.nextInt(GAVModification.values().length);

    GAVModification mod = GAVModification.values()[i];

    GAV gav = testFixtureModel.getOrgGav();

    for (TestFixtureModel tm : sessionModel.groupByGAV().get(gav)) {

      tm.changeGAVProperty().set(mod);
    }
  }

  /**
   * Select a single testfixture randomly
   *
   * @return revmoves a randomly selected TestFixture
   */
  private TestFixtureModel getRandomTestFixtureModel() {

    int random_integer = random.nextInt(upperBound - LOWERBOUND) + LOWERBOUND;

    return sessionModel.getWord(random_integer);
  }

  private void setTestFixtureDetailsRandomly(TestFixtureDetailModel testFixtureDetailModel) {
    if (testFixtureDetailModel.isContained()) {
      testFixtureDetailModel.includeProperty().set(random.nextBoolean());
    }
  }

  public void updateTreeView(TreeTableView<TestFixtureModel> treeTableView) {

    ObservableList<TreeItem<TestFixtureModel>> children = treeTableView.getRoot().getChildren();
    Set<GAV> selectedGavs =
        this.sessionModel.getSelectedWords().stream()
            .map(TestFixtureModel::getOrgGav)
            .collect(Collectors.toSet());

    for (TreeItem<TestFixtureModel> child : children) {
      if (selectedGavs.contains(child.getValue().getOrgGav())) {
        child.getValue().setInclude(true);
      }
    }
  }
}
