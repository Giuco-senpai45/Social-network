package domain;

import java.sql.Timestamp;

public class ChatDTO {

    private String userName;
    private String message;
    private Timestamp timestamp;
    private Long replyID;


    public ChatDTO(String userName, String message, Timestamp timestamp, Long replyID) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.replyID = replyID;
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
        if(replyID == -1)
            return userName + ", " + timestamp + "\n" + "         " + message;
        else
            return userName + ", " + timestamp + ", replied to: " + replyID +
                    "\n" + "         " + message;
    }
}
