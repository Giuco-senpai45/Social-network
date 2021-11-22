package service;

import domain.*;
import repository.Repository;
import service.serviceExceptions.FindException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void addMessage(Long id, String message, List<Long> chatters){
        String errors = "";
        for(Long chatterID: chatters) {
            User user = repoUsers.findOne(chatterID);
            if (user == null)
                errors = errors + "The user " + chatterID + " doesn't exist!\n";
        }
        if(!errors.equals(""))
            throw new FindException(errors);

        Chat chat = null;
        for(Chat c: repoChats.findAll()){
            int count = 0;
            for(Long chatterID: chatters) {
                if(c.getChatUsers().contains(chatterID))
                    count++;
            }
            if(count == chatters.size())
                chat = c;
        }

        if(chat == null) {
            chat = new Chat();
            chat.setId(maximChatId() + 1);
        }
        chat.addUserToChat(id);
        for(Long chatterID: chatters) {
            chat.addUserToChat(chatterID);
        }
        repoChats.save(chat);

        Long nextID = maximUserID() + 1;
        Message msg = new Message(id, message, Timestamp.valueOf(LocalDateTime.now()), -1L, chat.getId());
        msg.setId(nextID);
        repoMessages.save(msg);
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
        return maxID;
    }

  
    public void replyMessage(Long id, String message, Long messageIDforReply){
        verifyReplyForMessage(id, messageIDforReply);
        Message m = repoMessages.findOne(messageIDforReply);
        Long nextID = maximUserID() + 1;
        Message msg = new Message(id, message, Timestamp.valueOf(LocalDateTime.now()), messageIDforReply, m.getChatID());
        msg.setId(nextID);
        repoMessages.save(msg);
    }

    private void verifyReplyForMessage(Long userID, Long messageID){
        List<Long> messagesForReply = messagesToReplyForUser(userID);
        if(!messagesForReply.contains(messageID))
            throw new FindException("You can't reply to this message!\n");
    }


    public List<Long> messagesToReplyForUser(Long userID){
        Iterable<Chat> chats = repoChats.findAll();
        ArrayList<Chat> listChats = new ArrayList<>();
        chats.forEach(listChats::add);

        Predicate<Chat> testUserInChat = f -> f.getChatUsers().contains(userID);

        List<Long> cts = listChats.stream()
                .filter(testUserInChat)
                .map(Entity::getId)
                .collect(Collectors.toList());

        Iterable<Message> messages = repoMessages.findAll();
        ArrayList<Message> listMessages = new ArrayList<>();
        messages.forEach(listMessages::add);

        Predicate<Message> testMessageInChat = f -> cts.contains(f.getChatID());
        Predicate<Message> testMessageFromUser = f -> !Objects.equals(f.getUser(), userID);
        Predicate<Message> testBoth = testMessageInChat.and(testMessageFromUser);

        return listMessages.stream()
                .filter(testBoth)
                .map(Entity::getId)
                .collect(Collectors.toList());
    }


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
            if(user == null)
                return "Deleted user";
            else
                return user.getFirstName() + " " + user.getLastName();
        };

        return messagesList.stream()
                .filter(testIsInChat)
                .map(m-> new ChatDTO(getName.apply(m.getUser()) , m.getMessage(), m.getTimeOfMessage(), m.getReplyId()))
                .sorted(MessageService::compareTime)
                .collect(Collectors.toList());
    }

}
