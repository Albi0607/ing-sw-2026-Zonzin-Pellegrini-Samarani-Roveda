package it.polimi.ingsw.mesos.view.CLI.state;

/**
 * The core interface for the State Pattern driving the CLI.
 * Each implementation represents a specific screen or logical phase of the client (e.g., Login, Lobby, Game).
 * It strictly separates user input processing from terminal rendering logic.
 */
public interface UIState {

    /**
     * Processes raw text input provided by the user.
     * This is the logical "brain" of the state: it handles data validation, executes local business logic,
     * sends network commands via the Context, and triggers state transitions.
     * It should NOT perform heavy graphical rendering.
     *
     * @param input   The raw string typed by the user in the terminal.
     * @param context The UIContext providing access to the CLI engine, network controller, and memory.
     */
    void handleInput(String input, UIContext context);

    /**
     * Performs a full, heavy redraw of the screen.
     * Implementations should typically clear the terminal and draw the complete visual representation
     * of the current state (e.g., printing the entire game board or large data tables).
     * Triggered by the CLI engine when the 'fullDirty' flag is set.
     *
     * @param context The UIContext providing access to the data needed for rendering.
     */
    default void render(UIContext context) {}

    /**
     * Prints the lightweight interaction prompt at the bottom of the screen.
     * Used to ask the user a specific question (e.g., "Choose a card (1-5): ") without clearing
     * or redrawing the entire screen above it.
     * Triggered automatically after a full render, or independently when the 'softDirty' flag is set.
     *
     * @param context The UIContext providing access to the data needed for the prompt.
     */
    default void renderPrompt(UIContext context) {}
}
