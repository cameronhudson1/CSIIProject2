package Players.teamDab;

import java.util.ArrayList;

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

    private Node predecessor;

    private int distance;

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
     * Accessor Method for getting a predecessor.
     * @return
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
     * Sets the row field
     * @param row the row number to be set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * sets the column field
     * @param column the column number to be set
     */
    public void setColumn(int column) {
        this.column = column;
    }

    public int getDistance() {
        return distance;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * ToString for the Node Class
     * @return
     */
    @Override
    public String toString() {
        return "Player " + getPlayerOccupied() + " occupies a space at " +
                "row: " + this.getRow() + " and at column: " + this.getColumn();
    }


}
