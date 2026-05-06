package it.polimi.ingsw.mesos.view;


import javafx.application.Application;
import javafx.stage.Stage;

public class GuiLauncher extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("GUI del gioco");
        stage.show();
    }

    public static void launchGUI() {
        launch();
    }
}