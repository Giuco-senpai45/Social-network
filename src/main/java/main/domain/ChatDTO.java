package main.domain;

import java.sql.Timestamp;

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


    /**
     * @param userName String representing the full name of the user.
     * @param message String representing the body of the message.
     * @param timestamp Timestamp representing the time when the message was sent
     * @param replyID Long representing if the current Message is a reply or not
     */
    public ChatDTO(String userName, String message, Timestamp timestamp, Long replyID) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
        this.replyID = replyID;
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
        if(replyID == -1)
            return userName + ", " + timestamp + "\n" + "         " + message;
        else
            return userName + ", " + timestamp + ", replied to: " + replyID +
                    "\n" + "         " + message;
    }
}
