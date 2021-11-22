package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends Entity<Long>{

    private List<Long> chatUsers;

    public Chat(){
        chatUsers = new ArrayList<>();
    }

    public void addUserToChat(Long userID){
        chatUsers.add(userID);
    }

    public List< Long> getChatUsers() {
        return chatUsers;
    }

    @Override
    public String toString() {
        String users = "";
        for(Long user: getChatUsers())
            users = users + user + " , ";
        return "Chat: " + "pairUserMessage = " + users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(chatUsers, chat.chatUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatUsers);
    }
}
