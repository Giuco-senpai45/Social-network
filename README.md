# Social-network
## This project emulates a social networking application.
Currently the application allows the creation of User entities that can do the following.
Login by giving their ID, and with that they can perform the following 4.
<ul>
    <li> <b> 1.Send a message to a Chat </b> <br>
        <i> (can specify multiple existing Users and it will create a chat with them) </i>
    </li>
    <br>
    <li> <b> 2.Reply to a message </b> <br>
        <i>(the User receives a List of message IDs from messages he can reply to)</i>
    </li>
    <br>
    <li> <b> 3.Send a friend request </b> <br>
        <i>(the User introduces the ID of other existing User and sends him a friend request
            that will have the initial state of pending)
        </i>
    </li>
    <br>
    <li> <b> 4.Process a friend request </b> <br>
        <i>
            (the User can view his current pending friend requests and accept or reject them
            <br>
            If the user accepts the request the two Users will have a friendship introduced 
            in the friendships database) 
        </i>
    </li>
</ul>

Here's a quick preview of the first version of the network.
<br>
<i> <font size="-1">(with a few extra functionalities like finding the most sociable community and raw operations
for the User and Friendship entities)</font>
</i>
![](src/main/resources/imgs/Lab5.gif)