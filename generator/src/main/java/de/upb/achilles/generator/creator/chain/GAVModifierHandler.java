package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;
import java.util.HashMap;
import java.util.Random;

/** Sets the GAV for the dependency */
public class GAVModifierHandler extends Handler {

  private static final Random random = new Random();

  private static final String[] keyword = new String[] {"fix", "patch", "update"};

  private static final int MINIMUM = 1;
  private final HashMap<GAV, TestFixtureModel> modifiedGAVs = new HashMap<>();

  private static String getSlightModification() {
    String slightModify = keyword[random.nextInt(keyword.length)];
    slightModify += random.nextInt(Integer.MAX_VALUE - MINIMUM) + MINIMUM;
    return slightModify;
  }

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return !(request.getChangeGAV() == GAVModification.ORG);
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) {

    GAV testFixGav = requTestFixtureModel.getOrgGav();
    if (modifiedGAVs.containsKey(testFixGav)) {
      // share the modified GAV for all fixtures affecting the same dependency
      GAV modGAV = modifiedGAVs.get(testFixGav).getGAV4Pom();
      requTestFixtureModel.setGAV4Pom(modGAV);
      return;
    }

    GAV newGavForFixture = null;
    switch (requTestFixtureModel.getChangeGAV()) {
      case ORG:
        newGavForFixture = requTestFixtureModel.getOrgGav();
        break;
      case MOD:
        // slightly modified GAV, with fix or update or patched
        String group = requTestFixtureModel.getOrgGav().getGroupId();
        String artifactId = requTestFixtureModel.getOrgGav().getArtifactId();
        String version =
            requTestFixtureModel.getOrgGav().getVersion() + "_" + getSlightModification();
        newGavForFixture = new GAV(group, artifactId, version);
        break;
      case RANDOM:
        newGavForFixture = RandomGavCreator.getRandomGave();
        break;
    }
    requTestFixtureModel.setGAV4Pom(newGavForFixture);
    modifiedGAVs.put(testFixGav, requTestFixtureModel);
  }
}
