package it.polimi.ingsw.mesos.view.CLI.state;

public interface UIState {
    void handleInput(String input, UIContext context);

    default void render(UIContext context) {}

    default void renderPrompt(UIContext context) {}
}
