package it.polimi.ingsw.mesos.view;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.view.CLI.CLI;

public class MesosMain {
    public static void main(String[] args) {
        GameController controller = new GameController();
        CLI cli = new CLI(controller);
        cli.start();
    }
}
