package main.domain;

public class Post extends Entity<Long>{

    private String postURL;
    private Long userID;

    public Post(String postURL, Long userID) {
        this.postURL = postURL;
        this.userID = userID;
    }

    public String getPostURL() {
        return postURL;
    }

    public Long getUserID() {
        return userID;
    }
}
