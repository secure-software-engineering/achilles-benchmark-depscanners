package de.upb.achilles.generator.model;

/*
 * Open Source Software published under the Apache Licence, Version 2.0.
 */

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.groupingBy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

public final class SessionModel {

  private final List<TestFixtureModel> allWords;
  private final ObservableList<TestFixtureModel> wordList;
  private final SimpleObjectProperty<TestFixtureModel> currentWord;
  private final SimpleObjectProperty<TestFixtureDetail[]> currentWordDetails;
  private final ObservableList<TestFixtureDetailModel> testFixtureDetailModels;

  // the Properties from JavaFX
  private final SimpleBooleanProperty editable = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty searchOpen = new SimpleBooleanProperty(true);
  private final SimpleBooleanProperty installJARs = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty oneFrankenJar = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty removeMetaData = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty keepTime = new SimpleBooleanProperty(false);

  public boolean isKeepTime() {
    return keepTime.get();
  }

  public SimpleBooleanProperty keepTimeProperty() {
    return keepTime;
  }

  public void setKeepTime(boolean keepTime) {
    this.keepTime.set(keepTime);
  }

  private final FilteredList<TestFixtureModel> selectedWords;
  private TreeItem<TestFixtureDetailModel> root;

  private Map<GAV, List<TestFixtureModel>> groupedByGAV;

  private final DetailModelChangeListener listener = new DetailModelChangeListener();

  public SessionModel(final List<TestFixtureModel> words) {
    allWords = words;
    wordList = FXCollections.observableArrayList(allWords);

    ObservableList<TestFixtureModel> testFixtureModels =
        FXCollections.observableArrayList(TestFixtureModel.INCLUDE_PROPERTY_CALLBACK);
    testFixtureModels.setAll(allWords);
    //noinspection Convert2Diamond
    selectedWords = new FilteredList<TestFixtureModel>(testFixtureModels);
    selectedWords.setPredicate(TestFixtureModel::isInclude);

    if (!words.isEmpty()) {

      // updateWordList(true, new MarkTool<>(words));
      currentWord = new SimpleObjectProperty<>(null);

      currentWordDetails = new SimpleObjectProperty<>(null);

      testFixtureDetailModels = FXCollections.observableArrayList(Collections.emptyList());
    } else {
      currentWord = new SimpleObjectProperty<>(null);

      currentWordDetails = new SimpleObjectProperty<>(null);

      testFixtureDetailModels = FXCollections.observableArrayList(Collections.emptyList());
    }

    for (TestFixtureModel testFixtureModel : words) {
      if (testFixtureModel.getTestFixtureDetailModel().length > 0) {
        testFixtureModel.addAdListener(listener);
      }
    }
  }

  public void reset() {
    editable.set(false);
    installJARs.set(false);
    oneFrankenJar.set(false);
    for (TestFixtureModel testFixtureModel : this.getWordList()) {
      testFixtureModel.includeProperty().set(false);
    }
  }

  public boolean getRemoveMetaData() {
    return removeMetaData.get();
  }

  public void setRemoveMetaData(boolean removeMetaData) {
    this.removeMetaData.set(removeMetaData);
  }

  public SimpleBooleanProperty removeMetaDataProperty() {
    return removeMetaData;
  }

  public ObservableList<TestFixtureDetailModel> getTestFixtureDetailModels() {
    return testFixtureDetailModels;
  }

  public void addSelectedWord(final TestFixtureModel word) {
    selectedWords.add(word);
  }

  public void removeDeselectedWord(final TestFixtureModel word) {
    selectedWords.remove(word);
  }

  public boolean isInstallJARs() {
    return installJARs.get();
  }

  public void setInstallJARs(boolean installJARs) {
    this.installJARs.set(installJARs);
  }

  public SimpleBooleanProperty installJARsProperty() {
    return installJARs;
  }

  public boolean isOneFrankenJar() {
    return oneFrankenJar.get();
  }

  public void setOneFrankenJar(boolean oneFrankenJar) {
    this.oneFrankenJar.set(oneFrankenJar);
  }

  public SimpleBooleanProperty oneFrankenJarProperty() {
    return oneFrankenJar;
  }

  public ObservableList<TestFixtureModel> getSelectedWords() {
    return selectedWords;
  }

  public boolean isSelected(final int index) {
    TestFixtureModel word = allWords.get(index);

    return selectedWords.contains(word);
  }

  public TestFixtureModel getWord(final int index) {
    return allWords.get(index);
  }

  public SimpleObjectProperty<TestFixtureModel> currentWordProperty() {
    return currentWord;
  }

  public SimpleBooleanProperty editableProperty() {
    return editable;
  }

  public SimpleBooleanProperty searchOpenProperty() {
    return searchOpen;
  }

  public boolean isSearchOpen() {
    return searchOpen.get();
  }

  public void setSearchOpen(final boolean isSearchOpen) {
    searchOpen.set(isSearchOpen);
  }

  public boolean isEditable() {
    return editable.get();
  }

  public TestFixtureModel getCurrentWord() {
    return currentWord.get();
  }

  public ObservableList<TestFixtureModel> getWordList() {
    return wordList;
  }

  public int getWordListSize() {
    return wordList.size();
  }

  public List<TestFixtureModel> getAllWords() {
    return unmodifiableList(allWords);
  }

  public int getAllWordsSize() {
    return allWords.size();
  }

  public Map<GAV, List<TestFixtureModel>> groupByGAV() {
    if (groupedByGAV == null) {
      groupedByGAV = this.wordList.stream().collect(groupingBy(TestFixtureModel::getOrgGav));
    }
    return groupedByGAV;
  }

  // FIXME: make this nice in the future
  private class DetailModelChangeListener implements DetailModelListener {
    @Override
    public void advertisement(DetailChangeEvent e) {

      Map<GAV, List<TestFixtureModel>> val = SessionModel.this.groupByGAV();

      Object source = e.getSource();

      if (source instanceof TestFixtureModel) {
        GAV gav = ((TestFixtureModel) source).getOrgGav();
        List<TestFixtureModel> testFixtureModels = val.get(gav);
        for (TestFixtureModel t : testFixtureModels) {
          if (source == t) {
            // do not inform myself
            continue;
          }
          t.makeDetailSelectionConsistent(null, e.getFileName(), e.isNewIncludeValue());
        }
      }
    }
  }
}
