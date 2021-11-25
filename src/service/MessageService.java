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

/**
 * Service class for Messages and Chat
 */
public class MessageService {

    /**
     * repository for Friendship entities
     */
    private Repository<Tuple<Long,Long>, Friendship> repoFriends;
    /**
     * repository for User entities
     */
    private Repository<Long, User> repoUsers;
    /**
     * repository for Message entities
     */
    private Repository<Long, Message> repoMessages;
    /**
     * repository for Chat entities
     */
    private Repository<Long, Chat> repoChats;

    /**
     * Overloaded constructor
     * @param repoFriends repository for Friendship entities
     * @param repoUsers repository for User entities
     * @param messageRepository repository for Message entities
     * @param chatRepository repository for Chat entitites
     */
    public MessageService(Repository<Tuple<Long, Long>, Friendship> repoFriends, Repository<Long, User> repoUsers, Repository<Long, Message> messageRepository, Repository<Long, Chat> chatRepository) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoMessages = messageRepository;
        this.repoChats = chatRepository;
    }

    /**
     * This function sends a message from a specified user to another user(s)
     * Checks if there is already a chat between the given users and creates one if not
     * @param id Long parameter which represents the id of the sender
     * @param message String message which represents the message that user want to send
     * @param chatters List of Longs representing the ids of the receivers
     */
    public void sendMessage(Long id, String message, List<Long> chatters){
        String errors = "";
        for(Long chatterID: chatters) {
            User user = repoUsers.findOne(chatterID);
            if (user == null)
                errors = errors + "The user " + chatterID + " doesn't exist!\n";
        }
        if(!errors.equals(""))
            throw new FindException(errors);

        chatters.add(id);
        Chat chat = null;
        for(Chat c: repoChats.findAll()){
            int count = 0;
            for(Long chatterID: chatters) {
                if(c.getChatUsers().contains(chatterID))
                    count++;
            }
            if(count == chatters.size() && chatters.size() == c.getChatUsers().size())
                chat = c;
        }

        if(chat == null) {
            chat = new Chat();
            chat.setId(maximChatId() + 1);
        }
        for(Long chatterID: chatters) {
            chat.addUserToChat(chatterID);
        }
        repoChats.save(chat);

        Long nextID = maximMessageID() + 1;
        Message msg = new Message(id, message, Timestamp.valueOf(LocalDateTime.now()), -1L, chat.getId());
        msg.setId(nextID);
        repoMessages.save(msg);
    }

    /**
     * Computes the maximum id from the message database
     * @return Long representing the required id
     */
    private Long maximMessageID() {
        Long maxID = 0L;
        for(Message msg: repoMessages.findAll()){
            if(msg.getId() > maxID)
                maxID = msg.getId();
        }
        return maxID;
    }

    /**
     * Computes the maximum id from the chat database
     * @return Long representing the required id
     */
    private Long maximChatId() {
        Long maxID = 0L;
        for(Chat chat: repoChats.findAll()){
            if(chat.getId() > maxID)
                maxID = chat.getId();
        }
        return maxID;
    }

    /**
     * This function sends a reply from a given user for one specific message
     * @param userID Long parameter which represents the id of the sender
     * @param message String parameter which represents the message the user want to send
     * @param messageIDforReply Long parameter which represents the id of the message the reply is sent to
     */
    public void replyMessage(Long userID, String message, Long messageIDforReply){
        verifyReplyForMessage(userID, messageIDforReply);
        Message m = repoMessages.findOne(messageIDforReply);
        Long nextID = maximMessageID() + 1;
        Message msg = new Message(userID, message, Timestamp.valueOf(LocalDateTime.now()), messageIDforReply, m.getChatID());
        msg.setId(nextID);
        repoMessages.save(msg);
    }

    /**
     * This function checks if the specified User can reply to the specified Message ID.
     * @param userID Long representing the Users ID
     * @param messageID Long representing the Messages ID
     * @throws FindException if the User can't reply to that message
     */
    private void verifyReplyForMessage(Long userID, Long messageID){
        List<Long> messagesForReply = messagesToReplyForUser(userID);
        if(!messagesForReply.contains(messageID))
            throw new FindException("You can't reply to this message!\n");
    }

    /**
     * This function returns a list of IDs that represent the IDs of Messages the specified User can reply to
     * @param userID Long representing the Users ID
     * @return List of Longs representing the ID's of the messages to which the specified user can reply to
     */
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

    /**
     * This function compares the time at which 2 message where sent.
     * @param a ChatDTO representing a message sent by a user
     * @param b ChatDTO representing a message sent by a user
     * @return
     *      -the value 0 if the two Timestamp objects are equal;
     *      -a value less than 0 if this Timestamp object is before the given argument;
     *      -and a value greater than 0 if this Timestamp object is after the given argument.
     */
    public static int compareTime(ChatDTO a, ChatDTO b){
        return  a.getTimestamp().compareTo(b.getTimestamp());
    }

    /**
     * This function returns a List of ChatDTOs that are going to represent
     * Messages sent in a specified Chat and the users who sent them
     * @param id Long representing the ID of the Chat entity
     * @return List of ChatDTO entities
     */
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
