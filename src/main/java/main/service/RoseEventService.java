package main.service;

import javafx.event.Event;
import javafx.event.EventHandler;
import main.domain.Post;
import main.domain.RoseEvent;
import main.domain.User;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.PageableImplementation;
import main.repository.paging.PagingRepository;

import java.sql.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoseEventService {

    private PagingRepository<Long, User> repoUsers;
    private PagingRepository<Long, RoseEvent> repoEvents;
    private int page = 0;
    private int size = 1;
    private Pageable pageable;

    private  String url;
    private  String username;
    private  String password;

    public RoseEventService(PagingRepository<Long, User> repoUsers, PagingRepository<Long, RoseEvent> repoEvents, String url, String username, String password) {
        this.repoUsers = repoUsers;
        this.repoEvents = repoEvents;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public RoseEventService(PagingRepository<Long, User> repoUsers, PagingRepository<Long, RoseEvent> repoEvents) {
        this.repoUsers = repoUsers;
        this.repoEvents = repoEvents;
    }

    public int numberOfPagesForEvents(Long loggedUser){
        Collection<RoseEvent> collection = ((Collection<RoseEvent>) repoEvents.findAll())
                .stream().collect(Collectors.toList());
        int postsNumber = collection.size();
        int pagesNumber = postsNumber % 3 != 0 ? (postsNumber/3 + 1) : postsNumber/3;
        return pagesNumber;
    }

    public void setPageSize(int size) {
        this.size = size;
    }

    public void addUserToEvent(Long userId, RoseEvent event){
        String sql = "insert into event_participants (event_id, participant_id) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1,event.getId());
            ps.setLong(2,userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeUserFromEvent(Long userId, RoseEvent event){
        String sql = "delete from event_participants where event_id = ? and participant_id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1,event.getId());
            ps.setLong(2,userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Set<RoseEvent> getEventsOnPage(int page,Long loggedUser){
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<RoseEvent> eventsPage = repoEvents.findAll(pageable);
//        Predicate<RoseEvent> testUser = ev -> ev.getParticipants().contains(loggedUser);
        return eventsPage.getContent().collect(Collectors.toSet());
    }
}
