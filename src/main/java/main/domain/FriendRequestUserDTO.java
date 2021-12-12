package main.domain;

import java.time.LocalDate;

public class FriendRequestUserDTO {
    private String userName;
    private Long friendRequestID;
    private Long userId;
    private String status;
    private LocalDate time;

    public FriendRequestUserDTO(String userName,Long userId, Long friendRequestID,String status,LocalDate time) {
        this.userName = userName;
        this.userId = userId;
        this.friendRequestID = friendRequestID;
        this.status = status;
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public String getUserName() {
        return userName;
    }

    public Long getFriendRequestID() {
        return friendRequestID;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return userName + " " + time + " " + status;
    }
}
