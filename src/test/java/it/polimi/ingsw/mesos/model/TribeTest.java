package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.*;
import it.polimi.ingsw.mesos.model.enums.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tribe")
class TribeTest {

    private Tribe tribe;

    // ── Helpers ──────────────────────────────────────────────────────────────
    // These factory methods keep test bodies short and avoid repeating
    // constructor arguments throughout the file.

    private Hunter hunter(boolean hasIcon) {
        return new Hunter(Era.ERA_I, 2, hasIcon);
    }

    private Shaman shaman(int icons) {
        return new Shaman(Era.ERA_I, 2, icons);
    }

    private Artist artist() {
        return new Artist(Era.ERA_I, 2);
    }

    private Builder builder(int discount, int pp) {
        return new Builder(Era.ERA_I, 2, discount, pp);
    }

    private Inventor inventor(InventionIcon icon) {
        return new Inventor(Era.ERA_I, 2, icon);
    }

    private Gatherer gatherer() {
        return new Gatherer(Era.ERA_I, 2, 3);
    }

    private BuildingCard building() {
        // Minimal building: cost 2, 1 VP, no effect, no event context
        return new BuildingCard(Era.ERA_I, 2, 1, null);
    }

    // ─────────────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        tribe = new Tribe();
    }

    // Constructor
    // ? to implement tribe initialization tests i had to add a method to return respectively
    // the inner lists of characters and buildings

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("characters list initialized and empty")
        void charactersEmpty() {
            assertNotNull(tribe.getCharacters());
            assertTrue(tribe.getCharacters().isEmpty());
        }

        @Test
        @DisplayName("buildings list initialized and empty")
        void buildingsEmpty() {
            assertNotNull(tribe.getBuildings());
            assertTrue(tribe.getBuildings().isEmpty());
        }
    }

    @Nested
    @DisplayName("addCharacter() and addBuilding()")
    class AddCardTests {

        @Test
        @DisplayName("addCharacter increases character count by one")
        void addCharacterIncreasesCount() {
            tribe.addCharacter(hunter(false));
            assertEquals(1, tribe.getCharactersCount());
        }

        @Test
        @DisplayName("addBuilding increases building count by one")
        void addBuildingIncreasesCount() {
            tribe.addBuilding(building());
            assertEquals(1, tribe.getBuildingsCount());
        }

        @Test
        @DisplayName("null character throws IllegalArgumentException")
        void nullCharacterThrows() {
            assertThrows(IllegalArgumentException.class, () -> tribe.addCharacter(null));
        }

        @Test
        @DisplayName("null building throws IllegalArgumentException")
        void nullBuildingThrows() {
            assertThrows(IllegalArgumentException.class, () -> tribe.addBuilding(null));
        }

        @Test
        @DisplayName("multiple characters are all stored")
        void multipleCharactersStored() {
            tribe.addCharacter(hunter(false));
            tribe.addCharacter(artist());
            tribe.addCharacter(gatherer());
            assertEquals(3, tribe.getCharactersCount());
        }
    }

    @Nested
    @DisplayName("countCharacters()")
    class CountCharactersTests {

        @Test
        @DisplayName("returns zero when tribe is empty")
        void zeroWhenEmpty() {
            assertEquals(0, tribe.getCharactersTypeCount(CharacterType.HUNTER));
        }

        @Test
        @DisplayName("counts only the requested type")
        void countsOnlyRequestedType() {
            tribe.addCharacter(hunter(false));
            tribe.addCharacter(hunter(true));
            tribe.addCharacter(artist());
            assertEquals(2, tribe.getCharactersTypeCount(CharacterType.HUNTER));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.ARTIST));
        }

        @Test
        @DisplayName("returns zero for a type not present in the tribe")
        void zeroForAbsentType() {
            tribe.addCharacter(hunter(false));
            assertEquals(0, tribe.getCharactersTypeCount(CharacterType.SHAMAN));
        }

        @Test
        @DisplayName("counts all six character types correctly")
        void countsAllTypes() {
            tribe.addCharacter(hunter(false));
            tribe.addCharacter(shaman(2));
            tribe.addCharacter(artist());
            tribe.addCharacter(builder(1, 3));
            tribe.addCharacter(inventor(InventionIcon.BOAT));
            tribe.addCharacter(gatherer());

            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.HUNTER));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.SHAMAN));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.ARTIST));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.BUILDER));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.INVENTOR));
            assertEquals(1, tribe.getCharactersTypeCount(CharacterType.GATHERER));
        }
    }

    @Nested
    @DisplayName("getBuildingDiscount()")
    class BuildingDiscountTests {

        @Test
        @DisplayName("returns zero with no builders")
        void zeroWithNoBuilders() {
            tribe.addCharacter(hunter(false));
            assertEquals(0, tribe.getBuildingDiscount());
        }

        @Test
        @DisplayName("returns discount value of a single builder")
        void singleBuilder() {
            tribe.addCharacter(builder(2, 4));
            assertEquals(2, tribe.getBuildingDiscount());
        }

        @Test
        @DisplayName("sums discounts of multiple builders")
        void multipleBuildersSummed() {
            tribe.addCharacter(builder(1, 3));
            tribe.addCharacter(builder(2, 1));
            assertEquals(3, tribe.getBuildingDiscount());
        }

        @Test
        @DisplayName("non-builder cards do not affect the discount")
        void nonBuildersIgnored() {
            tribe.addCharacter(builder(2, 4));
            tribe.addCharacter(hunter(false));
            tribe.addCharacter(gatherer());
            assertEquals(2, tribe.getBuildingDiscount());
        }
    }

    @Nested
    @DisplayName("getSustenanceDiscount()")
    class SustenanceDiscountTests {

        @Test
        @DisplayName("returns zero with no gatherers")
        void zeroWithNoGatherers() {
            tribe.addCharacter(hunter(false));
            assertEquals(0, tribe.getSustenanceDiscount());
        }

        @Test
        @DisplayName("returns 3 for a single gatherer")
        void singleGatherer() {
            tribe.addCharacter(gatherer());
            assertEquals(3, tribe.getSustenanceDiscount());
        }

        @Test
        @DisplayName("returns 6 for two gatherers")
        void twoGatherers() {
            tribe.addCharacter(gatherer());
            tribe.addCharacter(gatherer());
            assertEquals(6, tribe.getSustenanceDiscount());
        }

        @Test
        @DisplayName("non-gatherer cards do not contribute")
        void nonGatherersIgnored() {
            tribe.addCharacter(gatherer());
            tribe.addCharacter(builder(2, 3));
            assertEquals(3, tribe.getSustenanceDiscount());
        }
    }

    @Nested
    @DisplayName("getTotalShamanIcons()")
    class ShamanIconTests {

        @Test
        @DisplayName("returns zero with no shamans")
        void zeroWithNoShamans() {
            assertEquals(0, tribe.getTotalShamanIcons());
        }

        @Test
        @DisplayName("returns icon count of a single shaman")
        void singleShaman() {
            tribe.addCharacter(shaman(3));
            assertEquals(3, tribe.getTotalShamanIcons());
        }

        @Test
        @DisplayName("sums icons across multiple shamans")
        void multipleShamansSummed() {
            tribe.addCharacter(shaman(1));
            tribe.addCharacter(shaman(2));
            tribe.addCharacter(shaman(3));
            assertEquals(6, tribe.getTotalShamanIcons());
        }

        @Test
        @DisplayName("non-shaman cards do not contribute")
        void nonShamansIgnored() {
            tribe.addCharacter(shaman(2));
            tribe.addCharacter(hunter(false));
            assertEquals(2, tribe.getTotalShamanIcons());
        }
    }

    @Nested
    @DisplayName("getDistinctInventionCount()")
    class DistinctInventionTests {

        @Test
        @DisplayName("returns zero with no inventors")
        void zeroWithNoInventors() {
            assertEquals(0, tribe.getDistinctInventionCount());
        }

        @Test
        @DisplayName("returns 1 for a single inventor")
        void singleInventor() {
            tribe.addCharacter(inventor(InventionIcon.BOAT));
            assertEquals(1, tribe.getDistinctInventionCount());
        }

        @Test
        @DisplayName("counts two inventors with different icons as 2")
        void twoDifferentIcons() {
            tribe.addCharacter(inventor(InventionIcon.BOAT));
            tribe.addCharacter(inventor(InventionIcon.HOOK));
            assertEquals(2, tribe.getDistinctInventionCount());
        }

        @Test
        @DisplayName("counts two inventors with the same icon as 1 (distinct)")
        void twoSameIconsCountAsOne() {
            tribe.addCharacter(inventor(InventionIcon.BOWL));
            tribe.addCharacter(inventor(InventionIcon.BOWL));
            assertEquals(1, tribe.getDistinctInventionCount());
        }

        @Test
        @DisplayName("counts all 10 distinct icons correctly")
        void allTenIcons() {
            for (InventionIcon icon : InventionIcon.values()) {
                tribe.addCharacter(inventor(icon));
            }
            assertEquals(10, tribe.getDistinctInventionCount());
        }

        @Test
        @DisplayName("non-inventor cards do not contribute")
        void nonInventorsIgnored() {
            tribe.addCharacter(inventor(InventionIcon.BOAT));
            tribe.addCharacter(hunter(false));
            assertEquals(1, tribe.getDistinctInventionCount());
        }
    }

    @Nested
    @DisplayName("getCharacterCount() and getBuildingCount()")
    class CountTests {

        @Test
        @DisplayName("both return zero on empty tribe")
        void zeroOnEmptyTribe() {
            assertEquals(0, tribe.getCharactersCount());
            assertEquals(0, tribe.getBuildingsCount());
        }

        @Test
        @DisplayName("buildings are not counted as characters")
        void buildingsNotCountedAsCharacters() {
            tribe.addBuilding(building());
            tribe.addBuilding(building());
            assertEquals(0, tribe.getCharactersCount());
            assertEquals(2, tribe.getBuildingsCount());
        }

        @Test
        @DisplayName("characters are not counted as buildings")
        void charactersNotCountedAsBuildings() {
            tribe.addCharacter(hunter(false));
            tribe.addCharacter(artist());
            assertEquals(2, tribe.getCharactersCount());
            assertEquals(0, tribe.getBuildingsCount());
        }
    }

    @Nested
    @DisplayName("getInventors()")
    class getInventors {

        @Test
        @DisplayName("inventors are correctly filtered")
        void inventorsFiltering() {
            for (InventionIcon icon : InventionIcon.values()) {
                tribe.addCharacter(inventor(icon));
            }
            tribe.addCharacter(hunter(true));
            assertEquals(tribe.getInventors().size(), tribe.getCharactersTypeCount(CharacterType.INVENTOR));
        }

        @Test
        @DisplayName("empty list when no inventor are in tribe")
        void emptyInventorList() {
            tribe.addCharacter(hunter(true));
            assertTrue(tribe.getInventors().isEmpty());
        }
        @Nested
        @DisplayName("getLastCard()")
        class getLastCard {

            @Test
            @DisplayName("the card is the last in the tribe")
            void lastCardIndex() {
                tribe.addCharacter(hunter(true));
                tribe.addBuilding(building());
                //assertEquals(tribe.getLastCard(), tribe.getBuildings().get(tribe.getBuildings().size() - 1));
            }
        }

    }

}