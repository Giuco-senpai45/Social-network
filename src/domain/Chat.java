package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chat extends Entity<Long>{

    private List<Tuple<Long, Long>> pairUserMessage;

    public Chat(){
        pairUserMessage = new ArrayList<>();
    }

    public void addPair(Long userID, Long messageID){
        Tuple<Long, Long> pair = new Tuple<>(userID, messageID);
        pairUserMessage.add(pair);
    }

    public List<Tuple<Long, Long>> getPairUserMessage() {
        return pairUserMessage;
    }

    @Override
    public String toString() {
        String pairs = "";
        for(Tuple<Long, Long> pair: getPairUserMessage())
            pairs = pairs + pair.getE1().toString() + " " + pair.getE2().toString() + ", ";
        return "Chat: " + "pairUserMessage = " + pairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(pairUserMessage, chat.pairUserMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pairUserMessage);
    }
}
