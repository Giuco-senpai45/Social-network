package main.repository.memory;

import main.domain.Entity;
import main.domain.validators.Validator;
import main.repository.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <ID> Generic type representing the type of ID the entities will have
 * @param <E> E will be the type of entity saved in the repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    /**
     * Validator for entities in the repo
     */
    private Validator<E> validator;


    /**
     * Map in which the entities will be stored in memory
     */
    Map<ID,E> entities;

    /**
     * Constructor for the Repo
     * @param validator Validator that represents the validator for the entities in the repo
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**
     * This function searches the Map for the entity with the given ID and returns that entity
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return E entity that has the given ID
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**
     * @return Iterable representing all the entities that are currently saved in the repo
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * This function adds to the repo the entity given as a parameter
     * @param entity entity must be not null
     * @return
     *  -null if the entity is saved successfully
     *  -entity if the entity already exists
     * @throws IllegalArgumentException if the entity given as parameter is null
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        boolean entityExists = false;
        for (E entityMap : entities.values()) {
            if(entityMap.equals(entity))
            {
                entityExists = true;
                break;
            }
        }
        if(entityExists)
            return entity;
        else
            entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * This function deletes from the repo the entity with the given id
     * @param id id must be not null
     * @return
     *  -null if the entity with the given id doesn't exist
     *  -entity if the entity is deleted from the repo successfully
     * @throws IllegalArgumentException if the ID given as a parameter is null
     */
    @Override
    public E delete(ID id) {
        if(id == null) {
            throw new IllegalArgumentException("id must not be null00");
        }
        if(entities.get(id) == null){
            return null;
        }
        return entities.remove(id);
    }

    /**
     * This function updates the entity given as a parameter if the entity exists
     * @param entity entity must not be null
     * @return
     *  -null if the entity is updated successfully
     *  -entity if the entity doesn't exist
     * @throws IllegalArgumentException if the entity given as a parameter is null
     */
    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

//        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

}