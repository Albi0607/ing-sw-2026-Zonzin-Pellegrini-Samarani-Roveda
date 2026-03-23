package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Classe generica per aprire file JSON dal package resources
 * e convertirli in oggetti Java usando Jackson.
 */
public class OpenFileJson {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Carica una lista di oggetti da un file JSON.
     * @param resourcePath percorso relativo del file nella cartella resources
     * @param typeReference TypeReference della lista di oggetti
     * @param <T> tipo degli oggetti nella lista
     * @return lista di oggetti deserializzati dal JSON
     * @throws IOException se il file non esiste o non può essere letto
     */
    public static <T> List<T> loadList(String resourcePath, TypeReference<List<T>> typeReference) throws IOException {
        InputStream inputStream = OpenFileJson.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) throw new IOException("File not found: ");
        return mapper.readValue(inputStream, typeReference);
    }
}