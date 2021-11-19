package ui;

import domain.*;
import domain.validators.ValidationException;
import org.postgresql.util.PSQLException;
import repository.Repository;
import service.FriendshipService;
import service.MessageService;
import service.UserService;
import service.serviceExceptions.AddException;
import service.serviceExceptions.FindException;
import service.serviceExceptions.RemoveException;
import service.serviceExceptions.UpdateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


    /**
     * User service
     */
    private UserService userService;
    /**
     * Friendship service
     */
    private FriendshipService friendsService;

    private MessageService messageService;
    /**
     * Overloaded constructor
     * @param repoUsers repository for user entities
     * @param repoFriends repository for friendship entities
     */
    public UI(Repository<Long, User> repoUsers, Repository<Tuple<Long, Long>, Friendship> repoFriends, Repository<Long, Message> messageRepository, Repository<Long, Chat> chatRepository) {
        this.repoUsers = repoUsers;
        this.repoFriends = repoFriends;
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;

        this.userService = new UserService(repoUsers, repoFriends);
        this.friendsService = new FriendshipService(repoFriends, repoUsers);
        this.messageService = new MessageService(repoFriends, repoUsers, messageRepository, chatRepository);
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

    private void showUserFriendsList(Scanner input){
        System.out.println();
        System.out.println("Users id: ");
        Long userID = input.nextLong();
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

    private void showConversation(Scanner input){
        System.out.println();
        System.out.println("Chat Id");
        Long id = input.nextLong();
        input.nextLine();
        for(ChatDTO pair : messageService.getConversation(id)) {
            System.out.println(pair);
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
        System.out.println("14.Show a conversation");
        System.out.println("15.Login");
        System.out.println("x.Exit application");
    }

    private void loginMenu(){
        System.out.println("# You are logged in. Choose an action: #");
        System.out.println("1.Send messages");
        System.out.println("2.Reply to messages");
        System.out.println("x.Logout");
    }

    private void sendMessageMenu(Scanner input, Long id){
        System.out.println("User you want to text: ");
        Long userID = input.nextLong();
        input.nextLine();
        System.out.println("Message: ");
        String message = input.nextLine();
        messageService.addMessage(id, message, userID);
    }

    private void runLogin(Scanner input, Long id){
        loginMenu();
        while (true){
            switch (input.nextLine()){
                case "1":
                    sendMessageMenu(input, id);
                    loginMenu();
                    break;
                case "2":

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
                case "14":
                    showConversation(input);
                    showMenu();
                    break;
                case "15":
                    System.out.println();
                    System.out.println("Id: ");
                    Long id = input.nextLong();
                    input.nextLine();
                    runLogin(input, id);
                    showMenu();
                    break;
                case "x":
                    return;
            }
        }
    }




}
