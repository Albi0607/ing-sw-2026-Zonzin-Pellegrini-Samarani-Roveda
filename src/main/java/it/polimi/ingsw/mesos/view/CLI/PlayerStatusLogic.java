package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.view.CLI.formatter.BuildingFormatter;
import it.polimi.ingsw.mesos.view.CardRegistry;
import it.polimi.ingsw.mesos.common.CharacterCardJson;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.ClientModel.PlayerDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerStatusLogic {

    private static final BuildingFormatter buildFormatter = new BuildingFormatter();

    /**
     * Processes a player's building list and converts it into a single formatted
     * string displaying icons and effects for the CLI status.
     * @param p The Data Transfer Object representing the player.
     * @return A formatted string showing all player's buildings or "Nessuno".
     */
    public static String getBuildingsString(PlayerDTO p) {
        if (p.tribe == null || p.tribe.buildings == null || p.tribe.buildings.isEmpty()) {
            return "Nessuno";
        }

        String hutIcon = VisualTheme.getSymbol("building_hut");
        String colorYellow = VisualTheme.getColor("YELLOW");
        String colorReset = VisualTheme.getColor("RESET");

        return p.tribe.buildings.stream()

                .map(b -> CardRegistry.getCard(b.id, BuildingCardJson.class))
                .filter(Objects::nonNull)
                .map(bJson -> colorYellow + hutIcon + " [" + buildFormatter.getBuildingEffectString(bJson) + "]" + colorReset)
                .collect(Collectors.joining(" | "));
    }

    /**
     * Aggregates a player's character collection into a summarized "Tribe" string,
     * grouping them by type and calculating collective bonuses (e.g., discounts, icons).
     * @param p The Data Transfer Object representing the player.
     * @return A descriptive string summarizing the player's tribe composition.
     */
    public static String getTribeString(PlayerDTO p) {
        if (p.tribe == null || p.tribe.characters == null || p.tribe.characters.isEmpty()) {
            return "Nessun personaggio";
        }

        List<CharacterCardJson> chars = p.tribe.characters.stream()

                .map(c -> CardRegistry.getCard(c.id, CharacterCardJson.class))
                .filter(Objects::nonNull)
                .toList();

        List<String> statusParts = new ArrayList<>();

        String foodIcon = VisualTheme.getSymbol("food");
        String prestigeIcon = VisualTheme.getSymbol("prestige");
        String shamanIcon = VisualTheme.getSymbol("shaman_star");

        // --- ARTIST ---
        long artists = chars.stream().filter(c -> c.type == CharacterType.ARTIST).count();
        if (artists > 0) {
            statusParts.add("ARTIST: " + artists);
        }

        // --- HUNTER ---
        long hunters = chars.stream().filter(c -> c.type == CharacterType.HUNTER).count();
        long huntersWithIcon = chars.stream().filter(c -> c.type == CharacterType.HUNTER && Boolean.TRUE.equals(c.hasIcon)).count();
        if (hunters > 0) {
            statusParts.add("HUNTER: " + hunters + (huntersWithIcon > 0 ? " (" + huntersWithIcon + foodIcon + ")" : ""));
        }

        // --- INVENTOR ---
        List<CharacterCardJson> inventors = chars.stream().filter(c -> c.type == CharacterType.INVENTOR).toList();
        if (!inventors.isEmpty()) {
            Map<String, Long> invCounts = inventors.stream()
                    .filter(c -> c.icon != null)
                    .collect(Collectors.groupingBy(c -> VisualTheme.getInvention(c.icon.toString()), Collectors.counting()));

            String invDetails = invCounts.entrySet().stream()
                    .map(e -> e.getValue() + "x" + e.getKey())
                    .collect(Collectors.joining(", "));

            statusParts.add("INVENTOR: " + inventors.size() + (!invDetails.isEmpty() ? " [" + invDetails + "]" : ""));
        }

        // --- SHAMAN ---
        long shamans = chars.stream().filter(c -> c.type == CharacterType.SHAMAN).count();
        int shamanStars = chars.stream().filter(c -> c.type == CharacterType.SHAMAN && c.numberOfIcons != null)
                .mapToInt(c -> c.numberOfIcons).sum();
        if (shamans > 0) {
            statusParts.add("SHAMAN: " + shamans + (shamanStars > 0 ? " (" + shamanStars + shamanIcon + ")" : ""));
        }

        // --- GATHERER ---
        long gatherers = chars.stream().filter(c -> c.type == CharacterType.GATHERER).count();
        int gathDiscount = chars.stream().filter(c -> c.type == CharacterType.GATHERER && c.discountValue != null)
                .mapToInt(c -> c.discountValue).sum();
        if (gatherers > 0) {
            statusParts.add("GATHERER: " + gatherers + (gathDiscount > 0 ? " (-" + gathDiscount + foodIcon + ")" : ""));
        }

        // --- BUILDER ---
        List<CharacterCardJson> builders = chars.stream().filter(c -> c.type == CharacterType.BUILDER).toList();
        if (!builders.isEmpty()) {

            // Sommiamo tutti gli sconti cibo
            int totalDiscount = builders.stream()
                    .filter(b -> b.discountValue != null)
                    .mapToInt(b -> b.discountValue)
                    .sum();

            // Sommiamo tutti i punti prestigio
            int totalPrestige = builders.stream()
                    .filter(b -> b.prestigePoints != null)
                    .mapToInt(b -> b.prestigePoints)
                    .sum();

            // Costruiamo la stringa dei dettagli solo se ci sono effettivamente dei bonus
            String bDetails = "";
            if (totalDiscount > 0) {
                bDetails += "-" + totalDiscount + foodIcon;
            }
            if (totalPrestige > 0) {
                bDetails += (bDetails.isEmpty() ? "" : "/") + "+" + totalPrestige + prestigeIcon;
            }

            statusParts.add("BUILDER: " + builders.size() + (!bDetails.isEmpty() ? " [" + bDetails + "]" : ""));
        }

        return String.join(" | ", statusParts);
    }
}
