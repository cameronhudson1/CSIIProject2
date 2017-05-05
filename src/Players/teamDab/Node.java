package Players.teamDab;

import Interface.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A node class to represent every space on the game board. There can be a
 * player occupying it specified by the integer of the player number. Each
 * node has an array list of its four directional neighbors, unless it is one
 * of the four side pointer nodes.
 * @author Mark Nash
 */
public class Node {

    /**
     * The adjacency list of neighbors. index: 0 = top neighbor, 1 = right
     * neighbor, 2 = bottom neighbor, 3 = left neighbor
     */
    private ArrayList<Node> neighbors;

    /** What player occupies the space, 0 for no one */
    private int playerOccupied;

    /** Stores the row in the graph. -2 if not yet put in the graph */
    private int row;

    /** Stores the column in the graph. -2 if not yet put in the graph */
    private int column;

    /** A predecessor reference that is used to build a path using Dijkstra's */
    private Node predecessor;

    /** The shortest possible distance from the start node using Dijkstra's */
    private int distance;

    private int userFlag;

    /**
     * Constructor for Node class. Sets playerOccupied to 0 initially, loads
     * its neighbors as null for the four corners.
     */
    public Node(int playerOccupied) {
        this.playerOccupied = playerOccupied;
        this.neighbors = new ArrayList<>(4);
        this.neighbors.add(null);
        this.neighbors.add(null);
        this.neighbors.add(null);
        this.neighbors.add(null);
        this.row = -2;
        this.column = -2;
        this.predecessor = null;
        this.distance = Integer.MAX_VALUE;
        this.userFlag = 0;
    }

    /**
     * Creates a deep copy of a generic node. Used while making a deep copy
     * of the main class.
     *
     * @param n the node to copy
     * @param newGraph the graph of the new main class instance
     */
    public void copy(Node n, HashMap<Coordinate, Node> newGraph) {
        n.playerOccupied = this.playerOccupied;
        n.row = this.row;
        n.column = this.column;
        for (int i = 0; i < neighbors.size(); i++) {
            Node neighbor = n.neighbors.get(i);
            if (neighbor == null) {
                this.neighbors.set(i, null);
            }
            else {
                this.neighbors.set(i, newGraph.get(new
                        Coordinate(neighbor.getRow(), neighbor.getColumn())));
            }
        }
    }



    /**
     * @return the playerId of the player occupying the node
     */
    public int getPlayerOccupied() {
        return this.playerOccupied;
    }

    /**
     * @return the node's list of neighbors
     */
    public ArrayList<Node> getNeighbors() {
        return this.neighbors;
    }

    /**
     * @return the predecessor
     */
    public Node getPredecessor(){
        return this.predecessor;
    }

    /**
     * Setter for playerOccupied
     * @param player the player to occupy the spot
     */
    public void setPlayerOccupied(int player) {
        this.playerOccupied = player;
    }

    /**
     * Sets the top neighbor to the node and sets the node to
     * the top neighbor's bottom neighbor.
     * @param topNeighbor the top neighbor of interest
     */
    public void setTopNeighbor(Node topNeighbor) {
        neighbors.set(0, topNeighbor);
        if (topNeighbor != null) {
            topNeighbor.neighbors.set(2, this);
        }
    }

    /**
     * Sets the right neighbor to the node and sets the node to
     * the right neighbor's left neighbor.
     * @param rightNeighbor the right neighbor of interest
     */
    public void setRightNeighbor(Node rightNeighbor) {
        neighbors.set(1, rightNeighbor);
        if (rightNeighbor != null) {
            rightNeighbor.neighbors.set(3, this);
        }
    }

    /**
     * Sets the bottom neighbor to the node and sets the node to
     * the bottom neighbor's top neighbor.
     * @param bottomNeighbor the bottom neighbor of interest
     */
    public void setBottomNeighbor(Node bottomNeighbor) {
        neighbors.set(2, bottomNeighbor);
        if (bottomNeighbor != null) {
            bottomNeighbor.neighbors.set(0, this);
        }
    }

    /**
     * Sets the left neighbor to the node and sets the node to
     * the left node's right neighbor.
     * @param leftNeighbor the left neighbor of interest
     */
    public void setLeftNeighbor(Node leftNeighbor) {
        neighbors.set(3, leftNeighbor);
        if (leftNeighbor != null) {
            leftNeighbor.neighbors.set(1, this);
        }
    }

    /**
     * @return the row field
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return the column field
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Setter for the row field
     * @param row the row number to be set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Setter for the column field
     * @param column the column number to be set
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return the distance field
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Setter for the predecessor field
     * @param predecessor predecessor to set
     */
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * Setter for the distance field
     * @param distance distance to set
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Node) {
            Node n = (Node) obj;
            if (playerOccupied != n.playerOccupied ||
                    row != n.row ||
                    column != n.column ||
                    neighbors.size() != n.neighbors.size()) {
                return false;
            }
            for (int i = 0; i < neighbors.size(); i++) {
                if (neighbors.get(i) == null) {
                    if (n.neighbors.get(i) == null) {
                        continue;
                    }
                    return false;
                }
                else if (neighbors.get(i).playerOccupied != n.neighbors.get
                        (i).playerOccupied) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ToString for the Node Class
     * @return a string representing pertinent information about this class.
     */
    @Override
    public String toString() {
        return "Player " + getPlayerOccupied() + " occupies a space at " +
                "row: " + this.getRow() + " and at column: " + this.getColumn();
    }

    /**
     * Getter for userFlag
     * @return
     */
    public int getUserFlag() {
        return userFlag;
    }

    /**
     * Setter for userFlag
     * @param userFlag
     */
    public void setUserFlag(int userFlag) {
        this.userFlag = userFlag;
    }
}