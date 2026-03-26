package it.polimi.ingsw.mesos.model.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    public class CreateOfferTile {

        public List<OfferTile> initializeOfferTiles(int numPlayers) {
            if (numPlayers < 2 || numPlayers > 5) {
                throw new IllegalArgumentException("Invalid number of players");
            }

            List<OfferTile> tiles = new ArrayList<>();

            OfferTile tileA = new OfferTile('A', 5);
            tileA.setUpperCount(0); tileA.setLowerCount(0); tileA.setFoodBonus(3);

            OfferTile tileB = new OfferTile('B', 0);
            tileB.setUpperCount(0); tileB.setLowerCount(1); tileB.setFoodBonus(0);

            OfferTile tileC = new OfferTile('C', 0);
            tileC.setUpperCount(1); tileC.setLowerCount(0); tileC.setFoodBonus(0);

            OfferTile tileD = new OfferTile('D', 3);
            tileD.setUpperCount(0); tileD.setLowerCount(2); tileD.setFoodBonus(0);

            OfferTile tileE = new OfferTile('E', 0);
            tileE.setUpperCount(1); tileE.setLowerCount(1); tileE.setFoodBonus(0);

            OfferTile tileF = new OfferTile('F', 0);
            tileF.setUpperCount(2); tileF.setLowerCount(0); tileF.setFoodBonus(0);

            OfferTile tileG = new OfferTile('G', 4);
            tileG.setUpperCount(2); tileG.setLowerCount(1); tileG.setFoodBonus(0);

            List<OfferTile> allTiles = Arrays.asList(
                    tileA, tileB, tileC, tileD, tileE, tileF, tileG
            );

            for (OfferTile t : allTiles) {
                if (t.getMinPlayers() <= numPlayers) {
                    tiles.add(t);
                }
            }

            return tiles;
        }
    }
