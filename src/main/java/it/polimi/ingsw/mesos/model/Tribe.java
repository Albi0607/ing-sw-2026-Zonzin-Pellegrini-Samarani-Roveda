package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.Builder;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.Inventor;
import it.polimi.ingsw.mesos.model.card.character.Shaman;
import it.polimi.ingsw.mesos.model.enums.CharacterType;
import it.polimi.ingsw.mesos.model.enums.InventionIcon;

import java.util.ArrayList;
import java.util.List;

public class Tribe {

    private final List<CharacterCard> characters;
    private final List<BuildingCard> buildings;

    public Tribe() {
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    /**
     * adds a carachter to the tribe, in the characters list tail
     * @param c: the new carachterCard added to the tribe
     */
    public void addCharacter(CharacterCard c) {
        if (c == null) {
            throw new IllegalArgumentException("character cannot be null");
        }
        characters.add(c);
    }

    /**
     * adds a building to the tribe, in the buildings list tail
     * @param b: the new buildingCard added to the tribe
     */
    public void addBuilding(BuildingCard b) {
        if (b == null) {
            throw new IllegalArgumentException("character cannot be null");
        }
        buildings.add(b);
    }

    /**
     *  Sum of all Builder discount values in the tribe.
     * @return total food discount for building payments
     * as the number of builder in the tribe
     * */
    public int getBuildingDiscount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.BUILDER)
                .map(c -> (Builder) c)
                .mapToInt(Builder::getDiscountValue)
                .sum();
    }

    /**
     * Sum of all Gatherer food discounts in the tribe.
     * @return total food discount of the tribe
     * as 3 * number of gatherer in the tribe
     */
    public int getSustenanceDiscount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.GATHERER)
                .mapToInt(c -> 3)
                .sum();

        /*
        alternativa:
        long count = characters.stream()
                .filter( c -> c.getType() == CharacterType.GATHERER)
                .count();

        return (int) count * 3;
        */
    }

    /**
     * Total shaman star icons across all Shamans.
     * @return total number of Shaman star icons
     * */
    public int getTotalShamanIcons() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.SHAMAN)
                .map(c -> (Shaman) c)
                .mapToInt(Shaman::getNumberOfIcons)
                .sum();
    }

    public List<InventionIcon> getDistinctInventionIcons(){return null;}

    /**
     * Number of distinct InventionIcon values among Inventors.
     * @return total distinct icons of inventors card
     * */
    public long getDistinctInventionCount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.INVENTOR)
                .map(c -> (Inventor) c)
                .map(Inventor::getIcon)
                .distinct()
                .count();
    }

    public int getCharactersCount() {
        return characters.size();
    }

    //per contare quanti character dello stesso tipo ci sono
    public int getCharactersTypeCount(CharacterType type){return 0;}

    public int getBuildingsCount() {
        return buildings.size();
    }

    public List<CharacterCard> getCharacters() {
        return new ArrayList<>(characters);
    }

    public List<BuildingCard> getBuildings() { return new ArrayList<>(buildings); }


    //attenzione il giocatore sceglie 2 carte in un round si potrebbe perdere effetto della penultima carta
    public CharacterCard getLastCard(){return null;}

    public List<Inventor> getInventors(){return null;}

}
