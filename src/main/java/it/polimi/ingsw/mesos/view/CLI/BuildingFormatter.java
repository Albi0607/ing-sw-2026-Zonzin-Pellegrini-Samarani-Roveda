package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.BuildingCardJson;
import it.polimi.ingsw.mesos.common.enums.CharacterType;
import it.polimi.ingsw.mesos.common.enums.EventType;
import it.polimi.ingsw.mesos.common.enums.SpecialActionType;

public class BuildingFormatter implements Formatters.CardFormatter<BuildingCardJson> {

    public BuildingFormatter() {}

    /**
     * Formats a building card's data into a color-coded string for CLI display,
     * including its cost and specific effects.
     * @param buildJson The JSON data object of the building card.
     * @return A formatted ANSI string representing the building card.
     */
    @Override
    public String format(BuildingCardJson buildJson) {
        String colorYellow = VisualTheme.getColor("YELLOW");
        String colorReset = VisualTheme.getColor("RESET");
        String foodIcon = VisualTheme.getSymbol("food");

        // --- SCELTA DINAMICA DELL'ICONA EDIFICIO IN BASE ALL'ERA DELLA CARTA ---
        String cardEra = (buildJson.era != null) ? buildJson.era.toString() : "ERA_I";

        String buildingIcon = switch (cardEra) {
            case "ERA_II" -> "🏠";
            case "ERA_III" -> "🏛️";
            default -> "🛖";
        };

        String effectStr = getBuildingEffectString(buildJson);
        return colorYellow + buildingIcon + " Edificio [Costo: " + buildJson.cost + foodIcon + "]" +
                (effectStr.isEmpty() ? "" : " (" + effectStr + ")") + colorReset;
    }

    /**
     * Parses the building's logic and converts it into a descriptive string.
     * Rimosso il boltIcon per pulizia visiva come richiesto.
     */
    public String getBuildingEffectString(BuildingCardJson buildJson) {
        String foodIcon = VisualTheme.getSymbol("food");
        String prestigeIcon = VisualTheme.getSymbol("prestige");
        String shamanStar = VisualTheme.getSymbol("shaman_star");
        String shieldIcon = VisualTheme.getSymbol("shield");
        String cardsIcon = VisualTheme.getSymbol("cards");

        StringBuilder effectStr = new StringBuilder();

        if (buildJson.victoryPoints > 0) {
            effectStr.append("+").append(buildJson.victoryPoints).append(prestigeIcon);
        }

        if (buildJson.effect != null) {
            if (!effectStr.isEmpty()) effectStr.append(" | ");

            switch (buildJson.effect) {
                case "ResourceBonusEffect" -> {
                    if (buildJson.eventContext != null) {
                        String evt = buildJson.eventContext.name();
                        String ref = (buildJson.countRef != null) ? buildJson.countRef.name() : "";
                        int amt = (buildJson.amount != null) ? buildJson.amount : 0;
                        effectStr.append(evt).append(" (+").append(amt);
                        if (evt.equals("HUNT")) effectStr.append(foodIcon).append("/+1").append(prestigeIcon).append(" per ").append(ref).append(")");
                        else effectStr.append(foodIcon).append(" per ").append(ref).append(")");
                    } else if (CharacterType.INVENTOR.equals(buildJson.countRef)) {
                        effectStr.append("Coppia INVENTOR: +").append(buildJson.amount).append(foodIcon);
                    } else {
                        effectStr.append("Set di 6: +").append(buildJson.amount).append(foodIcon);
                    }
                }
                case "EventModifierEffect" -> {
                    if (EventType.SUSTENANCE.equals(buildJson.eventContext)) {
                        String ref = (buildJson.countRef != null) ? buildJson.countRef.name() : "";
                        effectStr.append("SUSTENANCE: -").append(buildJson.discount).append(foodIcon).append("/").append(ref);
                    } else if (Boolean.TRUE.equals(buildJson.doublePrestige)) {
                        effectStr.append("SHAMANIC: 2x").append(prestigeIcon);
                    } else if (Boolean.TRUE.equals(buildJson.noLosePrestige)) {
                        effectStr.append("SHAMANIC: ").append(shieldIcon).append(" No -").append(prestigeIcon);
                    } else if (buildJson.virtualIcons != null && buildJson.virtualIcons > 0) {
                        effectStr.append("SHAMANIC: +").append(buildJson.virtualIcons).append(shamanStar);
                    }
                }
                case "EndGameScoringEffect" -> {
                    if (Boolean.TRUE.equals(buildJson.doubleBuilderPoints)) {
                        effectStr.append("Fine: 2x").append(prestigeIcon).append(" per BUILDER");
                    } else if (buildJson.pointsPerSet != null && buildJson.pointsPerSet > 0) {
                        effectStr.append("Fine: +").append(buildJson.pointsPerSet).append(prestigeIcon).append(" per Set");
                    } else if (buildJson.multiplierRef != null) {
                        effectStr.append("Fine: +").append(buildJson.prestigePoints).append(prestigeIcon).append("/").append(buildJson.multiplierRef.name());
                    } else if (buildJson.prestigePoints != null && buildJson.prestigePoints == 25) {
                        effectStr.append("Fine: +25").append(prestigeIcon);
                    }
                }
                case "SpecialActionEffect" -> {
                    if (SpecialActionType.FOOD_ON_TOTEM_SLOT.equals(buildJson.specialType)) {
                        effectStr.append("Totem: +1").append(foodIcon).append(" se Cibo");
                    } else if (SpecialActionType.EXTRA_DRAW.equals(buildJson.specialType)) {
                        effectStr.append("Azione Extra: ").append(cardsIcon).append(" Pescata");
                    }
                }
            }
        }
        return effectStr.toString();
    }
}
