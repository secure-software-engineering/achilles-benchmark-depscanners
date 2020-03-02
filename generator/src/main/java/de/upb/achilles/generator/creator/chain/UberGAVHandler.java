package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;

public class UberGAVHandler extends Handler {

  private final boolean useSameGAVForAllFixtures;
  private final GAV uberGAV = RandomGavCreator.getRandomGave();

  public UberGAVHandler(boolean useOneGAVForAllFixtures) {
    this.useSameGAVForAllFixtures = useOneGAVForAllFixtures;
  }

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return useSameGAVForAllFixtures;
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) {
    if (!useSameGAVForAllFixtures) {
      return;
    }

    requTestFixtureModel.setGAV4Pom(uberGAV);
  }
}
