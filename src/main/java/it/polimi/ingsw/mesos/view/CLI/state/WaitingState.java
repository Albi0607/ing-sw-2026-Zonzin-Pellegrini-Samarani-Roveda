package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import it.polimi.ingsw.mesos.view.CLI.CLIPrinter;

public class WaitingState implements UIState {

    public static final WaitingState INSTANCE = new WaitingState();

    private WaitingState() {}

    @Override
    public void handleInput(String input, UIContext context) {
    }

    @Override
    public void render(UIContext context) {

        if (context.getClientState() == it.polimi.ingsw.mesos.rete.ClientModel.ClientState.IN_GAME) {
            context.drawUI();
        }
    }

    @Override
    public void renderPrompt(UIContext context){

        if (!context.isfullDirty()) {
            return;
        }

        if (context.getGameState() == null) return;


        String currentPlayer =
                context.getGameState().currentPlayerNickname;

        GameState phase =
                context.getGameState().currentState;

        switch (phase) {

            case PLACING_TOTEMS ->
                    System.out.println(
                            "\n⏳ In attesa che "
                                    + currentPlayer.toUpperCase()
                                    + " piazzi il totem..."
                    );

            case RESOLVING_ACTIONS ->
                    System.out.println(
                            "\n⏳ In attesa che "
                                    + currentPlayer.toUpperCase()
                                    + " scelga una carta..."
                    );

            default ->
                    System.out.println(
                            "\n⏳ In attesa della prossima mossa..."
                    );
        }
    }
}
