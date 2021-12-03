package domain;

import java.util.Objects;

/**
 * @param <E1> Generic type representing the first element in the tuple
 * @param <E2> Generic type representing the second element in the tuple
 */
public class Tuple <E1 , E2>{
    /**
     * The first element
     */
    private E1 e1;
    /**
     * The second element
     */
    private E2 e2;

    /**
     * @return E1 representing the first element in the tuple
     */
    public E1 getE1() {
        return e1;
    }

    /**
     * @return E2 representing the second element in the tuple
     */
    public E2 getE2() {
        return e2;
    }

    /**
     * @param e1 Generic type element representing the first element of the tuple
     * @param e2 Generic type element representing the second element of the tuple
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * @param o object to be compared to
     * @return boolean
     *  -true if the tuples are equal
     *  -false if the tuples are not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(e1, tuple.e1) && Objects.equals(e2, tuple.e2);
    }

    /**
     * @return integer representing the hash value of the given parameters
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
