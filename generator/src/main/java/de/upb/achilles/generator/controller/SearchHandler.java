package de.upb.achilles.generator.controller;

/*
 * Open Source Software published under the Apache Licence, Version 2.0.
 */

import de.upb.achilles.generator.model.SearchModel;
import de.upb.achilles.generator.model.SessionModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.searcher.SearchFieldClassTool;
import de.upb.achilles.generator.searcher.SearchTool;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;

public class SearchHandler {

  private final SessionModel model;

  private final TableViewHandler wordListHandler;

  private final ToolBar barSearch;

  private final CustomTextField fieldSearch;

  private final Label labelMatches;

  private final Button buttonCloseSearch;

  private final Button buttonSearchUp;

  private final Button buttonSearchDown;

  private SearchModel<TestFixtureModel> searchModel;

  public SearchHandler(
      final SessionModel model,
      final TableViewHandler wordListHandler,
      final ToolBar barSearch,
      final CustomTextField fieldSearch,
      final Label labelMatches,
      final Button buttonCloseSearch,
      final Button buttonSearchUp,
      final Button buttonSearchDown) {
    this.model = model;
    this.wordListHandler = wordListHandler;
    this.barSearch = barSearch;
    this.fieldSearch = fieldSearch;
    this.labelMatches = labelMatches;
    this.buttonCloseSearch = buttonCloseSearch;
    this.buttonSearchUp = buttonSearchUp;
    this.buttonSearchDown = buttonSearchDown;
  }

  public void prepare() {
    searchModel =
        new SearchModel<>(
            SearchTool::matchMaker,
            fieldSearch.textProperty(),
            model.currentWordProperty(),
            model.getWordList());
    labelMatches.textProperty().bind(searchModel.matchDescriptionProperty());
    fieldSearch.textProperty().addListener((o, old, v) -> processTextUpdate(v));

    buttonCloseSearch.setOnAction(e -> closeSearch());
    buttonSearchUp.setOnAction(e -> selectWord(searchModel.previousMatchProperty()));
    buttonSearchDown.setOnAction(e -> selectWord(searchModel.nextMatchProperty()));

    barSearch.visibleProperty().bindBidirectional(model.searchOpenProperty());
    barSearch.managedProperty().bindBidirectional(model.searchOpenProperty());

    buttonSearchUp.setDisable(true);
    buttonSearchDown.setDisable(true);
    searchModel
        .previousButtonDisabledProperty()
        .addListener((o, n, v) -> buttonSearchUp.setDisable(v));
    searchModel
        .nextButtonDisabledProperty()
        .addListener((o, n, v) -> buttonSearchDown.setDisable(v));

    searchModel
        .searchFailProperty()
        .addListener((o, n, v) -> SearchFieldClassTool.updateStateClass(fieldSearch, v));

    fieldSearch.textProperty().addListener((o, n, v) -> updateIfRequired());
    fieldSearch.setOnKeyPressed(this::processSearchKeyPress);
    model.currentWordProperty().addListener((o, n, v) -> updateIfRequired());
    model.getWordList().addListener((ListChangeListener<TestFixtureModel>) c -> updateIfRequired());

    searchModel.resetValues();
  }

  private void processSearchKeyPress(final KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      selectWord(searchModel.wrapMatchProperty());
    }
  }

  private void updateIfRequired() {
    if (model.isSearchOpen()) {
      searchModel.updateValues();
    }
  }

  private void selectWord(final ObjectProperty<TestFixtureModel> property) {
    TestFixtureModel word = property.get();

    if (word != null) {
      wordListHandler.selectWord(word);
    }
  }

  private void processTextUpdate(final String value) {
    if (StringUtils.isNotBlank(value)) {
      Predicate<TestFixtureModel> matcher = SearchTool.matchMaker(value);

      model.getWordList().stream()
          .filter(matcher)
          .findFirst()
          .ifPresent(wordListHandler::selectWord);
    }
  }

  public void openSearch() {
    model.setSearchOpen(true);
  }

  public void closeSearch() {
    fieldSearch.setText("");
    searchModel.resetValues();
    model.setSearchOpen(true);
  }

  public void processKeyPress(final KeyEvent event) {
    if (KeyCode.ESCAPE.equals(event.getCode())) {
      event.consume();
      closeSearch();
    }
  }
}
