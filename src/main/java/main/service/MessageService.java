package main.service;

import main.domain.*;
import main.repository.paging.Page;
import main.repository.paging.Pageable;
import main.repository.paging.PageableImplementation;
import main.repository.paging.PagingRepository;
import main.service.serviceExceptions.FindException;
import main.utils.Observable;
import main.utils.Observer;
import main.utils.events.ChangeEventType;
import main.utils.events.MessageEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for Messages and Chat
 */
public class MessageService implements Observable<MessageEvent> {

    /**
     * repository for Friendship entities
     */
    private PagingRepository<Tuple<Long,Long>, Friendship> repoFriends;
    /**
     * repository for User entities
     */
    private PagingRepository<Long, User> repoUsers;
    /**
     * repository for Message entities
     */
    private PagingRepository<Long, Message> repoMessages;
    /**
     * repository for Chat entities
     */
    private PagingRepository<Long, Chat> repoChats;

    private String username;
    private  String password;
    private String url;

    /**
     * Overloaded constructor
     * @param repoFriends repository for Friendship entities
     * @param repoUsers repository for User entities
     * @param messageRepository repository for Message entities
     * @param chatRepository repository for Chat entitites
     */
    public MessageService(PagingRepository<Tuple<Long, Long>, Friendship> repoFriends, PagingRepository<Long, User> repoUsers, PagingRepository<Long, Message> messageRepository, PagingRepository<Long, Chat> chatRepository) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoMessages = messageRepository;
        this.repoChats = chatRepository;
    }

    public MessageService(PagingRepository<Tuple<Long, Long>, Friendship> repoFriends, PagingRepository<Long, User> repoUsers, PagingRepository<Long, Message> messageRepository, PagingRepository<Long, Chat> chatRepository,String url,String username,String password) {
        this.repoFriends = repoFriends;
        this.repoUsers = repoUsers;
        this.repoMessages = messageRepository;
        this.repoChats = chatRepository;
        this.username = username;
        this.password = password;
        this.url = url;
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

        Chat chat = null;
        for(Chat c: repoChats.findAll()){
            int count = 0;
            for(Long chatterID: chatters) {
                if(c.getChatUsers().contains(chatterID))
                    count++;
            }
            if(count == chatters.size() && chatters.size() == c.getChatUsers().size()){
                chat = c;
            }
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
        notifyObservers(new MessageEvent(ChangeEventType.ADD, msg));
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
        notifyObservers(new MessageEvent(ChangeEventType.ADD, msg));
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

        Function<Long, String> getRepliedMessage = x ->{
            Message msg = repoMessages.findOne(x);
            if(msg == null){
                return "Message doesn't exist";
            }
            else {
                return msg.getMessage();
            }
        };

        return messagesList.stream()
                .filter(testIsInChat)
                .map(m-> new ChatDTO(getName.apply(m.getUser()) , m.getMessage(), m.getTimeOfMessage(), m.getId(), m.getReplyId(),m.getUser(),getRepliedMessage.apply(m.getReplyId())))
                .sorted(MessageService::compareTime)
                .collect(Collectors.toList());
    }

    private static int compareByLastMessage(List<ChatDTO> a, List<ChatDTO> b){
        return (int) a.get(a.size()-1).getTimestamp().compareTo(b.get(b.size()-1).getTimestamp());
    }

    public List<Chat> getChatsForUser(Long id){
        Iterable<Chat> chats = repoChats.findAll();
        List<Chat> chatsList = new ArrayList<>();
        chats.forEach(chatsList::add);

        List<Chat> userChats = new ArrayList<>();

        for(Chat c : chatsList){
            List<Long> users = c.getChatUsers();
            boolean found = false;
            for(Long u : users){
                if (u.equals(id)) {
                    found = true;
                    break;
                }
            }
            if(found){
                userChats.add(c);
            }
        }

        Predicate<Chat> testIfChatNotEmpty = x -> getConversation(x.getId()).size() != 0;

        return userChats.stream()
                .filter(testIfChatNotEmpty)
                .sorted((y, x) -> compareByLastMessage(getConversation(x.getId()), getConversation(y.getId())))
                .toList();
    }

    public List<Chat> getAllChatsForUser(Long id, Long chatID){
        List<Chat> userChats = new ArrayList<>(getChatsForUser(id));
        Chat emptyChat = repoChats.findOne(chatID);
        userChats.add(0, emptyChat);
        return userChats;
    }

    public boolean testIfChatEmpty(Long userID, Long chatID){
        Chat checkEmptyChat = repoChats.findOne(chatID);
        if(getChatsForUser(userID).contains(checkEmptyChat))
            return false;
        return true;
    }



    public Tuple<String,String> getPrivateChatData(Long loggedUser,Chat chat){
        if(chat.getChatUsers().size() !=2){
            return null;
        }
        for(Long id : chat.getChatUsers()){
            if(!id.equals(loggedUser)){
                User otherUser = repoUsers.findOne(id);
                return new Tuple<>(otherUser.getFirstName() + " " + otherUser.getLastName(),
                        otherUser.getImageURL());
            }
        }
        return null;
    }

    public void updateChat(Chat chat){
        Chat updatedChat = repoChats.update(chat);
        if(updatedChat!=null){
            throw new FindException("Failed to update the chat");
        }
    }

    public void updateChatForUser(Chat chat, Long otherUser){
        String sql = "update chats set url = ?, name = ? where chat_id = ? and user_id = ?";

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setString(1, chat.getUrl());
            ps.setString(2, chat.getName());
            ps.setLong(3, chat.getId());
            ps.setLong(4, otherUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long createPrivateChatWithUser(Long loggedUser,Long otherUser){
        Iterable<Chat> chatsIterable = repoChats.findAll();
        List<Chat> chatsList = new ArrayList<>();
        chatsIterable.forEach(chatsList::add);
        Predicate<Chat> testIsPrivateChat = c -> c.getChatUsers().size() == 2;
        Predicate<Chat> testIsPrivateChatForUsers = c-> c.getChatUsers().contains(loggedUser) &&c.getChatUsers().contains(otherUser);
        Predicate<Chat> testChatExists =c -> testIsPrivateChat.and(testIsPrivateChatForUsers).test(c);
        chatsList =  chatsList.stream()
                .filter(testChatExists)
                .collect(Collectors.toList());
        if(chatsList.size() == 0){
            System.out.println("Creating new chat");
            Chat newChat = new Chat();
            newChat.setId(maximChatId() + 1);
            newChat.addUserToChat(loggedUser);
            newChat.addUserToChat(otherUser);
            newChat.setName("Rose");
            newChat.setUrl("/imgs/rose3.jpg");
            repoChats.save(newChat);
            return newChat.getId();
        }
        else {
            return chatsList.get(0).getId();
        }
    }

    public Chat createNewChatGroup(List<Long> chatters){
        Chat chat = null;
        for(Chat c: repoChats.findAll()){
            int count = 0;
            for(Long chatterID: chatters) {
                if(c.getChatUsers().contains(chatterID))
                    count++;
            }
            if(count == chatters.size() && chatters.size() == c.getChatUsers().size()){
                chat = c;
            }
        }
/*
select * from messages
where messages.chat_id = 1
order by messages.date_time;
 */
        if(chat == null) {
            chat = new Chat();
            chat.setId(maximChatId() + 1);
        }
        for(Long chatterID: chatters) {
            chat.addUserToChat(chatterID);
        }
        chat.setName("RoseGroup");
        chat.setUrl("/imgs/rose3.jpg");
        repoChats.save(chat);
        return chat;
    }

    public void deleteMessage(Long messageID){
        Message deletedMessage = repoMessages.delete(messageID);
        if(deletedMessage != null)
            notifyObservers(new MessageEvent(ChangeEventType.DELETE, deletedMessage));
    }

    public Message findOneMessage(Long messageID){
        return repoMessages.findOne(messageID);
    }

    /* Activitatile unui utilizator dintr-o perioada calendaristica, referitor la
    prietenii noi creati si mesajele primite in acea perioada*/

     public List<ChatDTO> report12(Long loggedUser, LocalDateTime beginningDate, LocalDateTime endDate){
         Iterable<Message> messgs = repoMessages.findAll();
         List<Message> messageList = new ArrayList<>();
         messgs.forEach(messageList::add);
         List<Chat> chats = getChatsForUser(loggedUser);
         List<ChatDTO> messages = new ArrayList<>();
         for(Chat chat: chats){
             for(ChatDTO chatDTO: getConversation(chat.getId())){
                 if((chatDTO.getTimestamp().compareTo(Timestamp.valueOf(beginningDate)))>0 && (chatDTO.getTimestamp().compareTo(Timestamp.valueOf(endDate)))<0 && !Objects.equals(chatDTO.getUserID(), loggedUser))
                    messages.add(chatDTO);
             }
         }
         return messages;
    }

    public List<ChatDTO> report2(Long loggedUser, Long fromID, LocalDateTime beginningDate, LocalDateTime endDate){
         Iterable<Message> messgs = repoMessages.findAll();
         List<Message> messageList = new ArrayList<>();
         messgs.forEach(messageList::add);
         List<Chat> chats = getChatsForUser(loggedUser);
         List<ChatDTO> messages = new ArrayList<>();
         for(Chat chat: chats){
            for(ChatDTO chatDTO: getConversation(chat.getId())){
                if(Objects.equals(chatDTO.getUserID(), fromID) && ( (chatDTO.getTimestamp().compareTo(Timestamp.valueOf(beginningDate)))>0 && (chatDTO.getTimestamp().compareTo(Timestamp.valueOf(endDate)))<0))
                    messages.add(chatDTO);
            }
         }
         return messages;
    }

    public Iterable<Message> getMessages(){
        return repoMessages.findAll();
    }

    private List<Observer<MessageEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }


    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

    public Set<Message> getNextUsers() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getMessagesOnPage(this.page);
    }

    public Set<Message> getMessagesOnPage(int page) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Message> messagesPage = repoMessages.findAll(pageable);
        return messagesPage.getContent().collect(Collectors.toSet());
    }

    public Chat findOneChat(Long chatID){
        return repoChats.findOne(chatID);
    }

}
