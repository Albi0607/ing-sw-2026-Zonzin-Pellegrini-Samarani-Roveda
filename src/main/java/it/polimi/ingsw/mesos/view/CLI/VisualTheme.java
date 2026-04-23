package it.polimi.ingsw.mesos.view.CLI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class VisualTheme {

    private static JsonObject themeData;

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

    public static String getColor(String colorName) {
        if (themeData == null) return "";
        return themeData.getAsJsonObject("colors").get(colorName).getAsString();
    }

    public static String getSymbol(String symbolName) {
        if (themeData == null) return "?";
        return themeData.getAsJsonObject("symbols").get(symbolName).getAsString();
    }

    public static String getInvention(String inventionName) {
        if (themeData == null) return "💡";
        if (themeData.getAsJsonObject("inventions").has(inventionName)) {
            return themeData.getAsJsonObject("inventions").get(inventionName).getAsString();
        }
        return "💡";
    }
}
