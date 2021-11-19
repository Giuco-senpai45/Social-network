package domain;

import java.sql.Timestamp;

public class ChatDTO {

    private String userName;
    private String message;
    private Timestamp timestamp;
    private Long replyID;

    //TODO adaugare parte de reply in DTO si in tabela de chats I guess??

    public ChatDTO(String userName, String message, Timestamp timestamp) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return userName + ", " + timestamp + "\n" + "         " + message;

    }
}
