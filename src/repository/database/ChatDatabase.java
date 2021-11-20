package repository.database;

import domain.Chat;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatDatabase implements Repository<Long, Chat> {

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
                Long message_id = resultSet.getLong("message_id");

                chat.addPair(user_id, message_id);
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
                Long chat_id = resultSet.getLong("user_id");
                Long user_id = resultSet.getLong("user_id");
                Long message_id = resultSet.getLong("message_id");

                Chat chat = new Chat();
                chat.setId(chat_id);
                boolean found = false;
                for(Chat c: chats)
                    if(Objects.equals(c.getId(), chat_id)) {
                        chat = c;
                        found = true;
                    }

                chat.addPair(user_id, message_id);
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
        String sql = "insert into chats (chat_id, user_id, message_id) values (?, ?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            //TODO ar trebui salvat doar ultimul element din pereche
            for(Tuple<Long, Long> pair: entity.getPairUserMessage()) {
                ps.setLong(1, entity.getId());
                ps.setLong(2, pair.getE1());;
                ps.setLong(3, pair.getE2());

                ps.executeUpdate();
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
        return null;
    }
}
