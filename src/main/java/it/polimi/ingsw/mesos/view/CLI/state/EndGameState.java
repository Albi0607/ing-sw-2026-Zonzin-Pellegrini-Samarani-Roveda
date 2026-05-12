package it.polimi.ingsw.mesos.view.CLI.state;

public class EndGameState implements UIState {
    @Override
    public void handleInput(String input, UIContext context) {}

    @Override
    public void render(UIContext context) {
        context.drawUI();
        context.flushNotifications(); // Stampa prima le notifiche!
        System.out.println("\n✨ Calcolo dei punteggi finali in corso... ✨\n");
        context.scheduleResolutionTimeout();
    }
}
