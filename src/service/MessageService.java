package service;

import domain.*;
import repository.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public void addMessage(Long id, String message, Long toUserId){
        Chat chat = verifyIfChatExists(id, toUserId);
        if(chat == null) {
            System.out.println("Il creez ");
            chat = new Chat();
            chat.setId(maximChatId() + 1);
        }
        chat.addUserToChat(id);
        chat.addUserToChat(toUserId);
        System.out.println(chat + " gasit ");
        repoChats.save(chat);

        Long nextID = maximUserID() + 1;
        Message msg = new Message(id, message, Timestamp.valueOf(LocalDateTime.now()), -1L, chat.getId());
        msg.setId(nextID);
        repoMessages.save(msg);

        System.out.println("Message sent successfully");
    }

    private Chat verifyIfChatExists(Long fromID, Long toID){
        boolean foundSender = false;
        boolean foundReceiver = false;
        for(Chat chat: repoChats.findAll()){
            for(Long user: chat.getChatUsers()){
                if(user == fromID){
                    foundSender = true;
                }
                if(user == toID){
                    foundReceiver = true;
                }
            }
            if(foundSender && foundReceiver) {
                return chat;
            }
            foundSender = false;
            foundReceiver = false;
        }
        return null;
    }

    private Long maximUserID() {
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
//        System.out.println("Idul maxim de chat " + maxID);
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

    public static int compareTime(ChatDTO a, ChatDTO b){
        return  a.getTimestamp().compareTo(b.getTimestamp());
    }

    public List<ChatDTO> getConversation(Long id){
        Iterable<Message> messages = repoMessages.findAll();
        List<Message> messagesList = new ArrayList<>();
        messages.forEach(messagesList::add);

        Predicate<Message> testIsInChat = m -> m.getChatID().equals(id);
        Function<Long, String> getName = x ->{
            User user = repoUsers.findOne(x);
            return user.getFirstName() + " " + user.getLastName();
        };

        return messagesList.stream()
                .filter(testIsInChat)
                .map(m-> new ChatDTO(getName.apply(m.getUser()) , m.getMessage(), m.getTimeOfMessage()))
                .sorted(MessageService::compareTime)
                .collect(Collectors.toList());
    }
}
