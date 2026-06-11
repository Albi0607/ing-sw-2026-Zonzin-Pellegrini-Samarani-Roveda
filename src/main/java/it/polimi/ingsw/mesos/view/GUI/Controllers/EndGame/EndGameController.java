package it.polimi.ingsw.mesos.view.GUI.Controllers.EndGame;

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

/**
 * Controller for the end game screen.
 * Displays the final results of the current game session sorted by score,
 * and optionally shows the all-time leaderboard fetched from the database.
 */
public class EndGameController {
    /** The observable game model used to retrieve the final player standings. */
    private ObservableGameModel gameModel;

    // FXML components
    @FXML private VBox resultContainer;
    @FXML private Label databaseLabel;
    @FXML private ScrollPane databaseContainer;
    @FXML private VBox databaseVBox;

    /**
     * Injects the game model and immediately renders the end game results.
     *
     * @param gameModel the observable game model containing the final player states
     */
    public void setController(ObservableGameModel gameModel) {
        this.gameModel = gameModel;
        printResult();
    }

    /**
     * Renders the end game result cards sorted by prestige points descending,
     * with food as a tiebreaker. Players with equal score share the same position.
     * The database leaderboard section is hidden when this method is called.
     */
    private void printResult() {
        setDatabaseVisible(false);

        resultContainer.getChildren().clear();

        //si potrebbe usare winners facendo solo il controllo delle posizioni
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

    /**
     * Renders the all-time leaderboard fetched from the database.
     * Makes the database section visible and populates it with one card per entry.
     * The local player's entry is highlighted if their position matches.
     *
     * @param leaderboard the list of all-time game results from the database
     * @param myPosition the position of the local player in the leaderboard
     */
    public void showLeaderboard(List<GameResult> leaderboard, int myPosition){

        setDatabaseVisible(true);

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

    /**
     * Shows or hides the database leaderboard section.
     * Manages both the layout presence and the disabled state of all three components.
     *
     * @param visible true to show the section, false to hide it
     */
    private void setDatabaseVisible(boolean visible) {
        databaseLabel.setManaged(visible);
        databaseContainer.setManaged(visible);
        databaseVBox.setManaged(visible);
        databaseLabel.setDisable(!visible);
        databaseContainer.setDisable(!visible);
        databaseVBox.setDisable(!visible);
    }

}
