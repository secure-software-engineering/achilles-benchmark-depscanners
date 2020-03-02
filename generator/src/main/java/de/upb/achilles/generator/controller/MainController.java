package de.upb.achilles.generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.upb.achilles.generator.creator.DefaultTestCaseCreator;
import de.upb.achilles.generator.creator.RandomSelector;
import de.upb.achilles.generator.creator.TestCaseCreatorChain;
import de.upb.achilles.generator.creator.UberJarTestCaseCreator;
import de.upb.achilles.generator.model.Initializer;
import de.upb.achilles.generator.model.SessionModel;
import de.upb.achilles.generator.model.SessionState;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeTableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/** @author Andreas Dann created on 05.01.19 */
public class MainController {

  @FXML private TreeTableView<TestFixtureModel> treeTableView;

  @FXML private ToolBar barSearch;

  @FXML private CustomTextField fieldSearch;

  @FXML private Label labelMatches;

  @FXML private Button buttonCloseSearch;

  @FXML private Button buttonSearchUp;

  @FXML private Button buttonSearchDown;

  @FXML private Button buttonSave;

  @FXML private Button buttonRandom;

  @FXML private TableView<TestFixtureDetailModel> detailTableView;

  @FXML private CheckMenuItem uberjarMenu;

  @FXML private CheckMenuItem checkBoxInstallJAR;

  @FXML private CheckMenuItem removeMetaDataMenu;

  @FXML private CheckBox uberjarcheckbox;

  @FXML private CheckBox removeMetaDataCheckBox;
  @FXML private CheckBox keepTimecheckbox;

  private SessionModel sessionModel;

  private TableViewHandler wordListHandler;

  private SearchHandler searchHandler;

  private SessionActions sessionActions;
  private DetailViewHandler detailTextAreaHandler;

  @FXML private ProgressBar progressBar;

  public void initialize() {
    this.initialise(new SessionModel(Initializer.getTestFixtureModels()));
  }

  private void initialise(final SessionModel sessionModel) {
    this.sessionModel = sessionModel;
    this.progressBar.setDisable(true);
    treeTableView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((o, old, word) -> updateCurrentTestFixtureProperty(word.getValue()));

    prepareFixtureListHandler();

    prepareSearchBar();

    prepareDetailTextArea();

    prepareButtons();

    sessionActions = new SessionActions(searchHandler::processKeyPress, searchHandler::openSearch);

    // bind  the checkbox to the session model's properties
    uberjarcheckbox.selectedProperty().bindBidirectional(sessionModel.oneFrankenJarProperty());
    checkBoxInstallJAR.selectedProperty().bindBidirectional(sessionModel.installJARsProperty());

    removeMetaDataMenu.selectedProperty().bindBidirectional(sessionModel.removeMetaDataProperty());

    removeMetaDataCheckBox
        .selectedProperty()
        .bindBidirectional(sessionModel.removeMetaDataProperty());

    uberjarMenu.selectedProperty().bindBidirectional(sessionModel.oneFrankenJarProperty());

    keepTimecheckbox.selectedProperty().bindBidirectional(sessionModel.keepTimeProperty());
  }

