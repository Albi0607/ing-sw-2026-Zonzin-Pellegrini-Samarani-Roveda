package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.CardJson;

public interface Formatters {
    interface CardFormatter<T extends CardJson> {
        String format(T cardJson);
    }
}
