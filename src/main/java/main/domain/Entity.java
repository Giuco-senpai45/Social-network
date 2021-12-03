package main.domain;

import java.io.Serializable;

/**
 * This class represents a generic entity that has a generic type of id
 * @param <ID> generic type id re
 */
public class Entity<ID> implements Serializable {

    /**
     * unique id
     */
    private static final long serialVersionUID = 7331115341259248461L;
    /**
     *  the id of the entity
     */
    private ID id;

    /**
     * @return ID representing the entities id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id representing the id the current id is going to get changed into
     */
    public void setId(ID id) {
        this.id = id;
    }
}