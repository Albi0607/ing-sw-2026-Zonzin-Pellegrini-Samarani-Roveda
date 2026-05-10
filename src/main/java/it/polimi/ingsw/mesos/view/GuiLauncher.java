package it.polimi.ingsw.mesos.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GuiLauncher extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        // Imposta larghezza 800, altezza 600
        Scene scene = new Scene(root, 1500, 800);
        stage.setTitle("GUI del gioco");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}