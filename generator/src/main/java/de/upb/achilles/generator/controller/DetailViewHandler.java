package de.upb.achilles.generator.controller;

import de.upb.achilles.generator.model.SessionModel;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import javafx.scene.control.TableView;

/** @author Andreas Dann created on 11.01.19 */
public class DetailViewHandler {

  private final TableView<TestFixtureDetailModel> textArea;
  private final SessionModel sessionModel;

  public DetailViewHandler(
      final TableView<TestFixtureDetailModel> textArea, final SessionModel sessionModel) {
    this.textArea = textArea;

    this.sessionModel = sessionModel;
  }

  public void prepare() {
    textArea.setItems(sessionModel.getTestFixtureDetailModels());
  }
}
