package main.utils;

import main.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}