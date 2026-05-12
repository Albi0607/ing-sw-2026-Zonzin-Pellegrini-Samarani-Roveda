package it.polimi.ingsw.mesos.view.CLI.state;

import it.polimi.ingsw.mesos.view.CLI.UIContext;

public class WaitingState implements UIState {

    // L'unica istanza statica di questo stato
    public static final WaitingState INSTANCE = new WaitingState();

    // Costruttore privato! Nessuno può fare "new WaitingState()"
    private WaitingState() {}

    @Override
    public void handleInput(String input, UIContext context) {
        // Silenzio assoluto.
    }

    @Override
    public void render(UIContext context) {
        // Disegna la plancia solo se siamo effettivamente in partita,
        // altrimenti (es. durante il login) resta in silenzio.
        if (context.getClientState() == it.polimi.ingsw.mesos.rete.ClientModel.ClientState.IN_GAME) {
            context.drawUI();
        }
    }
}
