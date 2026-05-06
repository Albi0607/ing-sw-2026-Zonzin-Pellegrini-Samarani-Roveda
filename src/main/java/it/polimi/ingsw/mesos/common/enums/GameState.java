package it.polimi.ingsw.mesos.common.enums;
//è veramente necessario dato che ci sono già le classi stato? ->alberto

//non lo so dire, io quando ho visto questa enum senza implementazioni le ho usate come id in un nuovo
// metodo getter di ciascuna classe per ritornare la categoria dell'evento attuale,
// c'è sicuro un modo migliore, dunque probabilmente inutili
public enum GameState {
    SETUP,
    PLACING_TOTEMS,
    RESOLVING_ACTIONS,
    END_ROUND,
    FINISHED
}
