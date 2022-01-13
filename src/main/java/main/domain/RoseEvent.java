package main.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoseEvent extends Entity<Long>{
    private String description;
    private String eventName;
    private Long organiser;
    private String location;
    private String eventUrl;
    private LocalDateTime date;
    private List<Long> participants;

    public RoseEvent(String eventName, Long organiser, String location, String eventUrl, LocalDateTime date,String description) {
        this.eventName = eventName;
        this.organiser = organiser;
        this.location = location;
        this.eventUrl = eventUrl;
        this.date = date;
        this.description = description;
        this.participants = new ArrayList<>();
    }

    public RoseEvent() {
        this.participants = new ArrayList<>();
    }

    public void addParticipant(Long participant) {
        participants.add(participant);
    }

    public String getEventName() {
        return eventName;
    }

    public Long getOrganiser() {
        return organiser;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setOrganiser(Long organiser) {
        this.organiser = organiser;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return eventName + " " + location + " on " + date;
    }
}
