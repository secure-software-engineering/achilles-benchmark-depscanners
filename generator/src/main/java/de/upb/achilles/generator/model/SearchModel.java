package de.upb.achilles.generator.model;

/*
 * Open Source Software published under the Apache Licence, Version 2.0.
 */

import de.upb.achilles.generator.searcher.SearchResult;
import de.upb.achilles.generator.searcher.Searcher;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.function.Function;
import java.util.function.Predicate;

public class SearchModel<T extends TestFixtureModel> {
  private final Searcher<T> searcher;

  private final StringProperty searchField;

  private final ObjectProperty<T> currentWord;

  private final ObservableList<T> wordList;

  private final StringProperty matchDescription = new SimpleStringProperty();

  private final ObjectProperty<T> previousMatch = new SimpleObjectProperty<>();

  private final ObjectProperty<T> nextMatch = new SimpleObjectProperty<>();

  private final ObjectProperty<T> wrapMatch = new SimpleObjectProperty<>();

  private final BooleanProperty previousButtonDisabled = new SimpleBooleanProperty();

  private final BooleanProperty nextButtonDisabled = new SimpleBooleanProperty();

  private final BooleanProperty searchFail = new SimpleBooleanProperty();

  public SearchModel(
      final Function<String, Predicate<TestFixtureModel>> matchMaker,
      final StringProperty searchField,
      final ObjectProperty<T> currentWord,
      final ObservableList<T> wordList) {
    this.searcher = new Searcher<>(matchMaker);
    this.searchField = searchField;
    this.currentWord = currentWord;
    this.wordList = wordList;
  }

  public void resetValues() {
    updateValues(new SearchResult<>());
  }

  public void updateValues() {
    SearchResult<T> result = searcher.buildResult(wordList, currentWord.get(), searchField.get());

    updateValues(result);
  }

  private void updateValues(final SearchResult<T> result) {
    matchDescription.set(result.getMatchDescription());
    previousMatch.set(result.getPreviousMatch());
    previousButtonDisabled.set(result.getPreviousMatch() == null);
    nextMatch.set(result.getNextMatch());
    wrapMatch.set(result.getWrapMatch());
    nextButtonDisabled.set(result.getNextMatch() == null);
    searchFail.set(result.isSearchFail());
  }

  public StringProperty matchDescriptionProperty() {
    return matchDescription;
  }

  public ObjectProperty<T> previousMatchProperty() {
    return previousMatch;
  }

  public ObjectProperty<T> nextMatchProperty() {
    return nextMatch;
  }

  public ObjectProperty<T> wrapMatchProperty() {
    return wrapMatch;
  }

  public BooleanProperty previousButtonDisabledProperty() {
    return previousButtonDisabled;
  }

  public BooleanProperty nextButtonDisabledProperty() {
    return nextButtonDisabled;
  }

  public BooleanProperty searchFailProperty() {
    return searchFail;
  }
}
