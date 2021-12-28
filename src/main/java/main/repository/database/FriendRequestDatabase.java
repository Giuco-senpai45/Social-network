package main.repository.database;

import main.domain.Chat;
import main.domain.FriendRequest;
import main.domain.Friendship;
import main.domain.Message;
import main.domain.validators.Validator;
import main.repository.Repository;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.Paginator;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDatabase implements PagingRepository<Long, FriendRequest> {

    private String url;
    private String username;
    private String password;
    private Validator validator;

    public FriendRequestDatabase(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public FriendRequest findOne(Long aLong) {
        FriendRequest entity = null;
        String sql = "SELECT * from friend_requests where request_id = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1, aLong);

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long from = resultSet.getLong("from_user");
                Long to = resultSet.getLong("to_user");
                String status = resultSet.getString("status");

                entity = new FriendRequest(from,to,status);
                entity.setId(aLong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        List<FriendRequest> requests = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from friend_requests");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long id = resultSet.getLong("request_id");
                Long from = resultSet.getLong("from_user");
                Long to = resultSet.getLong("to_user");
                String status = resultSet.getString("status");

                FriendRequest entity = new FriendRequest(from,to,status);
                entity.setId(id);
                requests.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        String sql = "insert into friend_requests (from_user, to_user, status) values (?, ?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getFrom());
            ps.setLong(2, entity.getTo());
            ps.setString(3, entity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
        return null;
    }

    @Override
    public FriendRequest delete(Long aLong) {
        String sql = "delete from friend_requests where request_id = ?";

        FriendRequest removedRequest = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aLong);

            ps.executeUpdate();
        } catch (SQLException e) {
            return null;
        }
        return removedRequest;
    }

    @Override
    public FriendRequest update(FriendRequest entity) {
        String sql = "update friend_requests set from_user=? , to_user=?, status=?  where request_id=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getFrom());
            ps.setLong(2, entity.getTo());
            ps.setString(3, entity.getStatus());
            ps.setLong(4,entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        Paginator<FriendRequest> paginator = new Paginator<FriendRequest>(pageable, this.findAll());
        return paginator.paginate();
    }
}
