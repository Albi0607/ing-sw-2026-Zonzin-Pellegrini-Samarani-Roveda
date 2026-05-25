package it.polimi.ingsw.mesos.view.CLI.state;

/**
 * Represents the transitional UI state triggered immediately when the game finishes.
 * It locks user input, displays the final board configuration, and initiates a background
 * timer to eventually reveal the game-over screen and leaderboard.
 */
public class EndGameState implements UIState {

    /**
     * Intentionally left blank.
     * Acts as an input "sink", ignoring any keyboard input from the user
     * to prevent commands from being sent after the match has officially ended.
     *
     * @param input   The raw string typed by the user (discarded).
     * @param context The UIContext providing access to the CLI engine.
     */
    @Override
    public void handleInput(String input, UIContext context) {
    }

    /**
     * Renders the final state of the match, displays pending messages, and prepares the transition to the leaderboard.
     *
     * @param context The UIContext providing access to rendering tools and the internal scheduler.
     */
    @Override
    public void render(UIContext context) {
        context.drawUI();
        context.flushNotifications();
        System.out.println("\n✨ Calcolo dei punteggi finali in corso... ✨\n");
        context.scheduleResolutionTimeout();
    }
}
