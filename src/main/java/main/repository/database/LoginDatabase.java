package main.repository.database;

import main.domain.Login;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.Paginator;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDatabase implements PagingRepository<String, Login> {
    private String url;
    private String username;
    private String password;
    //private Validator<User> validator;

    public LoginDatabase(String url, String username, String password) {
        //this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Login save(Login entity) {
        String sql = "insert into login (username, password, user_id) " +
                "values (?, ?, ?)";
        //validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getId());
            ps.setString(2, entity.getPassword());
            ps.setInt(3, Integer.parseInt(entity.getUserID().toString()));

            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Login delete(String id) {
        return null;
    }

    @Override
    public Login update(Login entity) {

        String sql = "update login set password=? , user_id=?  where username=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getPassword());
            ps.setInt(2, entity.getUserID().intValue());
            ps.setString(3, entity.getId());


            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Iterable<Login> findAll() {
        List<Login> logins = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from login");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Long userID = (long) resultSet.getInt("user_id");

                Login loginData = new Login(password, userID);
                loginData.setId(username);
                logins.add(loginData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logins;
    }

    @Override
    public Login findOne(String id) {
        Login loginData = null;
        String sql = "SELECT * from login where username = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, id);

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Long userID = (long) resultSet.getInt("user_id");

                loginData = new Login(password, userID);
                loginData.setId(username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginData;
    }

    @Override
    public Page<Login> findAll(Pageable pageable) {
        Paginator<Login> paginator = new Paginator<Login>(pageable, this.findAll());
        return paginator.paginate();
    }
}
