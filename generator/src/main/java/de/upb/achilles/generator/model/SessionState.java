package de.upb.achilles.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionState {

  private boolean install = false;
  private HashMap<Integer, TestFixtureModelState> selectTestFixturesStates = new HashMap<>();
  private boolean removeMetaData = false;

  public SessionState() {}

  /**
   * creates the state from the current session model
   *
   * @param sessionModel
   */
  public SessionState(SessionModel sessionModel) {
    this.initFromSessionModel(sessionModel);
  }

  public boolean isInstall() {
    return install;
  }

  public void setInstall(boolean install) {
    this.install = install;
  }

  public HashMap<Integer, TestFixtureModelState> getSelectTestFixturesStates() {
    return selectTestFixturesStates;
  }

  public void setSelectTestFixturesStates(
      HashMap<Integer, TestFixtureModelState> selectTestFixturesStates) {
    this.selectTestFixturesStates = selectTestFixturesStates;
  }

  public boolean isRemoveMetaData() {
    return removeMetaData;
  }

  public void setRemoveMetaData(boolean removeMetaData) {
    this.removeMetaData = removeMetaData;
  }

  private void initFromSessionModel(SessionModel sessionModel) {
    this.removeMetaData = sessionModel.getRemoveMetaData();
    this.install = sessionModel.isInstallJARs();
    for (TestFixtureModel testFixtureModel : sessionModel.getSelectedWords()) {
      TestFixtureModelState state = new TestFixtureModelState();
      state.changeGav = testFixtureModel.getChangeGAV();
      state.byteCodeModification = testFixtureModel.getByteCodeModification();
      ArrayList<String> includedQNames = new ArrayList<>();
      for (TestFixtureDetailModel detailModel : testFixtureModel.getTestFixtureDetailModel()) {
        if (detailModel.isInclude()) {
          includedQNames.add(detailModel.getQname());
        }
      }
      state.includedQnames = includedQNames;

      selectTestFixturesStates.put(testFixtureModel.getId(), state);
    }
  }

  /** @param sessionModel */
  public void restoreFromState(SessionModel sessionModel) {

    sessionModel.reset();
    // FIXME: performance
    for (TestFixtureModel testFixtureModel : sessionModel.getWordList()) {
      int testFixtureId = testFixtureModel.getId();

      TestFixtureModelState state = selectTestFixturesStates.get(testFixtureId);

      if (state != null) {
        testFixtureModel.includeProperty().set(true);
        testFixtureModel.changeGAVProperty().set(state.changeGav);
        testFixtureModel.byteCodeModificationProperty().set(state.byteCodeModification);

        for (TestFixtureDetailModel detailModel : testFixtureModel.getTestFixtureDetailModel()) {
          if (state.includedQnames.contains(detailModel.getQname())) {
            detailModel.includeProperty().set(true);
          } else {
            detailModel.includeProperty().set(false);
          }
        }
      }
    }

    sessionModel.installJARsProperty().set(this.install);
    sessionModel.removeMetaDataProperty().set(this.removeMetaData);
  }
}
