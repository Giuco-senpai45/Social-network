package main.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a Chat entitu.
 * This entity will have an ID for the Chat and an ID for the User that is in the chat.
 * e.g:
 * 1 2
 * 1 3
 * This signifies that User 2 and 3 are in the Chat with the ID 1
 */
public class Chat extends Entity<Long>{

    /**
     * This List of Longs represents the users that are present in this Chat
     */
    private List<Long> chatUsers;
    private String url;
    private String name;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Default constructor, the chat is initially empty
     */
    public Chat(){
        chatUsers = new ArrayList<>();
    }

    /**
     * This function adds the User as a member of this Chat
     * @param userID Long representing the ID of an existing User
     */
    public void addUserToChat(Long userID){
        chatUsers.add(userID);
    }

    /**
     * This function returns a list of the users members of this Chat
     * @return List of Longs representing the Users in the Chat
     */
    public List< Long> getChatUsers() {
        return chatUsers;
    }

    /**
     * Overridden toString
     * @return String representing the Chat entity
     */
    @Override
    public String toString() {
        String users = "";
        for(Long user: getChatUsers())
            users = users + user + " , ";
        return "Chat: " + "pairUserMessage = " + users;
    }

    /**
     * @param o Entity to be compared
     * @return boolean (true if the objects are equal / false if the objects are not equal)
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(chatUsers, chat.chatUsers);
    }

    /**
     * @return int representing the hash of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(chatUsers);
    }
}
