package main.graph;

import main.domain.Friendship;
import main.domain.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.StreamSupport;

/**
 * This class represents a Graph that is going to have its vertices and edges represented by the friendships in the friendship
 * file.
 */
public class Graph {
    /**
     * Integer representing the number of vertices
     */
    private int V; // No. of vertices in graph.


    /**
     * A LinkedList that represents the adjacency list of each vertex
     */
    private LinkedList<Integer>[] adjList;

    /**
     * An array of arrays that represents the connected components in the graph
     */
    private ArrayList<ArrayList<Integer>> components = new ArrayList<>();

    /**
     * An iterable representing the list of users
     */
    private Iterable<User> users;

    /**
     * Overloaded constructor
     * @param friendships iterable representing the friendships currently in the file
     * @param users iterable representing the users currently in the file
     */
    public Graph(Iterable<Friendship> friendships, Iterable<User> users)
    {
        this.users = users;
        int size = (int) StreamSupport.stream(users.spliterator(), false).count();
        int maxx = 0;
        for(User user : users){
            if(user.getId() > maxx){
                maxx = user.getId().intValue();
            }
        }
        size = maxx;
        System.out.println("Maximul " + maxx);
        V = size;
        adjList = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            adjList[i] = new LinkedList();
        }

        for (Friendship friendship : friendships){
            int x = friendship.getBuddy1().intValue();
            int y = friendship.getBuddy2().intValue();
            addEdge(x - 1,y - 1);
        }
    }

    private boolean isActualFriendship(int vertex)
    {
        vertex++;
        for(User user : users){
            if(user.getId() == vertex){
                return true;
            }
        }
        return false;
    }

    /**
     * This function adds to the adjacency list of the vertex u,w and to w,u
     * @param u integer representing a vertex
     * @param w integer representing a vertex
     */
    private void addEdge(int u, int w)
    {
        adjList[u].add(w);
        adjList[w].add(u);
    }

    /**
     * This functions realises a Depth First Search for a given vertex and it realises the connected component the vertex
     * is part of.
     * @param v integer representing the current vertex
     * @param visited a boolean array representing the visited vertices
     * @param al an ArrayList in which the current connected components vertices are added
     */
    private void DFSUtil(int v, boolean[] visited, ArrayList<Integer> al)
    {
        visited[v] = true;
        al.add(v+1);
        Iterator<Integer> it = adjList[v].iterator();

        while (it.hasNext()) {
            int n = it.next();
            if (!visited[n]) {
                DFSUtil(n, visited, al);
            }
        }
    }

    /**
     * This function realises a Depth First Search for every vertex in the Graph.
     * If the vertex is not visited then we do a DFS on it creating the connected component the vertex is part of and marking the
     * other member vertices as visited, essentially breaking the Graph in multiple Disjointed Trees realising a forest
     */
    private void DFS()
    {
        boolean[] visited = new boolean[V];

        for (int i = 0; i < V; i++) {
            ArrayList<Integer> al = new ArrayList<>();
            if (!visited[i] && isActualFriendship(i))
            {
                DFSUtil(i, visited, al);
                components.add(al);
            }
        }
    }

    /**
     * This function calls the DFS function and calculates in the components field the connected components
     * @return integer representing the number of connected components in the Graph
     */
    public int noOfComponents()
     {
        DFS();
        return components.size();
    }

    /**
     * This function calculates for each connected component found using the DFS function a sociability rank by summing up
     * for each vertex part of the component the number of edges it has and dividing the sum by the number of vertices in the
     * component.
     * If 2 components have the same sociability score then we take the component which has more vertices part of it
     * @return an ArrayList of integers which represents the most sociable connected component
     */
    public ArrayList<Integer> getLongestPathComponents() {
        DFS();
        double mostSociable = 0;
        ArrayList<Integer> mostSociableComponents = new ArrayList<>();
        for(ArrayList<Integer> component : components){
            double friendshipPower = 0;
            for(Integer friendId : component){
                for(User user : users) {
                    if (user.getId() == friendId.longValue()) {
                        friendshipPower += user.getFriendListSize();
                    }
                }
            }
            friendshipPower /= component.size();
            if(mostSociable < friendshipPower){
                mostSociable = friendshipPower;
                mostSociableComponents = component;
            }
            else if(mostSociable == friendshipPower){
                if(component.size() > mostSociableComponents.size())
                    mostSociableComponents = component;
            }
        }
        return mostSociableComponents;
    }

}