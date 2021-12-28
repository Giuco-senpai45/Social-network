package main.utils.events;

import main.domain.Friendship;

public class FriendDeletionEvent implements Event{
    private ChangeEventType type;
    private Friendship data, oldData;

    public FriendDeletionEvent(ChangeEventType type, Friendship user){
        this.type = type;
        this.data = user;
    }

    public FriendDeletionEvent(ChangeEventType type, Friendship user, Friendship oldUser) {
        this.type = type;
        this.data = user;
        this.oldData = oldUser;
    }

    public ChangeEventType getType() { return type; }

    public Friendship getData() { return data; }

    public Friendship getOldData() { return oldData; }
}
