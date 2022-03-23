package main.domain;

public class Login extends Entity<String>{

    private String password;
    private Long userID;

    public Login(String password, Long userID) {
        this.password = password;
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
