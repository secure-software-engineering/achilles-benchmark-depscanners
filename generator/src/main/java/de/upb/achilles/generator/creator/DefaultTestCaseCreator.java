package de.upb.achilles.generator.creator;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.util.ArrayList;
import java.util.Collection;

/** @author Andreas Dann created on 07.01.19 */
public class DefaultTestCaseCreator extends TestCaseCreatorChain {

  private final ArrayList<TestFixtureModel> selectedWordModels;

  public DefaultTestCaseCreator(
      Collection<TestFixtureModel> selectedWordModels,
      String outputFolder,
      boolean installJARs,
      boolean removeMetaData) {
    super(installJARs, outputFolder, removeMetaData);
    this.selectedWordModels = new ArrayList<>(selectedWordModels);
    // the project gav
  }

  @Override
  public ArrayList<TestFixtureModel> getSelectedTestFixtures() {
    return selectedWordModels;
  }

  @Override
  protected boolean useUberGAV() {
    return false;
  }

  @Override
  protected boolean createUberJar() {
    return false;
  }

  @Override
  protected Collection<TestFixtureModel> getTestFixturesToInstall() {
    return selectedWordModels;
  }

  @Override
  protected void prepareTestCases() {}
}
