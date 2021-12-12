package main.domain;

import java.time.LocalDate;

/**
 * Class of Data Transfer Object type which contains the name of a friend of a specific user and the date of the beginning of their friendship
 */
public class UserFriendshipsDTO {
    private Long friendID;
    private String friendFirstName;
    private String friendLastName;
    private LocalDate date;

    /**
     * Overloaded constructor
     * @param friendFirstName String representing the first name of a user
     * @param friendLastName String representing the last name of a user
     * @param date LocalDate representing the beginning of a friendship
     */
    public UserFriendshipsDTO(String friendFirstName, String friendLastName, LocalDate date, Long friendID) {
        this.friendID = friendID;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.date = date;
    }

    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getFriendID() {
        return friendID;
    }

    @Override
    public String toString() {
        return friendFirstName + " | " + friendLastName + " | " + date;
    }
}
