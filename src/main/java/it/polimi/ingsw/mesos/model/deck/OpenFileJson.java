package it.polimi.ingsw.mesos.model.deck;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**Generic class for reading JSON files from the resources package
 * and converting them into Java objects using the Jackson library.
 * @author Alberto Roveda
 */
public class OpenFileJson {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Loads a list of objects from a JSON file.
     *
     * @param path the relative path of the file in the resources folder
     * @param typeReference the TypeReference of the object list
     * @param <T> the type of objects in the list
     * @return a list of objects deserialized from the JSON file
     * @throws IOException if the file does not exist or cannot be read
     */
    public static <T> List<T> loadList(String path, TypeReference<List<T>> typeReference) throws IOException {
        InputStream inputStream = OpenFileJson.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) throw new IOException("File not found: ");
        return mapper.readValue(inputStream, typeReference);
    }
}