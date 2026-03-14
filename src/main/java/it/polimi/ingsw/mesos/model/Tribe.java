package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.enums.CharacterType;

import java.util.List;

public class Tribe {

    private List<CharacterCard> characters;
    private List<BuildingCard> buildings;

    public Tribe() { }

    public void addCharacter(CharacterCard c) { }

    public void addBuilding(BuildingCard b) { }

    /** Sum of all Builder discount values in the tribe. */
    public int getBuildingDiscount() { return 0; }

    /** Sum of all Gatherer food discounts in the tribe. */
    public int getSustenanceDiscount() { return 0; }

    /** Total shaman star icons across all Shamans. */
    public int getTotalShamanIcons() { return 0; }

    /** Number of distinct InventionIcon values among Inventors. */
    public int getDistinctInventionCount() { return 0; }

    public int getCharacterCount() { return 0; }

    public int getBuildingCount() { return 0; }

    public int countCharacters(CharacterType type) { return 0; }

    // --- Getters ---

    public List<CharacterCard> getCharacters() { return null; }

    public List<BuildingCard> getBuildings() { return null; }
}
