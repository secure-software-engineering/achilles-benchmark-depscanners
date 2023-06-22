package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JarTrimmerHandler extends Handler {

  static final Logger LOGGER = LoggerFactory.getLogger(JarTrimmerHandler.class);

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return request.testFixtureDetailsChanges();
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) throws JarModificationException {

    LOGGER.debug(String.format("Modify content of the JAR %s", requTestFixtureModel.getOrgGav()));

    this.modifyJar(requTestFixtureModel);

    // update the include relations in the test fixture
    requTestFixtureModel.updateIncludesAfterDelete();
  }

  protected abstract void modifyJar(TestFixtureModel requTestFixtureModel)
      throws JarModificationException;
}
