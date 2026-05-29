package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class EndGameController {
    private ObservableGameModel gameModel;

    @FXML private VBox resultContainer;
    @FXML private Label databaseLabel;
    @FXML private ScrollPane databaseContainer;
    @FXML private VBox databaseVBox;


    public void set(ObservableGameModel gameModel) {
        this.gameModel = gameModel;
        printResult();
    }

    private void printResult() {
        databaseLabel.setManaged(false);
        databaseContainer.setManaged(false);
        databaseVBox.setManaged(false);
        databaseLabel.setDisable(true);
        databaseContainer.setDisable(true);
        databaseVBox.setDisable(true);


        resultContainer.getChildren().clear();

        List<ObservablePlayerModel> sorted = gameModel.getPlayers().stream()
                .sorted(
                        Comparator.comparingInt(ObservablePlayerModel::getPrestigePoints).reversed()
                                .thenComparing(Comparator.comparingInt(ObservablePlayerModel::getFood).reversed())
                )
                .toList();

        int position = 1;

        for (int i = 0; i < sorted.size(); i++) {

            try {

                ObservablePlayerModel current = sorted.get(i);
                int currentPosition = position;

                boolean isLast = (i == sorted.size() - 1);

                if (!isLast) {
                    ObservablePlayerModel next = sorted.get(i + 1);

                    boolean sameScore = current.getPrestigePoints() == next.getPrestigePoints()
                            && current.getFood() == next.getFood();

                    if (!sameScore) {
                        position++;
                    }
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerCard.fxml"));
                Parent view = loader.load();

                EndGameCardController controller = loader.getController();
                controller.setPlayer(currentPosition,sorted.get(i),gameModel.getPlayers().size());

                resultContainer.getChildren().add(view);

            } catch (Exception e) {
                System.out.println("ERRORE NEL CARICAMENTO DELLE SINGOLE GAMECARD: " + e.getMessage());
                e.printStackTrace();
            }

        }

    }

    public void showLeaderboard(List<GameResult> leaderboard, int myPosition){

        databaseLabel.setManaged(true);
        databaseContainer.setManaged(true);
        databaseVBox.setManaged(true);
        databaseLabel.setDisable(false);
        databaseContainer.setDisable(false);
        databaseVBox.setDisable(false);

        databaseVBox.getChildren().clear();

        for (int i = 0; i < leaderboard.size(); i++) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerCard.fxml"));
                Parent view = loader.load();

                EndGameCardController controller = loader.getController();
                controller.updateLeaderboard(leaderboard.get(i),myPosition==i+1,i+1);

                databaseVBox.getChildren().add(view);

            } catch (Exception e) {
                System.out.println("ERRORE NEL CARICAMENTO DELLE SINGOLE CARD DELLA LEADERBOARD: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

}
