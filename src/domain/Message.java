package domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{

    private Long user;
    private List<Long> chatters;
    private String message;
    private Timestamp timeOfMessage;
    private Long replyId;
    private Long chatID;

    public Message(Long user, String message, Timestamp timeOfMessage, Long replyId,Long chatID) {
        this.user = user;
        this.message = message;
        this.timeOfMessage = timeOfMessage;
        this.replyId = replyId;
        this.chatID = chatID;
    }

    //TODO chatters - lista de destinatari

    public Long getUser() {
        return user;
    }

    public List<Long> getChatters() {
        return chatters;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimeOfMessage() {
        return timeOfMessage;
    }

    public Long getReplyId() {
        return replyId;
    }

    public Long getChatID() {
        return chatID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", chatters=" + chatters +
                ", message='" + message + '\'' +
                ", timeOfMessage=" + timeOfMessage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(user, message1.user) && Objects.equals(chatters, message1.chatters)
                && Objects.equals(message, message1.message)
                && Objects.equals(timeOfMessage, message1.timeOfMessage)
                && Objects.equals(replyId, message1.replyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, chatters, message, timeOfMessage, replyId);
    }
}
