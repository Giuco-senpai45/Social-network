package main.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class represents for data from User and Message objects
 */
public class ChatDTO {

    /**
     * String representing the full name of the user.
     */
    private String userName;

    /**
     * String representing the body of the message.
     */
    private String message;

    /**
     * Timestamp representing the time when the message was sent
     */
    private Timestamp timestamp;

    /**
     * Long representing if the current Message is a reply or not
     */
    private Long replyID;
    private Long userID;

    private String repliedMessage;


    public ChatDTO(String userName, String message, Timestamp timestamp, Long replyID) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.replyID = replyID;
    }

    public ChatDTO(String userName, String message, Timestamp timestamp, Long replyID, Long userID) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.replyID = replyID;
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    /**
     * @param userName String representing the full name of the user.
     * @param message String representing the body of the message.
     * @param timestamp Timestamp representing the time when the message was sent
     * @param replyID Long representing if the current Message is a reply or not
     */


    public ChatDTO(String userName, String message, Timestamp timestamp, Long replyID, String replyMessage) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.replyID = replyID;
        this.repliedMessage = replyMessage;
    }

    /**
     * Getter for the User's name
     * @return String representing the name of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for the body of the message
     * @return String representing the body of the Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the Timestamp
     * @return Timestamp representing the time when Message was sent
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Overridden method for the toString method
     * @return String representing the DTO
     */
    @Override
    public String toString() {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        String formatedDate = sdf.format(date);
        if(replyID == -1)
            return userName + ", " + formatedDate + "\n" + "         " + message;
        else
            return userName + ", " + formatedDate + ", replied to: " + replyID +
                    "\n" + "         " + message;
    }
}
