package domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents the Friendship between 2 Users
 * It extends the Entity, the friendship being an entity
 * with a Tuple of longs representing the id's of the users that are friends
 */
public class Friendship extends Entity<Tuple<Long, Long>>{
    /**
     * The id of the first friend
     */
    private Long buddy1;
    /**
     * The id of the second friend
     */
    private Long buddy2;

    /**
     * String representing the date this friendship was made
     */
    private LocalDate date;

    /**
     * @param buddy1 Long representing the id of the first friend
     * @param buddy2 Long representing the id of the second friend
     */
    public Friendship(Long buddy1, Long buddy2) {
        this.buddy1 = buddy1;
        this.buddy2 = buddy2;
        date = LocalDate.now();
        setId(new Tuple(buddy1,buddy2));
    }

    public Friendship() {

    }

    /**
     * @return String representing the date when this friendship was made
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date String representing the date that the date of the current entity is set to
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return Long representing the Id of the first friend
     */
    public Long getBuddy1() {
        return buddy1;
    }

    /**
     * @return Long representing the Id of the second friend
     */
    public Long getBuddy2() {
        return buddy2;
    }

    /**
     * @return String representing the friendship
     */
    @Override
    public String toString() {
        return buddy1 + " " + buddy2 + " " + " friends since " + date;
    }

    /**
     * @param o object to be compared to
     * @return boolean
     *  -true if the friendship are equal
     *  -false if the friendships are not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        //The friendship is the same if the friendship is 1->2 or 2->1
        return ((Objects.equals(buddy1, that.buddy1) && Objects.equals(buddy2, that.buddy2))
                || (Objects.equals(buddy1, that.buddy2) && Objects.equals(buddy2, that.buddy1)));
    }

    /**
     * @return integer representing the hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(buddy1, buddy2);
    }
}