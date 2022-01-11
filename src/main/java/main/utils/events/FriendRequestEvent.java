package main.utils.events;

import main.domain.FriendRequest;

public class FriendRequestEvent implements Event{
    private ChangeEventType type;
    private FriendRequest data,oldData;

    public FriendRequestEvent(ChangeEventType type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }

    public FriendRequestEvent(ChangeEventType type, FriendRequest data, FriendRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendRequest getData() {
        return data;
    }

    public FriendRequest getOldData() {
        return oldData;
    }
}
