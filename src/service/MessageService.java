package service;

import domain.*;
import repository.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessageService {

    private Repository<Tuple<Long,Long>, Friendship> repoFriends;
    private Repository<Long, User> repoUsers;
    private Repository<Long, Message> repoMessages;
    private Repository<Long, Chat> repoChats;

    public MessageService(Repository<Tuple<Long, Long>, Friendship> repoFriends, Repository<Long, User> repoUsers, Repository<Long, Message> messageRepository, Repository<Long, Chat> chatRepository) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoMessages = messageRepository;
        this.repoChats = chatRepository;
    }


    public void addMessage(Long id, String message, Long toID){
        //TODO validari
        Long nextID = maximID() + 1;
        Message msg = new Message(id, message, Timestamp.valueOf(LocalDateTime.now()), -1L);
        repoMessages.save(msg);

        Chat chat = verifyChat(id, toID);
        if(chat == null) {
            chat = new Chat();
            chat.setId(maximChatId() + 1);
        }
        chat.addPair(id, nextID);
        //TODO functia save pentru chat nu salveaza bine
        repoChats.save(chat);

        System.out.println("Message sent successfully");
    }

    private Chat verifyChat(Long fromID, Long toID){
        for(Chat chat: repoChats.findAll()){
            for(Tuple<Long, Long> pair: chat.getPairUserMessage())
                if(pair.getE1() == fromID && pair.getE2() == toID || pair.getE2() == fromID && pair.getE1() ==toID)
                    return chat;
        }
        return null;
    }

    private Long maximID() {
        Long maxID = 0L;
        for(Message msg: repoMessages.findAll()){
            if(msg.getId() > maxID)
                maxID = msg.getId();
        }
        return maxID;
    }

    private Long maximChatId() {
        Long maxID = 0L;
        for(Chat chat: repoChats.findAll()){
            if(chat.getId() > maxID)
                maxID = chat.getId();
        }
        return maxID;
    }

//    public List<Long> chatsForUser(Long id){
//        Iterable<Chat> chats = repoChats.findAll();
//        ArrayList<Chat> listChats = new ArrayList<>();
//        chats.forEach(listChats::add);
//
//        Predicate<Chat> testUserInChat = f -> Objects.equals(f.getId(), id);
//
//        return listChats.stream()
//                .filter(testUserInChat)
//                .map(Entity::getId)
//                .collect(Collectors.toList());
//    }

    public static int compareById(ChatDTO a, ChatDTO b){
        return (int) a.getTimestamp().compareTo(b.getTimestamp());
    }

    public List<ChatDTO> getConversation(Long id){
        Chat chat = repoChats.findOne(id);
        List<Tuple<Long, Long>> messages = chat.getPairUserMessage();
        Function<Long, String> getName = x -> repoUsers.findOne(x).getLastName() + " "
                + repoUsers.findOne(x).getFirstName();
        Function<Long, String> getMessage = x -> repoMessages.findOne(x).getMessage();
        Function<Long, Timestamp> getTime = x -> repoMessages.findOne(x).getTimeOfMessage();

        return messages.stream()
                .map(m -> new ChatDTO(getName.apply(m.getE1()),getMessage.apply(m.getE2()), getTime.apply(m.getE2())))
                .sorted(MessageService::compareById)
                .collect(Collectors.toList());
    }


}
