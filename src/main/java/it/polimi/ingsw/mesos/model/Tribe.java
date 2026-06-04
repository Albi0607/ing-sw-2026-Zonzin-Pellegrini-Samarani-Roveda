package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.Builder;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.Inventor;
import it.polimi.ingsw.mesos.model.card.character.Shaman;
import it.polimi.ingsw.mesos.common.enums.CharacterType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's tribe, containing their acquired character and building cards.
 * Provides methods to calculate total discounts and icons based on the cards in the tribe.
 */
public class Tribe {

    private final List<CharacterCard> characters;
    private final List<BuildingCard> buildings;

    /**
     * Constructs an empty Tribe with no characters or buildings.
     */
    public Tribe() {
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    /**
     * Adds a character card to the tribe.
     *
     * @param c the CharacterCard to add
     * @throws IllegalArgumentException if the character is null
     */
    public void addCharacter(CharacterCard c) {
        if (c == null) {
            throw new IllegalArgumentException("character cannot be null");
        }
        characters.add(c);
    }

    /**
     * Adds a building card to the tribe.
     *
     * @param b the BuildingCard to add
     * @throws IllegalArgumentException if the building is null
     */
    public void addBuilding(BuildingCard b) {
        if (b == null) {
            throw new IllegalArgumentException("building cannot be null");
        }
        buildings.add(b);
    }

    /**
     * Calculates the total food discount for building payments.
     * Sums the discount values of all Builder characters in the tribe.
     *
     * @return the total building discount
     */
    public int getBuildingDiscount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.BUILDER)
                .map(c -> (Builder) c)
                .mapToInt(Builder::getDiscountValue)
                .sum();
    }

    /**
     * Calculates the total sustenance discount for the tribe.
     * Sums the fixed discount (3 food units) for each Gatherer character in the tribe.
     *
     * @return the total sustenance discount
     */
    public int getSustenanceDiscount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.GATHERER)
                .mapToInt(c -> 3)
                .sum();
    }

    /**
     * Calculates the total number of Shaman star icons in the tribe.
     * Sums the icons of all Shaman characters.
     *
     * @return the total number of star icons
     */
    public int getTotalShamanIcons() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.SHAMAN)
                .map(c -> (Shaman) c)
                .mapToInt(Shaman::getNumberOfIcons)
                .sum();
    }

    /**
     * Counts the number of distinct invention icons among the Inventors in the tribe.
     *
     * @return the number of unique invention icons
     */
    public long getDistinctInventionCount() {
        return characters.stream()
                .filter(c -> c.getType() == CharacterType.INVENTOR)
                .map(c -> (Inventor) c)
                .map(Inventor::getIcon)
                .distinct()
                .count();
    }

    /**
     * Returns the total number of characters in the tribe.
     *
     * @return the character count
     */
    public int getCharactersCount() {
        return characters.size();
    }

    /**
     * Returns the number of characters of a specific type in the tribe.
     *
     * @param type the CharacterType to count
     * @return the number of characters of that type
     */
    public int getCharactersTypeCount(CharacterType type){
        return characters.stream()
                .filter( c -> c.getType() == type)
                .mapToInt(c -> 1)
                .sum();
    }

    /**
     * Returns the total number of buildings in the tribe.
     *
     * @return the building count
     */
    public int getBuildingsCount() {
        return buildings.size();
    }

    /**
     * Returns a copy of the list of characters in the tribe.
     *
     * @return a list of CharacterCard instances
     */
    public List<CharacterCard> getCharacters() {
        return new ArrayList<>(characters);
    }

    /**
     * Returns a copy of the list of buildings in the tribe.
     *
     * @return a list of BuildingCard instances
     */
    public List<BuildingCard> getBuildings() { return new ArrayList<>(buildings); }

    /**
     * Returns the last character card added to the tribe.
     *
     * @return the last CharacterCard, or throws an exception if the tribe is empty
     */
    public CharacterCard getLastCard(){
        return characters.get(characters.size() -1);
    }

    /**
     * Returns all Inventor characters in the tribe.
     *
     * @return a list of Inventor instances
     */
    public List<Inventor> getInventors(){
        return characters.stream()
                .filter( c -> c.getType() == CharacterType.INVENTOR)
                .map(c -> (Inventor)c )
                .toList();
    }

}
