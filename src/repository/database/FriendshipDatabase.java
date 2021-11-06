package repository.database;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDatabase extends InMemoryRepository<Tuple<Long,Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    /**
     * Constructor for the Repo
     *
     * @param validator Validator that represents the validator for the entities in the repo
     */
    public FriendshipDatabase(String url, String username, String password,Validator<Friendship> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        loadData();
    }

    @Override
    public Friendship save(Friendship entity) {
        String sql = "insert into friendships (buddy1, buddy2 ,date) values (?, ?, ?)";

        if(super.save(entity) !=null){
            return entity;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getBuddy1());
            ps.setLong(2, entity.getBuddy2());
            ps.setString(3,entity.getDate());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<Long,Long> id) {
        String sql = "delete from friendships where buddy1= ? and buddy2=?";

        Friendship removedFriendship = super.delete(id);
        if(removedFriendship == null){
            return null;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,id.getE1());
            ps.setLong(2,id.getE2());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return removedFriendship;
    }

    private void loadData(){
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long buddy1 = resultSet.getLong("buddy1");
                Long buddy2 = resultSet.getLong("buddy2");
                String date = resultSet.getString("date");

                Friendship friendship = new Friendship(buddy1,buddy2);
                friendship.setDate(date);
                super.save(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
