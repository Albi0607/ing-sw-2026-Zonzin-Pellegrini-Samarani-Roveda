package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.CharacterCardJson;



public class CharacterFormatter implements Formatters.CardFormatter<CharacterCardJson> {

    public CharacterFormatter() {}

    /**
     * Formats a character card's data into a color-coded string for CLI display,
     * showing the character type and its specific bonuses (icons, discounts, or prestige).
     * @param charJson The JSON data object representing the character card.
     * @return A formatted ANSI string representing the character and its unique attributes.
     */
    @Override
    public String format(CharacterCardJson charJson) {
        String colorReset = VisualTheme.getColor("RESET");

        // Leggiamo l'era direttamente dalla carta!
        String cardEra = (charJson.era != null) ? charJson.era.toString() : "ERA_I";

        String charColor = switch (cardEra) {
            case "ERA_II" -> VisualTheme.getColor("GREEN");
            case "ERA_III" -> VisualTheme.getColor("PURPLE");
            default -> VisualTheme.getColor("CYAN");
        };

        if (charJson.type == null) return charColor + "Sconosciuto" + colorReset;
        String typeName = charJson.type.name();

        StringBuilder sb = new StringBuilder();
        sb.append(charColor).append(typeName).append(colorReset);

        StringBuilder details = new StringBuilder();

        String foodIcon = VisualTheme.getSymbol("food");
        String prestigeIcon = VisualTheme.getSymbol("prestige");
        String shamanIcon = VisualTheme.getSymbol("shaman_star");

        switch (typeName) {
            case "INVENTOR" -> {
                if (charJson.icon != null) {
                    details.append(VisualTheme.getInvention(charJson.icon.toString()));
                }
            }
            case "HUNTER" -> {
                if (Boolean.TRUE.equals(charJson.hasIcon)) details.append(foodIcon);
            }
            case "SHAMAN" -> {
                int icons = (charJson.numberOfIcons != null) ? charJson.numberOfIcons : 0;
                if (icons > 0) details.append(icons).append(shamanIcon);
            }
            case "GATHERER" -> {
                if (charJson.discountValue != null && charJson.discountValue > 0) {
                    details.append("-").append(charJson.discountValue).append(foodIcon);
                }
            }
            case "BUILDER" -> {
                if (charJson.discountValue != null && charJson.discountValue > 0) {
                    details.append("-").append(charJson.discountValue).append(foodIcon).append(" ");
                }
                if (charJson.prestigePoints != null && charJson.prestigePoints > 0) {
                    details.append("+").append(charJson.prestigePoints).append(prestigeIcon);
                }
            }
        }

        if (!details.isEmpty()) {
            sb.append(" [").append(details.toString().trim()).append("]");
        }

        return sb.toString();
    }
}