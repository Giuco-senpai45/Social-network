package main.repository.database;

import main.domain.Chat;
import main.domain.RoseEvent;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.Paginator;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoseEventsDatabase implements PagingRepository<Long, RoseEvent> {
    private final String url;
    private final String username;
    private final String password;

    public RoseEventsDatabase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public RoseEvent findOne(Long aLong) {
        RoseEvent event = null;
        String sql = "SELECT * from events where event_id = ?";
        String sql2 = "SELECT * from event_participants where event_id = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement ps2 = connection.prepareStatement(sql2)){

            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();

            ps2.setLong(1, aLong);
            ResultSet resultSet2 = ps2.executeQuery();

            boolean setEntity = false;
            if(resultSet.next()){
                if(!setEntity) {
                    event = new RoseEvent();
                    event.setId(aLong);
                    setEntity = true;
                }
                String url = resultSet.getString("event_url");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                Long organiser = resultSet.getLong("organiser");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                event.setEventUrl(url);
                event.setEventName(name);
                event.setLocation(location);
                event.setOrganiser(organiser);
                event.setDate(date);
                while (resultSet.next()){
                    event.addParticipant(resultSet2.getLong("participant_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public Iterable<RoseEvent> findAll() {
        List<RoseEvent> events = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("SELECT * from events");
            PreparedStatement statement2 = connection.prepareStatement("SELECT * from event_participants where event_id = ?");
            ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next()){
                Long event_id = resultSet.getLong("event_id");
                String url = resultSet.getString("event_url");
                String name = resultSet.getString("name");
                String location = resultSet.getString("location");
                Long organiser = resultSet.getLong("organiser");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                RoseEvent event = new RoseEvent();
                event.setId(event_id);
                event.setEventUrl(url);
                event.setEventName(name);
                event.setLocation(location);
                event.setOrganiser(organiser);
                event.setDate(date);

                events.add(event);
           }

                for(RoseEvent c: events) {
                    statement2.setLong(1,c.getId());
                    ResultSet resultSet2 = statement2.executeQuery();
                    while(resultSet2.next()) {
                    Long event_id = resultSet2.getLong("event_id");
                    Long participant_id = resultSet2.getLong("participant_id");
                        if (Objects.equals(c.getId(), event_id)) {
                            c.addParticipant(participant_id);
                        }
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public RoseEvent save(RoseEvent entity) {
        String sql = "insert into events (event_id, organiser, location, date, event_url, name) " +
                "values (?, ?, ?, ?, ?, ?)";
//        String sql2 = "SELECT * from events where events_id = ? and participant_id = ?";
        String sql2 = "insert into event_participants (event_id, participant_id) values (?, ?)";
//        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             PreparedStatement ps2 = connection.prepareStatement(sql2)) {

            for(Long participant : entity.getParticipants()) {
                ps2.setLong(1, entity.getId());
                ps2.setLong(2, participant);

                ps2.executeUpdate();
            }

            ps.setLong(1, entity.getId());
            ps.setLong(2,entity.getOrganiser());
            ps.setString(3,entity.getLocation());
            ps.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));
            ps.setString(5,entity.getEventUrl());
            ps.setString(6,entity.getEventName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
        return null;
    }

    @Override
    public RoseEvent delete(Long aLong) {
        String sql = "delete from events where event_id = ?";

        RoseEvent removedEvent = findOne(aLong);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aLong);

            ps.executeUpdate();
        } catch (SQLException e) {
            return null;
        }
        return removedEvent;
    }

    @Override
    public RoseEvent update(RoseEvent entity) {
        String sql = "update events set event_url = ?, name = ?, location = ?, date = ?, organiser = ? ,where event_id = ?";

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1,entity.getEventUrl());
            ps.setString(2,entity.getEventName());
            ps.setString(3,entity.getLocation());
            ps.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));
            ps.setLong(5,entity.getOrganiser());
            ps.setLong(6, entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<RoseEvent> findAll(Pageable pageable) {
        Paginator<RoseEvent> paginator = new Paginator<>(pageable, this.findAll());
        return paginator.paginate();
    }
}
