package ui;

import domain.*;
import domain.validators.ValidationException;
import repository.Repository;
import service.FriendshipService;
import service.MessageService;
import service.FriendRequestService;
import service.UserService;
import service.serviceExceptions.AddException;
import service.serviceExceptions.FindException;
import service.serviceExceptions.RemoveException;
import service.serviceExceptions.UpdateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is the User interface class
 */
public class UI {
    /**
     * Repository for user entities
     */
    private Repository<Long, User> repoUsers;
    /**
     * Repository for friendship entities
     */
    private Repository<Tuple<Long,Long>, Friendship> repoFriends;

    private Repository<Long, Message> messageRepository;
    private Repository<Long, Chat> chatRepository;
    private Repository<Long, FriendRequest> requestRepository;

    /**
     * User service
     */
    private UserService userService;
    /**
     * Friendship service
     */
    private FriendshipService friendsService;

    private MessageService messageService;
    private FriendRequestService friendRequestService;
    /**
     * Overloaded constructor
     * @param repoUsers repository for user entities
     * @param repoFriends repository for friendship entities
     */
    public UI(Repository<Long, User> repoUsers, Repository<Tuple<Long, Long>, Friendship> repoFriends,
              Repository<Long, Message> messageRepository, Repository<Long, Chat> chatRepository,
              Repository<Long, FriendRequest> requestRepository) {
        this.repoUsers = repoUsers;
        this.repoFriends = repoFriends;
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.requestRepository = requestRepository;

        this.userService = new UserService(repoUsers, repoFriends);
        this.friendsService = new FriendshipService(repoFriends, repoUsers);
        this.messageService = new MessageService(repoFriends, repoUsers, messageRepository, chatRepository);
        this.friendRequestService = new FriendRequestService(repoFriends,repoUsers,requestRepository);
    }

