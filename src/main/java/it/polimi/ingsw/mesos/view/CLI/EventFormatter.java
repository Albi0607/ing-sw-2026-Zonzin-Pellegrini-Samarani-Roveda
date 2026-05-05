package it.polimi.ingsw.mesos.view.CLI;

import it.polimi.ingsw.mesos.common.EventCardJson;

public class EventFormatter implements Formatters.CardFormatter<EventCardJson> {

    /**
     * Formats an event card's data into a color-coded string for CLI display,
     * mapping specific event types to their respective rules and resource icons.
     * @param eventJson The JSON data object of the event card.
     * @return A formatted ANSI string representing the event and its resolution details.
     */
    @Override
    public String format(EventCardJson eventJson) {
        String colorRed = VisualTheme.getColor("RED");
        String colorReset = VisualTheme.getColor("RESET");
        String boltIcon = VisualTheme.getSymbol("event_bolt");
        String foodIcon = VisualTheme.getSymbol("food");
        String prestigeIcon = VisualTheme.getSymbol("prestige");

        if (eventJson.type == null) return colorRed + boltIcon + " Sconosciuto" + colorReset;
        String typeName = eventJson.type.name();

        StringBuilder sb = new StringBuilder();
        sb.append(colorRed).append(boltIcon).append(" ").append(typeName).append(colorReset);

        StringBuilder details = new StringBuilder();

        switch (typeName) {
            case "SUSTENANCE" -> {
                int foodCost = (eventJson.losePoints != null) ? eventJson.losePoints : 1;
                details.append("-").append(foodCost).append(foodIcon).append(" / -2").append(prestigeIcon);
            }
            case "HUNT" -> {
                int gp = (eventJson.gainPoints != null) ? eventJson.gainPoints : 0;
                details.append("+1").append(foodIcon).append(" / +").append(gp).append(prestigeIcon);
            }
            case "SHAMAN_RITUAL" -> {
                int gp = (eventJson.gainPoints != null) ? eventJson.gainPoints : 0;
                int lp = (eventJson.losePoints != null) ? eventJson.losePoints : 0;
                details.append("+").append(gp).append(prestigeIcon).append(" / -").append(lp).append(prestigeIcon);
            }
            case "PAINTING", "CAVE_PAINTING" -> {
                int reqGain = (eventJson.gainNumber != null) ? eventJson.gainNumber : 0;
                int reqLose = (eventJson.loseNumber != null) ? eventJson.loseNumber : 0;
                int gp = (eventJson.gainPoints != null) ? eventJson.gainPoints : 0;
                int lp = (eventJson.losePoints != null) ? eventJson.losePoints : 0;

                details.append("≥").append(reqGain).append("🎨:+").append(gp).append(prestigeIcon).append(" | ");
                details.append("<").append(reqLose).append("🎨:-").append(lp).append(prestigeIcon);
            }
        }

        if (!details.isEmpty()) {
            sb.append(" [").append(details.toString().trim()).append("]");
        }

        return sb.toString();
    }
}
