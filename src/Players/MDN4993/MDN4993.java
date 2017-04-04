package Players.MDN4993;

import Interface.Coordinate;
import Interface.PlayerModule;
import Interface.PlayerModulePart1;
import Interface.PlayerMove;

import java.util.*;

/**
 * A class with all the methods appropriate to call on and receive from game
 * moves by the referee.
 * @author Mark Nash
 */
public class MDN4993 implements PlayerModulePart1, PlayerModule {

    /** The graph that stores the spots on the game board */
    private HashMap<Coordinate, Node> graph;

    /** The dimensions of the game board */
    private int dim;

    /** This player's player id */
    private int playerId;

    /**
     * Method called to initialize a player module. Required task for Part 1.
     * Note that for tournaments of multiple games, only one instance of each
     * PlayerModule is created. The initPlayer method is called at the
     * beginning of each game, and must be able to reset the player
     * for the next game.
     * @param dim size of the smaller dimension of the playing area for one
     *             player. The grid of nodes for that player is of size
     *             dim x (dim+1).
     * @param playerId id (1 or 2) for this player.
     */
    public void initPlayer(int dim, int playerId) {
        this.dim = dim;
        this.playerId = playerId;
        this.graph = new HashMap<>(2 * (2 * dim + 1));
        initGraph();
    }

    /**
     * Method called after every move of the game. Used to keep internal game
     * state current. Required task for Part 1. Note that the engine will only
     * call this method after verifying the validity of the current move. Thus,
     * you do not need to verify the move provided to this method. It is
     * guaranteed to be a valid move.
     * @param m PlayerMove representing the most recent move
     */
    public void lastMove(PlayerMove m) {
        Node n = graph.get(m.getCoordinate());
        n.setPlayerOccupied(m.getPlayerId());
        setNeighbors(m.getCoordinate(), n);
    }

    /**
     * Indicates that the other player has been invalidated.
     * Required task for Part 2.
     */
    public void otherPlayerInvalidated() {

    }

    /**
     * Generates the next move for this player. Note that it is recommended
     * that updating internal game state does NOT occur inside of this method.
     * See lastMove. An initial, working version of this method is required for
     * Part 2. It may be refined subsequently.
     * @return a PlayerMove object representing the next move.
     */
    public PlayerMove move() {
        return null;
    }

