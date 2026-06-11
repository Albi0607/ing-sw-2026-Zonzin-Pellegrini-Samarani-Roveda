package it.polimi.ingsw.mesos.view.GUI.ObservableGame;

/**
 * Generic interface for objects that can update their internal state
 * from a corresponding Data Transfer Object (DTO).
 *
 * @param <T> the type of DTO used to update this object
 */
public interface DTOUpdatable<T> {

    /**
     * Updates the internal state of this object using the provided DTO.
     *
     * @param dto the DTO containing the new data
     */
    void updateFromDTO(T dto);

}