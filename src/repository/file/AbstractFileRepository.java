package repository.file;

import domain.Entity;
import domain.validators.Validator;
import repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class utilises the Template Method Design pattern
 * @param <ID> generic type representing the id given for the entities stored in the repository
 * @param <E> generic type representing the type of entities that are going to get stored in the repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    /**
     * String representing the name of the file from which the repo will get its data
     */
    String fileName;


    /**
     * @param fileName String representing the filename
     * @param validator Validator representing the validator object that is going to validate the data introduced
     */
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * This function loads the date from the file into memory.
     */
    private void loadData() {
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line = br.readLine()) != null){
                if(!line.equals("")) { //de scos afara
                    List<String> attributes = Arrays.asList(line.split(";"));
                    E entity = extractEntity(attributes);
                    super.save(entity);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes a list of strings representing the fields of the entity
     * @return an entity of type E
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * This function transforms an entity into a String to be stored to the file
     * @param entity representing the entity that is going to get transformed into a String
     * @return String representing the attributes of the entity
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * @param entity entity representing the entity which is going to be saved
     * @return
     *  -null  if the entity was saved successfully
     *  -entity if the entity already exists in the repository
     */
    @Override
    public E save(E entity){
        if(super.save(entity) == null){
            writeToFile(entity);
            return null;
        }
        else
            return entity;
    }

    /**
     * Overloaded with a parameter:
     * This function appends to the file the entity transformed into a string
     * @param entity represents the entity that is going to be written to the file
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true)))
        {
            bw.write(createEntityAsString(entity));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function takes the whole repository and writes it to the file
     */
    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,false)))
        {
            super.findAll().forEach(entity -> {
                try {
                    bw.write(createEntityAsString(entity));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function updates the entity given as a parameter if it exists
     * @param entity the updated entity
     * @return
     *  - null if the entity was updated successfully
     *  - entity if the entity doesn't exist
     */
    @Override
    public E update(E entity) {
        E updated_entity = super.update(entity);
        if(updated_entity == null){
            writeToFile();
            return null;
        }
        return entity;
    }

    /**
     * This function deletes the entity with the given ID
     * @param id the id of the entity that is going to be deleted
     * @return
     *  -null if there isn't any entity with the given id
     *  -entity if the entity with the given id was deleted successfully
     */
    @Override
    public E delete(ID id) {
        E deleted_entity = super.delete(id);
        if(deleted_entity == null){
            return null;
        }
        writeToFile();
        return deleted_entity;
    }
}
