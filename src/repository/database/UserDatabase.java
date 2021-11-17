package repository.database;

import domain.User;
import domain.validators.Validator;
import repository.Repository;
import repository.memory.InMemoryRepository;
import service.serviceExceptions.AddException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDatabase implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    /**
     * Constructor for the Repo
     *
     * @param validator Validator that represents the validator for the entities in the repo
     */
    public UserDatabase(String url, String username, String password,Validator<User> validator) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public User save(User entity) {
        String sql = "insert into users (first_name, last_name ) values (?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException  e) {
//            throw new AddException(e.getMessage());
                return entity;
        }
        return null;
    }

    public Long findMaxId(){

        String sql = "select MAX(user_id) as max_user_id from users ";
        Long max_id = 0L;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                max_id = resultSet.getLong("max_user_id");
            }
        }
        catch (SQLException  e) {
            e.printStackTrace();
        }
//        max_id += 1;
//        int maximul = max_id.intValue();
//        String sql2 = "ALTER SEQUENCE users_user_id_seq RESTART WITH maximul";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement ps = connection.prepareStatement(sql2)) {
//            ps.executeUpdate();
//        }
//        catch (SQLException  e) {
//            e.printStackTrace();
//        }
        return max_id;
    }

    @Override
    public User delete(Long id) {
        String sql = "delete from users where user_id = ?";

        User removedUser = findOne(id);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

           ps.setLong(1,id);

            ps.executeUpdate();
        } catch (SQLException e) {
            return null;
        }
        return removedUser;
    }

    @Override
    public User update(User entity) {

        String sql = "update users set first_name=? , last_name=?  where user_id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setInt(3,entity.getId().intValue());

            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from users");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                User user = new User(firstName,lastName);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User findOne(Long id) {
        User user = null;
        String sql = "SELECT * from users where user_id = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1,id);

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long user_id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                user = new User(firstName,lastName);
                user.setId(user_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

}
