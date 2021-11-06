package repository.database;

import domain.User;
import domain.validators.Validator;
import repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserDatabase extends InMemoryRepository<Long, User> {
    private String url;
    private String username;
    private String password;
    /**
     * Constructor for the Repo
     *
     * @param validator Validator that represents the validator for the entities in the repo
     */
    public UserDatabase(String url, String username, String password,Validator<User> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        loadData();
    }


    @Override
    public User save(User entity) {
        String sql = "insert into users (first_name, last_name ) values (?, ?)";

        if(super.save(entity) !=null){
            return entity;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        String sql = "delete from users where user_id = ?";

        User removedUser = super.delete(id);
        if(removedUser == null){
            return null;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

           ps.setLong(1,id);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return removedUser;
    }

    @Override
    public User update(User entity) {

        User updatedUser = super.update(entity);
        if(updatedUser != null){
            return updatedUser;
        }

        String sql = "update users set first_name=? , last_name=?  where user_id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setInt(3,entity.getId().intValue());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadData(){
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from users");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName,lastName);
                user.setId(id);
                super.save(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
