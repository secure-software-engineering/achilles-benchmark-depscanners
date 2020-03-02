package de.upb.achilles.generator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/achillesmain.fxml"));
    primaryStage.setTitle("Achilles TestFixture Generator");
    Scene scene = new Scene(root, 1024, 800);
    primaryStage.setScene(scene);

    scene.getStylesheets().add(Main.class.getResource("/main.css").toExternalForm());
    primaryStage.show();
  }
}
