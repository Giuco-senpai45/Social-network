package main.repository.database;

import main.domain.Chat;
import main.domain.Tuple;
import main.domain.validators.Validator;
import main.repository.Repository;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.Paginator;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatDatabase implements PagingRepository<Long, Chat> {

    private String url;
    private String username;
    private String password;
    private Validator validator;

    public ChatDatabase(String url, String username, String password, Validator<Chat> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Chat findOne(Long aLong) {
        Chat chat = null;
        String sql = "SELECT * from chats where chat_id = ?";

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();

            boolean setEntity = false;
            while(resultSet.next()){
                if(!setEntity) {
                    chat = new Chat();
                    chat.setId(aLong);
                    setEntity = true;
                }
                Long user_id = resultSet.getLong("user_id");
                String url = resultSet.getString("url");
                String name = resultSet.getString("name");
                chat.addUserToChat(user_id);
                chat.setUrl(url);
                chat.setName(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chat;
    }

    @Override
    public Iterable<Chat> findAll() {
        List<Chat> chats = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from chats");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long chat_id = resultSet.getLong("chat_id");
                Long user_id = resultSet.getLong("user_id");
                String url = resultSet.getString("url");
                String name = resultSet.getString("name");

                Chat chat = new Chat();
                chat.setId(chat_id);
                chat.setUrl(url);
                chat.setName(name);
                boolean found = false;
                for(Chat c: chats)
                    if(Objects.equals(c.getId(), chat_id)) {
                        chat = c;
                        found = true;
                    }

                chat.addUserToChat(user_id);
                if(!found)
                    chats.add(chat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

    @Override
    public Chat save(Chat entity) {
        String sql = "insert into chats (chat_id, user_id, url, name) values (?, ?, ?, ?)";
        String sql2 = "SELECT * from chats where chat_id = ? and user_id = ?";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps2 = connection.prepareStatement(sql2)) {

            for(Long user: entity.getChatUsers()) {
                ps2.setLong(1, entity.getId());
                ps2.setLong(2, user);
                ResultSet resultSet = ps2.executeQuery();
                if(!resultSet.next()) {

                    ps.setLong(1, entity.getId());
                    ps.setLong(2, user);
                    ps.setString(3,entity.getUrl());
                    ps.setString(4,entity.getName());

                    ps.executeUpdate();
                }
            }
        } catch (SQLException  e) {
            e.printStackTrace();
            return entity;
        }
        return null;
    }

    @Override
    public Chat delete(Long aLong) {
        String sql = "delete from chats where chat_id = ?";

        Chat removedChat = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aLong);

            ps.executeUpdate();
        } catch (SQLException e) {
            return null;
        }
        return removedChat;
    }

    @Override
    public Chat update(Chat entity) {
        String sql = "update chats set url = ?, name = ? where chat_id = ?";

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, entity.getUrl());
            ps.setString(2, entity.getName());
            ps.setLong(3, entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<Chat> findAll(Pageable pageable) {
        Paginator<Chat> paginator = new Paginator<Chat>(pageable, this.findAll());
        return paginator.paginate();
    }
}
