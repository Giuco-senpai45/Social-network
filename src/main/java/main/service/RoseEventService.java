package main.service;

import javafx.event.EventHandler;
import main.domain.Post;
import main.domain.RoseEvent;
import main.domain.User;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.PageableImplementation;
import main.repository.paging.PagingRepository;

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


    public Set<RoseEvent> getEventsOnPage(int page,Long loggedUser){
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<RoseEvent> eventsPage = repoEvents.findAll(pageable);
//        Predicate<RoseEvent> testUser = ev -> ev.getParticipants().contains(loggedUser);
        return eventsPage.getContent().collect(Collectors.toSet());
    }
}
