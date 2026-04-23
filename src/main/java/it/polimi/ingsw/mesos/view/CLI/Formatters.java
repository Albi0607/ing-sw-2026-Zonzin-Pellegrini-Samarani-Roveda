package it.polimi.ingsw.mesos.view.CLI;

public interface Formatters {
    interface CardFormatter<T> {
        String format(T cardJson);
    }
}
