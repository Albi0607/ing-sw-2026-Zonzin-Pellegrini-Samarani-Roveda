package it.polimi.ingsw.mesos.rete;

public interface Network {

    boolean register(String nickname, ClientController controller);
    boolean placeTotem(String nickname,char position);
    boolean takeCard(String nickname,int position,boolean isUpper);
    boolean choosePlayers(int numPlayers);

}
