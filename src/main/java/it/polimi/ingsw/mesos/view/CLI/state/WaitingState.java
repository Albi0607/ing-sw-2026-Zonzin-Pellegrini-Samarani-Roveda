package it.polimi.ingsw.mesos.view.CLI.state;

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
}