    /**
     * Part 1 task that tests if a player has won the game
     * given a set of PREMOVEs.
     * @param var1 player to test for a winning path.
     * @return boolean value indicating if the player has a winning path.
     */
    public boolean hasWonGame(int var1) {
        Node start;
        Node end;
        if (var1 == 1) {
            start = graph.get(new Coordinate(-1, 3));
            end = graph.get(new Coordinate(-1, 1));
        }
        else {
            start = graph.get(new Coordinate(-1, 0));
            end = graph.get(new Coordinate(-1, 2));
        }
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);
        Set<Node> visited = new HashSet<>();
        visited.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            if (current == end) {
                return true;
            }
            for (Node nbr : current.getNeighbors()) {
                if (nbr == null) {
                    continue;
                }
                if (nbr.getPlayerOccupied() == var1 &&
                        !visited.contains(nbr)) {
                    visited.add(nbr);
                    queue.add(nbr);
                }
            }
        }
        return false;
    }

    /**
     * Initializes the game board in the graph and the imaginary pointer nodes
     */
    private void initGraph() {
        for (int i = 0; i < 2 * dim + 1; i++) {
            for (int j = 0; j < 2 * dim + 1; j++) {
                Coordinate coord = new Coordinate(i, j);
                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) {
                    Node n = new Node();
                    graph.put(coord, n);
                    setNeighbors(new Coordinate(i, j), n);
                }
                else {
                    Node n = new Node();
                    graph.put(coord, n);
                    setNeighbors(new Coordinate(i, j), n);
                    if (i % 2 == 0 && j % 2 != 0) {
                        n.setPlayerOccupied(2);
                    }
                    else {
                        n.setPlayerOccupied(1);
                    }
                }
            }
        }
        // Creates the four side pointer nodes. mutually assigns their neighbors
        Node n0 = new Node();
        n0.setPlayerOccupied(2);
        n0.getNeighbors().clear();
        graph.put(new Coordinate(-1, 0), n0);
        Node n1 = new Node();
        n1.setPlayerOccupied(1);
        n1.getNeighbors().clear();
        graph.put(new Coordinate(-1, 1), n1);
        Node n2 = new Node();
        n2.setPlayerOccupied(2);
        n2.getNeighbors().clear();
        graph.put(new Coordinate(-1, 2), n2);
        Node n3 = new Node();
        n3.setPlayerOccupied(1);
        n3.getNeighbors().clear();
        graph.put(new Coordinate(-1, 3), n3);
        for (int i = 1; i < 2 * dim + 1; i += 2) {
            n0.getNeighbors().add(graph.get(new Coordinate(0, i)));
            graph.get(new Coordinate(0, i)).getNeighbors().set(0, n0);
            n1.getNeighbors().add(graph.get(new Coordinate(i, 2 * dim)));
            graph.get(new Coordinate(i, 2 * dim)).getNeighbors().set(1, n1);
            n2.getNeighbors().add(graph.get(new Coordinate(2 * dim, i)));
            graph.get(new Coordinate(2 * dim, i)).getNeighbors().set(2, n2);
            n3.getNeighbors().add(graph.get(new Coordinate(i, 0)));
            graph.get(new Coordinate(i, 0)).getNeighbors().set(3, n3);
        }
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the top border of the game board
     */
    private boolean isNotTopBorder(Coordinate coord) {
        return coord.getRow() != 0;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the right border of the game board
     */
    private boolean isNotRightBorder(Coordinate coord) {
        return coord.getCol() != 2 * dim + 1;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the bottom border of the game board
     */
    private boolean isNotBottomBorder(Coordinate coord) {
        return coord.getRow() != 2 * dim + 1;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the left border of the game board
     */
    private boolean isNotLeftBorder(Coordinate coord) {
        return coord.getCol() != 0;
    }

    /**
     * Mutually sets all of the given node's neighbors.
     * @param coord the coordinate of interest
     * @param n the node of interest
     */
    private void setNeighbors(Coordinate coord, Node n) {
        if (isNotTopBorder(coord)) {
            Node topNeighbor = graph.get(new Coordinate(coord.getRow()
                    - 1, coord.getCol()));
            n.setTopNeighbor(topNeighbor);
        }
        if (isNotRightBorder(coord)) {
            Node rightNeighbor = graph.get(new Coordinate(coord.getRow(), coord
                    .getCol() + 1));
            n.setRightNeighbor(rightNeighbor);
        }
        if (isNotBottomBorder(coord)) {
            Node bottomNeighbor = graph.get(new Coordinate(coord.getRow() + 1,
                    coord.getCol()));
            n.setBottomNeighbor(bottomNeighbor);
        }
        if (isNotLeftBorder(coord)) {
            Node leftNeighbor = graph.get(new Coordinate(coord.getRow(), coord
                    .getCol() - 1));
            n.setLeftNeighbor(leftNeighbor);
        }
    }

    /**
     * prints out the state of a game board for debugging and also fun
     * purposes. If a player has won, you should be able to see all of the
     * player's (either "X" for player 1, or "O" for player 2) symbols line
     * up from one respective end of the board to the other.
     * @return A string representation of the game board to be printed.
     */
    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < (2 * dim + 1); i++) {
            str += "\n";
            for (int j = 0; j < (2 * dim + 1); j++) {
                Node n = graph.get(new Coordinate(i, j));
                if (n.getPlayerOccupied() == 1) {
                    str += "X ";
                }
                else if (n.getPlayerOccupied() == 2) {
                    str += "O ";
                }
                else {
                    str += "  ";
                }
            }
        }
        return str;
    }
}
