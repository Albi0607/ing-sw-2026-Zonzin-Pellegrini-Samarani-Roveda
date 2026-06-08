package it.polimi.ingsw.mesos.view.CLI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Centralized theme manager that loads and provides visual assets for the CLI.
 * It reads configuration data from a JSON file to decouple the visual style
 * (colors, icons, symbols) from the application logic.
 */
public class VisualTheme {

    /** The parsed JSON object containing all theme configurations. */
    private static JsonObject themeData;

    /**
     * Initializes the theme by loading and parsing the 'CLI.json' configuration file
     * from the resources directory. Should be called once during application startup.
     */
    public static void init() {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(VisualTheme.class.getResourceAsStream("/CLI.json")),
                StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            themeData = gson.fromJson(reader, JsonObject.class);

        } catch (Exception e) {
            System.err.println("Errore nel caricamento del cli_theme.json: " + e.getMessage());
        }
    }

    /**
     * Retrieves the ANSI color code associated with the given color name.
     * * @param colorName The identifier for the color (e.g., "header", "error").
     * @return The ANSI string code, or an empty string if not found.
     */
    public static String getColor(String colorName) {
        if (themeData == null) return "";
        return themeData.getAsJsonObject("colors").get(colorName).getAsString();
    }

    /**
     * Retrieves the visual symbol associated with the given symbol identifier.
     * * @param symbolName The identifier for the symbol (e.g., "bullet", "arrow").
     * @return The string representing the symbol, or "?" if not found.
     */
    public static String getSymbol(String symbolName) {
        if (themeData == null) return "?";
        return themeData.getAsJsonObject("symbols").get(symbolName).getAsString();
    }

    /**
     * Retrieves the specific icon/emoji associated with a technological invention.
     * * @param inventionName The identifier for the invention.
     * @return The icon string, or a default lightbulb icon if not found.
     */
    public static String getInvention(String inventionName) {
        if (themeData == null) return "💡";
        if (themeData.getAsJsonObject("inventions").has(inventionName)) {
            return themeData.getAsJsonObject("inventions").get(inventionName).getAsString();
        }
        return "💡";
    }
}