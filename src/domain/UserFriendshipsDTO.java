package domain;

import java.time.LocalDate;

public class UserFriendshipsDTO {
    private String friendFirstName;
    private String friendLastName;
    private LocalDate date;

    public UserFriendshipsDTO(String friendFirstName, String friendLastName, LocalDate date) {
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

    @Override
    public String toString() {
        return friendFirstName + " | " + friendLastName + " | " + date;
    }
}