    /**
     * This is the ui function for adding a user
     * @param input Scanner representing the scanner
     */
    public void addUserMenu(Scanner input){
        System.out.println();
        System.out.println("Firstname:");
        String firstname = input.nextLine();
        System.out.println("Lastname:");
        String lastname = input.nextLine();
        try{
            userService.addUser(firstname,lastname);
        }
        catch (AddException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is the ui function for removing a user
     * @param input Scanner representing the scanner
     */
    public void removeUserMenu(Scanner input){
        System.out.println();
        System.out.println("User's ID:");
        Long id = input.nextLong();
        try{
            userService.removeUser(id);
        }
        catch (RemoveException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is the ui function for updating a user
     * @param input Scanner representing the scanner
     */
    private void updateUserMenu(Scanner input){
        System.out.println();
        System.out.println("Id");
        Long id = input.nextLong();
        System.out.println("Firstname:");
        input.nextLine();
        String firstname = input.nextLine();
        System.out.println("Lastname:");
        String lastname = input.nextLine();
        try{
            userService.updateUser(id,firstname,lastname);
        }
        catch (UpdateException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    private void findUserMenu(Scanner input) {
        System.out.println();
        System.out.println("User's ID:");
        Long id = input.nextLong();
        try{
            User foundUser = userService.findUserById(id);
            System.out.println(foundUser);
        }
        catch (FindException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is the ui function for adding a friendship
     * @param input Scanner representing the scanner
     */
    private void addFriendMenu(Scanner input) {
        System.out.println();
        System.out.println("Friend1 id:");
        Long id1 = input.nextLong();
        System.out.println("Friend2 id:");
        Long id2 = input.nextLong();
        input.nextLine();
        try {
            friendsService.addFriendship(id1,id2);
        }
        catch (AddException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This is the ui function for removing a friendship
     * @param input Scanner representing the scanner
     */
    private void removeFriendMenu(Scanner input) {
        System.out.println();
        System.out.println("Friend1: ");
        Long friend1 = input.nextLong();
        System.out.println("Friend2: ");
        Long friend2 = input.nextLong();
        input.nextLine();
        Tuple<Long,Long> tuple = new Tuple<>(friend1, friend2);
        try {
            friendsService.removeFriendship(tuple);
        }
        catch (RemoveException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    private void findFriendMenu(Scanner input) {
        System.out.println();
        System.out.println("Friend1: ");
        Long friend1 = input.nextLong();
        System.out.println("Friend2: ");
        Long friend2 = input.nextLong();
        input.nextLine();
        Tuple<Long,Long> tuple = new Tuple<>(friend1, friend2);
        try {
            Friendship friendship = friendsService.findFriendshipById(tuple);
            System.out.println(friendship);
        }
        catch (FindException | ValidationException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This function shows the current users stored in the repo
     */
    private void showUsersList(){
        System.out.println();
        Iterable<User> iterable = userService.getUsers();
        iterable.forEach(System.out::println);
    }

    /**
     * This function shows the current friendships stored in the repo
     */
    private void showFriendshipsList(){
        System.out.println();
        Iterable<Friendship> iterable = friendsService.getFriendships();
        iterable.forEach(System.out::println);
    }

    /**
     * This function shows the number of connected components
     */
    private void connectedComponentsMenu(){
        System.out.println();
        int componentCount = friendsService.findConnectedComponents();
        System.out.println("The number of connected components is " + componentCount);
    }

    /**
     * This function shows the most sociable community
     */
    private void sociableCommMenu(){
        System.out.println();
        ArrayList<Integer> components = friendsService.mostSociableCommunity();
        System.out.println("The most sociable community is:");
        components.forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    /**
     * Shows the friends of the specified user
     * @param input Scanner representing the scanner
     */
    private void showUserFriendsList(Scanner input){
        System.out.println();
        System.out.println("Users id: ");
        Long userID = input.nextLong();
        input.nextLine();
        try {
            List<UserFriendshipsDTO> userFriendList = userService.getUserFriendList(userID);
            if(userFriendList.size() > 0){
                userFriendList.forEach(System.out::println);
            }
            else{
                System.out.println("This user doesn't have any friends, sums up Society if you ask me..");
            }
        }
        catch(FindException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Shows a list of friends the specified user made in a month
     * @param input Scanner representing the scanner
     */
    private void showUserFriendsListByMonth(Scanner input){
        System.out.println();
        System.out.println("Users id: ");
        Long userID = input.nextLong();
        input.nextLine();
        System.out.println("Month: ");
        Integer month = input.nextInt();
        input.nextLine();
        try {
            List<UserFriendshipsDTO> userFriendListByMonth = userService.getUserFriendListByMonth(userID, month);
            if(userFriendListByMonth.size() > 0){
                userFriendListByMonth.forEach(System.out::println);
            }
            else{
                System.out.println("This user didn't make any friends that month.");
            }
        }
        catch(FindException e){
            System.out.println(e.getMessage());
        }
    }
    /**
     * Shows the messages in the Chat with the specified id
     * @param input Scanner representing the scanner
     */
    private void showConversation(Scanner input){
        System.out.println();
        System.out.println("Chat Id");
        Long id = input.nextLong();
        input.nextLine();
        for(ChatDTO pair : messageService.getConversation(id)) {
            System.out.println(pair);
        }
    }

    //Todo poate adaug sa ma opresc daca nu mai vreau requestul
    /**
     * Sends a friend request to another existing User
     * @param input Scanner representing the scanner
     * @param loggedUser Long representing the ID of the currently logged User
     */
    private void sendRequestMenu(Scanner input,Long loggedUser){
        System.out.println();
        while(true){
            System.out.println("Send request to?");
            Long receiverID = input.nextLong();
            input.nextLine();
            try {
                boolean sentSuccess = friendRequestService.sendRequest(loggedUser,receiverID);
                if(sentSuccess){
                    System.out.println("Request sent successfully");
                }
            }
            catch(AddException | FindException e){
                System.out.println(e.getMessage());
                continue;
            }
            System.out.println("Do you want to send another request?");
            System.out.println("1.Yes");
            System.out.println("2.No");
            Long response = input.nextLong();
            input.nextLine();
            if(response.equals(2L)){
                break;
            }
        }
    }


    /**
     * Shows the user the pending friend requests and lets him accept or reject them
     * @param input Scanner representing the scanner
     * @param loggedUser Long representing the ID of the currently logged User
     */
    private void processRequestMenu(Scanner input,Long loggedUser){
        System.out.println();
        while(true){
            List<FriendRequest> requestsList = friendRequestService.findPendingRequestsForUser(loggedUser);
            if(requestsList.size() == 0){
                System.out.println("There are currently no requests");
                break;
            }
            else{
                requestsList.forEach(System.out::println);
                System.out.println("Select a request:");
                Long requestId = input.nextLong();
                input.nextLine();
                Predicate<FriendRequest> testIDisInRequests = r ->r.getId().equals(requestId);
                if(requestsList.stream().filter(testIDisInRequests).collect(Collectors.toList()).size() == 0){
                    System.out.println("This isn't an available request");
                    continue;
                }
                System.out.println("1.Accept");
                System.out.println("2.Reject");
                Long action = input.nextLong();
                input.nextLine();
                String status ="";
                if(action.equals(1L)){
                    status = "approved";
                }
                else if(action.equals(2L)){
                    status = "rejected";
                }
                else {
                    System.out.println("Not a valid command");
                    continue;
                }
                try {
                    boolean processed = friendRequestService.processRequest(requestId,status);
                    System.out.println();
                    if(processed){
                        System.out.println("Friend request accepted");
                    }
                    else {
                        System.out.println("Friend request rejected");
                    }
                }
                catch (FindException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * This function shows the menu
     */
    private void showMenu(){
        System.out.println("############## M E N U ##############");
        System.out.println("1.Add User");
        System.out.println("2.Remove User");
        System.out.println("3.Update User");
        System.out.println("4.Find User by id");
        System.out.println("5.Add Friendship");
        System.out.println("6.Remove Friendship");
        System.out.println("7.Find Friendship");
        System.out.println("8.The number of connected components");
        System.out.println("9.The longest path in the network");
        System.out.println("10.Show users");
        System.out.println("11.Show friendships");
        System.out.println("12:Show users friend list");
        System.out.println("13.Show users friend list by month");
        System.out.println("14.Show a conversation");
        System.out.println("15.Login");
        System.out.println("x.Exit application");
    }

    /**
     * The login menu
     */
    private void loginMenu(){
        System.out.println("# You are logged in. Choose an action: #");
        System.out.println("1.Send messages");
        System.out.println("2.Reply to messages");
        System.out.println("3.Send friend request");
        System.out.println("4.View friend requests");
        System.out.println("x.Logout");
    }

    /**
     * Allows the user to send a message to an existing chat or create a new chat with either one person or with a group
     * @param input Scanner representing the scanner
     * @param loggedUser Long representing the ID of the currently logged User
     */
    private void sendMessageMenu(Scanner input, Long loggedUser){
        System.out.println();
        System.out.println("Users IDs you want to text: ");
        String users = input.nextLine();
        while(true) {
            System.out.println("Message: ");
            String message = input.nextLine();
            String[] usersSplitted = users.split(" ");
            List<Long> chatters = new ArrayList<>();
            for (String c : usersSplitted)
                chatters.add(Long.parseLong(c));
            try {
                messageService.sendMessage(loggedUser, message, chatters);
                System.out.println("The message was sent successfully\n");
                System.out.println("Do you want to send another message to this user? [Y/n]");
                String response = input.nextLine();
                if (Objects.equals(response, "n"))
                    break;
            }
            catch (FindException | ValidationException e){
                System.out.println(e.getMessage());
                break;
            }

        }
    }

    /**
     * Allows the logged User to reply to messages of chats he is part of
     * @param input Scanner representing the scanner
     * @param loggedUser Long representing the ID of the currently logged User
     */
    private void replyMessageMenu(Scanner input, Long loggedUser){
        while(true) {
            List<Long> msgsToReply = messageService.messagesToReplyForUser(loggedUser);
            StringBuilder messageIDs = new StringBuilder();
            for (Long msgID : msgsToReply)
                messageIDs.append(msgID).append(" ");
            if (messageIDs.isEmpty())
                System.out.println("You don't have any messages.");
            else {
                System.out.println("You can reply to the following messages: " + messageIDs);
                System.out.println();
                System.out.println("Message ID you want to reply to: ");
                Long messageID = input.nextLong();
                input.nextLine();
                System.out.println("Message: ");
                String message = input.nextLine();
                try {
                    messageService.replyMessage(loggedUser, message, messageID);
                    System.out.println("The message was sent successfully\n");
                }
                catch (FindException | ValidationException e){
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("Do you want to reply to another message? [Y/n]");
            String response = input.nextLine();
            if(Objects.equals(response, "n"))
                break;
        }
    }

    /**
     * Verify if the specified user exists and can login
     * @param input Scanner input
     */
    private void tryToLoginMenu(Scanner input){
        System.out.println();
        System.out.println("Login as user: ");
        Long loggedUserId = input.nextLong();
        input.nextLine();
        try {
            userService.findUserById(loggedUserId);
            runLogin(input, loggedUserId);
        }
        catch(FindException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Login menu
     * @param input Scanner
     * @param loggedUser Long representing the logged User
     */
    private void runLogin(Scanner input, Long loggedUser){
        loginMenu();
        while (true){
            switch (input.nextLine()) {
                case "1":
                    sendMessageMenu(input, loggedUser);
                    loginMenu();
                    break;
                case "2":
                    replyMessageMenu(input, loggedUser);
                    loginMenu();
                    break;
                case "3":
                    sendRequestMenu(input, loggedUser);
                    loginMenu();
                    break;
                case "4":
                    processRequestMenu(input, loggedUser);
                    loginMenu();
                    break;
                case "x":
                    return;
            }
        }
    }

    /**
     * This function runs the menu
     */
    public void run(){
        Scanner input = new Scanner(System.in);
        showMenu();
        while (true){
            switch (input.nextLine()){
                case "1":
                    addUserMenu(input);
                    showMenu();
                    break;
                case "2":
                    removeUserMenu(input);
                    showMenu();
                    break;
                case "3":
                    updateUserMenu(input);
                    showMenu();
                    break;
                case "4":
                    findUserMenu(input);
                    showMenu();
                    break;
                case "5":
                    addFriendMenu(input);
                    showMenu();
                    break;
                case "6":
                    removeFriendMenu(input);
                    showMenu();
                    break;
                case "7":
                    findFriendMenu(input);
                    showMenu();
                    break;
                case "8":
                    connectedComponentsMenu();
                    showMenu();
                    break;
                case "9":
                    sociableCommMenu();
                    showMenu();
                    break;
                case "10":
                    showUsersList();
                    showMenu();
                    break;
                case "11":
                    showFriendshipsList();
                    showMenu();
                    break;
                case "12":
                    showUserFriendsList(input);
                    showMenu();
                    break;
                case "13":
                    showUserFriendsListByMonth(input);
                    showMenu();
                    break;
                case "14":
                    showConversation(input);
                    showMenu();
                    break;
                case "15":
                    tryToLoginMenu(input);
                    showMenu();
                    break;
                case "x":
                    return;
                default:
                    System.out.println("The command introduced doesn't exist");
                    System.out.println();
                    showMenu();
                    break;
            }
        }
    }
}