  private void prepareButtons() {
    buttonSave.setOnAction(this::createTestCase);

    buttonRandom.setOnAction(
        event -> {
          TextInputDialog alert = new TextInputDialog("0");
          alert.setTitle("Random Generation Dialog");
          alert.setHeaderText("Generate Test Fixtures Randomly");
          alert.setContentText("Numbers of Fixtures to include:");

          // force the field to be numeric only
          alert
              .getEditor()
              .textProperty()
              .addListener(
                  (observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                      alert.getEditor().setText(newValue.replaceAll("[^\\d]", "0"));

                    } else if (!newValue.isEmpty()) {
                      // do nothing
                      int enteredValue = Integer.valueOf(newValue);
                      if (enteredValue < 0) {
                        alert.getEditor().setText("0");
                      } else if (enteredValue > sessionModel.getWordListSize()) {
                        alert.getEditor().setText(sessionModel.getWordListSize() + "");
                      }
                    }
                  });

          Optional<String> result = alert.showAndWait();
          if (result.isPresent()) {
            int numberOfSamples = 0;
            if (!result.get().isEmpty()) {
              numberOfSamples = Integer.valueOf(result.get());
            }

            // ... user chose OK
            RandomSelector randomSelector = new RandomSelector(this.sessionModel, numberOfSamples);
            randomSelector.setSessionModelRandomly();
            randomSelector.updateTreeView(this.treeTableView);

          } else {
            // ... user chose CANCEL or closed the dialog
          }
        });
  }

  private void prepareDetailTextArea() {

    detailTextAreaHandler = new DetailViewHandler(this.detailTableView, sessionModel);
    detailTextAreaHandler.prepare();
  }

  private void prepareFixtureListHandler() {
    wordListHandler = new TableViewHandler(treeTableView, sessionModel);
    wordListHandler.prepare();
  }

  private void prepareSearchBar() {
    searchHandler =
        new SearchHandler(
            sessionModel,
            wordListHandler,
            barSearch,
            fieldSearch,
            labelMatches,
            buttonCloseSearch,
            buttonSearchUp,
            buttonSearchDown);
    searchHandler.prepare();
  }

  public SessionActions getSessionActions() {
    return sessionActions;
  }

  private void updateCurrentTestFixtureProperty(final TestFixtureModel word) {
    if (word != null) {
      sessionModel.currentWordProperty().set(word);
      sessionModel
          .getTestFixtureDetailModels()
          .setAll(sessionModel.getCurrentWord().getTestFixtureDetailModel());
    }
  }

  public void saveState(ActionEvent actionEvent) {

    // Set extension filter for text files
    // Show save file dialog
    FileChooser fileChooser = new FileChooser();

    // Set extension filter for text files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
    fileChooser.getExtensionFilters().add(extFilter);

    fileChooser.setTitle("Save TestCase Configuration...");

    // Show save file dialog
    File file = fileChooser.showSaveDialog(treeTableView.getScene().getWindow());
    if (file != null) {
      try {
        // create the state model
        SessionState state = new SessionState(this.sessionModel);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, state);

      } catch (IOException e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText(e.getLocalizedMessage());
        alert.showAndWait();
      }
    }
  }

  public void loadState(ActionEvent actionEvent) {

    // Set extension filter for text files
    // Show save file dialog
    FileChooser fileChooser = new FileChooser();

    // Set extension filter for text files
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
    fileChooser.getExtensionFilters().add(extFilter);

    fileChooser.setTitle("Load TestCase Configuration...");

    // Show save file dialog
    File file = fileChooser.showOpenDialog(treeTableView.getScene().getWindow());
    if (file != null) {
      try {
        // load the state model

        ObjectMapper mapper = new ObjectMapper();
        SessionState state = mapper.readValue(file, SessionState.class);
        state.restoreFromState(sessionModel);

      } catch (IOException e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText(e.getLocalizedMessage());
        alert.showAndWait();
      }
    }
  }

  public void createTestCase(ActionEvent actionEvent) {
    progressBar.progressProperty().unbind();
    progressBar.setDisable(false);

    // Set extension filter for text files
    // Show save file dialog
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Generate TestCase...");
    File file = directoryChooser.showDialog(treeTableView.getScene().getWindow());

    if (file != null) {
      // saveTextToFile(sampleText, file);
      try {
        TestCaseCreatorChain testCaseCreator;
        if (sessionModel.isOneFrankenJar()) {
          testCaseCreator =
              new UberJarTestCaseCreator(
                  sessionModel.getSelectedWords(),
                  file.toString(),
                  sessionModel.isInstallJARs(),
                  sessionModel.getRemoveMetaData(),
                  sessionModel.isKeepTime());
        } else {
          testCaseCreator =
              new DefaultTestCaseCreator(
                  sessionModel.getSelectedWords(),
                  file.toString(),
                  sessionModel.isInstallJARs(),
                  sessionModel.getRemoveMetaData());
        }
        buttonSave.setDisable(true);

        progressBar.setProgress(0);
        progressBar.progressProperty().bind(testCaseCreator.progressProperty());

        new Thread(testCaseCreator).start();

        // FIXME: ugly UI stuff
        buttonSave.disableProperty().bind(testCaseCreator.workDoneProperty().isNotEqualTo(10));

      } catch (Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Look, an Error Dialog");
        alert.setContentText(e.getLocalizedMessage());
        alert.showAndWait();
      }
    }
  }

  public void closeAction(ActionEvent actionEvent) {
    // get a handle to the stage
    Stage stage = (Stage) treeTableView.getScene().getWindow();
    stage.close();
  }
}
