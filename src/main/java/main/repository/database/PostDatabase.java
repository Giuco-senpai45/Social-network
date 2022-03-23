package main.repository.database;

import main.domain.Post;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.Paginator;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDatabase implements PagingRepository<Long, Post> {
    private final String url;
    private final String username;
    private final String password;

    public PostDatabase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Post save(Post entity) {
        String sql = "insert into posts (post_id, post_url, user_id) " +
                "values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(entity.getId().toString()));
            ps.setString(2, entity.getPostURL());
            ps.setInt(3, Integer.parseInt(entity.getUserID().toString()));

            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Post delete(Long id) {
//        String sql = "delete from users where user_id = ?";
//
//        User removedUser = findOne(id);
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//
//            ps.setLong(1,id);
//
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            return null;
//        }
//        return removedUser;
        return null;
    }

    @Override
    public Post update(Post entity) {

        String sql = "update posts set post_url = ?, user_id = ?  where post_id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(entity.getId().toString()));
            ps.setString(2, entity.getPostURL());
            ps.setInt(3, Integer.parseInt(entity.getUserID().toString()));

            ps.executeUpdate();
        } catch (SQLException e) {
            return entity;
        }
        return null;
    }

    @Override
    public Iterable<Post> findAll() {
        List<Post> posts = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from posts");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long postID = (long) resultSet.getInt("post_id");
                String postURL = resultSet.getString("post_url");
                Long userID = (long) resultSet.getInt("user_id");

                Post post = new Post(postURL, userID);
                post.setId(postID);
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findOne(Long id) {
        Post post = null;
        String sql = "SELECT * from posts where post_id = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, Integer.parseInt(id.toString()));

            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long postID = (long) resultSet.getInt("post_id");
                String postURL = resultSet.getString("post_url");
                Long userID = (long) resultSet.getInt("user_id");

                post = new Post(postURL, userID);
                post.setId(postID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        Paginator<Post> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}

