package main.service;

import main.domain.Post;
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

public class PostService {

    private PagingRepository<Long, User> repoUsers;
    private PagingRepository<Long, Post> repoPosts;

    public PostService(PagingRepository<Long, User> repoUsers, PagingRepository<Long, Post> repoPosts) {
        this.repoUsers = repoUsers;
        this.repoPosts = repoPosts;
    }

    public int numberOfPagesForPosts(Long loggedUser){
        Predicate<Post> testUser = x -> Objects.equals(x.getUserID(), loggedUser);
        Collection<Post> collection = ((Collection<Post>) repoPosts.findAll())
                .stream().filter(testUser).collect(Collectors.toList());
        int postsNumber = collection.size();
        int pagesNumber = postsNumber % 3 != 0 ? (postsNumber/3 + 1) : postsNumber/3;
        return pagesNumber;
    }

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

//    public Set<Post> getNextPosts() {
////        Pageable pageable = new PageableImplementation(this.page, this.size);
////        Page<MessageTask> studentPage = repo.findAll(pageable);
////        this.page++;
////        return studentPage.getContent().collect(Collectors.toSet());
//        this.page++;
//        return getPostsOnPage(this.page);
//    }

    public Set<Post> getPostsOnPage(int page, Long loggedUser) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Post> postsPage = repoPosts.findAll(pageable);
        Predicate<Post> testUser = x -> Objects.equals(x.getUserID(), loggedUser);
        return postsPage.getContent().filter(testUser).collect(Collectors.toSet());
    }
}
