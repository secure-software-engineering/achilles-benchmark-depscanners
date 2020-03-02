package de.upb.achilles.generator.controller;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.SessionModel;
import de.upb.achilles.generator.model.TestFixture;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.model.TestFixtureModelParent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Andreas Dann created on 05.01.19 */
public class TableViewHandler {
  private static final int SCROLL_OFFSET = 4;

  private final TreeTableView<TestFixtureModel> testFixtureModelTreeTableView;

  private final SessionModel sessionModel;

  private final TreeTableView.TreeTableViewSelectionModel<TestFixtureModel> wordSelectionModel;

  private final Map<TestFixtureModel, TreeItem<TestFixtureModel>> testFixtureModelTreeItemMap =
      new HashMap<>();

  public TableViewHandler(
      final TreeTableView<TestFixtureModel> testFixtureModelTreeTableView,
      final SessionModel sessionModel) {
    this.testFixtureModelTreeTableView = testFixtureModelTreeTableView;
    this.sessionModel = sessionModel;

    wordSelectionModel = testFixtureModelTreeTableView.getSelectionModel();
    wordSelectionModel.setSelectionMode(SelectionMode.SINGLE);
    wordSelectionModel.setCellSelectionEnabled(false);
  }

  public void prepare() {
    testFixtureModelTreeTableView.setRoot(createTee(this.sessionModel.groupByGAV()));
    testFixtureModelTreeTableView.setShowRoot(false);
    selectWord(sessionModel.getCurrentWord());
  }

  public void selectNextWord() {
    wordSelectionModel.selectNext();
    int index = wordSelectionModel.getSelectedIndex();
    int scrollPosition = index - SCROLL_OFFSET;

    if (scrollPosition >= 0 && scrollPosition < sessionModel.getWordListSize()) {
      testFixtureModelTreeTableView.scrollTo(scrollPosition);
    }
  }

  public void selectWord(final TestFixtureModel word) {

    if (word == null) {
      return;
    }

    TreeItem<TestFixtureModel> obj = testFixtureModelTreeItemMap.get(word);

    obj.getParent().setExpanded(true);

    wordSelectionModel.select(obj);

    int parentRow = testFixtureModelTreeTableView.getRow(obj.getParent());
    testFixtureModelTreeTableView.scrollTo(parentRow);

    int elementRow = testFixtureModelTreeTableView.getRow(obj);
    wordSelectionModel.focus(elementRow);
  }

  public TreeItem<TestFixtureModel> createTee(Map<GAV, List<TestFixtureModel>> map) {

    // Creating the root element
    TestFixture dummyRoot =
        new TestFixture("", "dummy root fixture", new GAV("dummy", "root", "0"), false);
    TestFixtureModelParent dummyRootFixtureModel = new TestFixtureModelParent(dummyRoot);
    final TreeItem<TestFixtureModel> root = new TreeItem<>(dummyRootFixtureModel);
    root.setExpanded(true);

    for (Map.Entry<GAV, List<TestFixtureModel>> entry : map.entrySet()) {
      TestFixture dummyTestFixture = new TestFixture("", "grouping fixture", entry.getKey(), false);

      TestFixtureModelParent dummyTestFixtureModelParent =
          new TestFixtureModelParent(dummyTestFixture);

      TreeItem<TestFixtureModel> gavNode = new TreeItem<>(dummyTestFixtureModelParent);

      // add lister for gav change property
      dummyTestFixtureModelParent
          .changeGAVProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (oldValue != newValue) {
                  gavNode.getChildren().forEach(child -> child.getValue().setChangeGAV(newValue));
                }
              });

      // add lister for gav include property
      dummyTestFixtureModelParent
          .includeProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                gavNode.getChildren().forEach(child -> child.getValue().setInclude(newValue));
              });

      // add lister for gav bytecode modification property

      dummyTestFixtureModelParent
          .byteCodeModificationProperty()
          .addListener(
              (observable, oldValue, newValue) -> {
                if (oldValue != newValue) {
                  gavNode
                      .getChildren()
                      .forEach(child -> child.getValue().setByteCodeModification(newValue));
                }
              });

      // set the view for the dummy model

      for (TestFixtureModel testFixtureModel : entry.getValue()) {
        TreeItem<TestFixtureModel> child = new TreeItem<>(testFixtureModel);
        gavNode.getChildren().add(child);
        if (testFixtureModel.isVulnerable()) {
          dummyTestFixtureModelParent.setChildIsVulnerable(true);
        }
        if (testFixtureModel.isContainsCode()) {
          dummyTestFixtureModelParent.setChildContainsCode(true);
        }

        this.testFixtureModelTreeItemMap.put(testFixtureModel, child);

        // FIXME: clean up the mess witht 1000 listeners...
        // if a child is set to include set the parent as include
        testFixtureModel
            .includeProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  if (oldValue != newValue) {
                    dummyTestFixtureModelParent.setInclude(newValue);
                  }
                });

        testFixtureModel
            .changeGAVProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  if (oldValue != newValue) {
                    dummyTestFixtureModelParent.setChangeGAV(newValue);
                  }
                });

        testFixtureModel
            .byteCodeModificationProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  if (oldValue != newValue) {
                    dummyTestFixtureModelParent.setByteCodeModification(newValue);
                  }
                });
      }

      root.getChildren().add(gavNode);
    }
    return root;
  }
}
