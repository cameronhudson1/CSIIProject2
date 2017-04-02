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
        ArrayList<Node> side1 = new ArrayList<>(dim);
        ArrayList<Node> side2 = new ArrayList<>(dim);
        if (var1 == 2) {
            for (int i = 1; i < 2 * dim; i = i + 2) {
                side1.add(graph.get(new Coordinate(0, i)));
                side2.add(graph.get(new Coordinate(2 * dim, i)));
            }
        }
        else {
            for (int i = 1; i < 2 * dim; i = i + 2) {
                side1.add(graph.get(new Coordinate(i, 0)));
                side2.add(graph.get(new Coordinate(i, 2 * dim)));
            }
        }
        for (Node startNode : side1) {
            for (Node finishNode : side2) {
                Queue<Node> queue = new LinkedList<>();
                queue.add(startNode);
                Set<Node> visited = new HashSet<>();
                visited.add(startNode);
                while (!queue.isEmpty()) {
                    Node current = queue.remove();
                    if (current == finishNode) {
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
            }
        }
        return false;
    }

    /**
     * Initializes the game board in the graph.
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

    public static void main(String[] args) {
        MDN4993 m = new MDN4993();
        m.initPlayer(17, 1);
        String s = "PREMOVE 16,16,1\n" +
                "PREMOVE 3,15,2\n" +
                "PREMOVE 33,3,1\n" +
                "PREMOVE 11,11,2\n" +
                "PREMOVE 15,13,1\n" +
                "PREMOVE 28,4,2\n" +
                "PREMOVE 9,9,1\n" +
                "PREMOVE 27,33,2\n" +
                "PREMOVE 7,21,1\n" +
                "PREMOVE 9,1,2\n" +
                "PREMOVE 5,13,1\n" +
                "PREMOVE 14,24,2\n" +
                "PREMOVE 24,2,1\n" +
                "PREMOVE 2,8,2\n" +
                "PREMOVE 10,8,1\n" +
                "PREMOVE 1,5,2\n" +
                "PREMOVE 30,14,1\n" +
                "PREMOVE 28,20,2\n" +
                "PREMOVE 18,26,1\n" +
                "PREMOVE 21,3,2\n" +
                "PREMOVE 8,30,1\n" +
                "PREMOVE 5,27,2\n" +
                "PREMOVE 4,4,1\n" +
                "PREMOVE 8,8,2\n" +
                "PREMOVE 20,6,1\n" +
                "PREMOVE 21,29,2\n" +
                "PREMOVE 14,8,1\n" +
                "PREMOVE 32,4,2\n" +
                "PREMOVE 14,30,1\n" +
                "PREMOVE 17,25,2\n" +
                "PREMOVE 28,24,1\n" +
                "PREMOVE 23,29,2\n" +
                "PREMOVE 5,29,1\n" +
                "PREMOVE 13,5,2\n" +
                "PREMOVE 10,28,1\n" +
                "PREMOVE 7,33,2\n" +
                "PREMOVE 14,26,1\n" +
                "PREMOVE 9,31,2\n" +
                "PREMOVE 3,13,1\n" +
                "PREMOVE 13,33,2\n" +
                "PREMOVE 26,20,1\n" +
                "PREMOVE 31,29,2\n" +
                "PREMOVE 31,31,1\n" +
                "PREMOVE 3,3,2\n" +
                "PREMOVE 15,19,1\n" +
                "PREMOVE 8,32,2\n" +
                "PREMOVE 2,14,1\n" +
                "PREMOVE 25,15,2\n" +
                "PREMOVE 13,27,1\n" +
                "PREMOVE 17,33,2\n" +
                "PREMOVE 33,1,1\n" +
                "PREMOVE 6,30,2\n" +
                "PREMOVE 18,20,1\n" +
                "PREMOVE 9,17,2\n" +
                "PREMOVE 10,16,1\n" +
                "PREMOVE 9,27,2\n" +
                "PREMOVE 16,8,1\n" +
                "PREMOVE 1,3,2\n" +
                "PREMOVE 19,5,1\n" +
                "PREMOVE 21,7,2\n" +
                "PREMOVE 17,13,1\n" +
                "PREMOVE 25,31,2\n" +
                "PREMOVE 18,4,1\n" +
                "PREMOVE 22,16,2\n" +
                "PREMOVE 27,7,1\n" +
                "PREMOVE 1,11,2\n" +
                "PREMOVE 30,22,1\n" +
                "PREMOVE 30,4,2\n" +
                "PREMOVE 4,30,1\n" +
                "PREMOVE 6,8,2\n" +
                "PREMOVE 21,9,1\n" +
                "PREMOVE 8,26,2\n" +
                "PREMOVE 11,23,1\n" +
                "PREMOVE 19,21,2\n" +
                "PREMOVE 30,24,1\n" +
                "PREMOVE 11,21,2\n" +
                "PREMOVE 28,32,1\n" +
                "PREMOVE 32,16,2\n" +
                "PREMOVE 26,8,1\n" +
                "PREMOVE 33,7,2\n" +
                "PREMOVE 7,31,1\n" +
                "PREMOVE 29,31,2\n" +
                "PREMOVE 3,33,1\n" +
                "PREMOVE 28,22,2\n" +
                "PREMOVE 5,11,1\n" +
                "PREMOVE 9,19,2\n" +
                "PREMOVE 27,31,1\n" +
                "PREMOVE 25,3,2\n" +
                "PREMOVE 22,8,1\n" +
                "PREMOVE 29,5,2\n" +
                "PREMOVE 29,3,1\n" +
                "PREMOVE 24,8,2\n" +
                "PREMOVE 30,8,1\n" +
                "PREMOVE 33,15,2\n" +
                "PREMOVE 19,33,1\n" +
                "PREMOVE 10,12,2\n" +
                "PREMOVE 6,28,1\n" +
                "PREMOVE 1,23,2\n" +
                "PREMOVE 33,13,1\n" +
                "PREMOVE 20,22,2\n" +
                "PREMOVE 32,14,1\n" +
                "PREMOVE 11,15,2\n" +
                "PREMOVE 30,26,1\n" +
                "PREMOVE 21,25,2\n" +
                "PREMOVE 5,15,1\n" +
                "PREMOVE 27,19,2\n" +
                "PREMOVE 24,26,1\n" +
                "PREMOVE 33,19,2\n" +
                "PREMOVE 20,2,1\n" +
                "PREMOVE 21,31,2\n" +
                "PREMOVE 21,11,1\n" +
                "PREMOVE 20,8,2\n" +
                "PREMOVE 17,3,1\n" +
                "PREMOVE 5,7,2\n" +
                "PREMOVE 17,5,1\n" +
                "PREMOVE 12,26,2\n" +
                "PREMOVE 11,19,1\n" +
                "PREMOVE 31,5,2\n" +
                "PREMOVE 12,12,1\n" +
                "PREMOVE 15,23,2\n" +
                "PREMOVE 21,23,1\n" +
                "PREMOVE 10,22,2\n" +
                "PREMOVE 23,23,1\n" +
                "PREMOVE 27,15,2\n" +
                "PREMOVE 6,12,1\n" +
                "PREMOVE 24,20,2\n" +
                "PREMOVE 4,8,1\n" +
                "PREMOVE 25,5,2\n" +
                "PREMOVE 7,5,1\n" +
                "PREMOVE 31,9,2\n" +
                "PREMOVE 29,27,1\n" +
                "PREMOVE 3,19,2\n" +
                "PREMOVE 32,18,1\n" +
                "PREMOVE 8,18,2\n" +
                "PREMOVE 29,25,1\n" +
                "PREMOVE 9,29,2\n" +
                "PREMOVE 28,16,1\n" +
                "PREMOVE 24,6,2\n" +
                "PREMOVE 27,1,1\n" +
                "PREMOVE 8,28,2\n" +
                "PREMOVE 23,33,1\n" +
                "PREMOVE 12,20,2\n" +
                "PREMOVE 7,7,1\n" +
                "PREMOVE 19,23,2\n" +
                "PREMOVE 27,5,1\n" +
                "PREMOVE 23,31,2\n" +
                "PREMOVE 6,26,1\n" +
                "PREMOVE 18,28,2\n" +
                "PREMOVE 25,29,1\n" +
                "PREMOVE 24,32,2\n" +
                "PREMOVE 3,5,1\n" +
                "PREMOVE 17,19,2\n" +
                "PREMOVE 29,13,1\n" +
                "PREMOVE 25,1,2\n" +
                "PREMOVE 7,23,1\n" +
                "PREMOVE 12,6,2\n" +
                "PREMOVE 22,24,1\n" +
                "PREMOVE 3,25,2\n" +
                "PREMOVE 7,13,1\n" +
                "PREMOVE 8,24,2\n" +
                "PREMOVE 21,27,1\n" +
                "PREMOVE 4,16,2\n" +
                "PREMOVE 12,28,1\n" +
                "PREMOVE 7,19,2\n" +
                "PREMOVE 16,26,1\n" +
                "PREMOVE 7,29,2\n" +
                "PREMOVE 23,13,1\n" +
                "PREMOVE 26,24,2\n" +
                "PREMOVE 7,15,1\n" +
                "PREMOVE 1,29,2\n" +
                "PREMOVE 4,24,1\n" +
                "PREMOVE 4,14,2\n" +
                "PREMOVE 33,29,1\n" +
                "PREMOVE 26,16,2\n" +
                "PREMOVE 16,18,1\n" +
                "PREMOVE 21,17,2\n" +
                "PREMOVE 10,26,1\n" +
                "PREMOVE 22,4,2\n" +
                "PREMOVE 19,31,1\n" +
                "PREMOVE 14,28,2\n" +
                "PREMOVE 16,14,1\n" +
                "PREMOVE 13,29,2\n" +
                "PREMOVE 14,16,1\n" +
                "PREMOVE 12,4,2\n" +
                "PREMOVE 31,17,1\n" +
                "PREMOVE 24,10,2\n" +
                "PREMOVE 9,21,1\n" +
                "PREMOVE 26,10,2\n" +
                "PREMOVE 19,9,1\n" +
                "PREMOVE 10,10,2\n" +
                "PREMOVE 32,6,1\n" +
                "PREMOVE 20,26,2\n" +
                "PREMOVE 32,12,1\n" +
                "PREMOVE 2,18,2\n" +
                "PREMOVE 14,18,1\n" +
                "PREMOVE 16,32,2\n" +
                "PREMOVE 18,14,1\n" +
                "PREMOVE 9,11,2\n" +
                "PREMOVE 11,31,1\n" +
                "PREMOVE 11,29,2\n" +
                "PREMOVE 30,20,1\n" +
                "PREMOVE 17,27,2\n" +
                "PREMOVE 21,1,1\n" +
                "PREMOVE 25,23,2\n" +
                "PREMOVE 33,11,1\n" +
                "PREMOVE 31,27,2\n" +
                "PREMOVE 17,23,1\n" +
                "PREMOVE 25,33,2\n" +
                "PREMOVE 2,30,1\n" +
                "PREMOVE 15,9,2\n" +
                "PREMOVE 12,18,1\n" +
                "PREMOVE 13,11,2\n" +
                "PREMOVE 16,28,1\n" +
                "PREMOVE 26,28,2\n" +
                "PREMOVE 28,28,1\n" +
                "PREMOVE 11,7,2\n" +
                "PREMOVE 16,10,1\n" +
                "PREMOVE 29,1,2\n" +
                "PREMOVE 29,9,1\n" +
                "PREMOVE 13,3,2\n" +
                "PREMOVE 17,29,1\n" +
                "PREMOVE 15,31,2\n" +
                "PREMOVE 11,27,1\n" +
                "PREMOVE 26,26,2\n" +
                "PREMOVE 24,24,1\n" +
                "PREMOVE 25,17,2\n" +
                "PREMOVE 21,15,1\n" +
                "PREMOVE 24,4,2\n" +
                "PREMOVE 14,4,1\n" +
                "PREMOVE 1,21,2\n" +
                "PREMOVE 3,17,1\n" +
                "PREMOVE 29,7,2\n" +
                "PREMOVE 8,16,1\n" +
                "PREMOVE 16,12,2\n" +
                "PREMOVE 32,30,1\n" +
                "PREMOVE 25,25,2\n" +
                "PREMOVE 32,24,1\n" +
                "PREMOVE 18,16,2\n" +
                "PREMOVE 22,2,1\n" +
                "PREMOVE 33,27,2\n" +
                "PREMOVE 4,32,1\n" +
                "PREMOVE 16,20,2\n" +
                "PREMOVE 13,9,1\n" +
                "PREMOVE 11,13,2\n" +
                "PREMOVE 6,32,1\n" +
                "PREMOVE 31,1,2\n" +
                "PREMOVE 4,28,1\n" +
                "PREMOVE 7,17,2\n" +
                "PREMOVE 21,13,1\n" +
                "PREMOVE 12,24,2\n" +
                "PREMOVE 5,31,1\n" +
                "PREMOVE 25,21,2\n" +
                "PREMOVE 15,3,1\n" +
                "PREMOVE 19,17,2\n" +
                "PREMOVE 17,1,1\n" +
                "PREMOVE 18,6,2\n" +
                "PREMOVE 28,10,1\n" +
                "PREMOVE 13,7,2\n" +
                "PREMOVE 28,6,1\n" +
                "PREMOVE 33,5,2\n" +
                "PREMOVE 26,4,1\n" +
                "PREMOVE 10,6,2\n" +
                "PREMOVE 20,10,1\n" +
                "PREMOVE 18,18,2\n" +
                "PREMOVE 30,12,1\n" +
                "PREMOVE 18,12,2\n" +
                "PREMOVE 2,28,1\n" +
                "PREMOVE 27,9,2\n" +
                "PREMOVE 27,25,1\n" +
                "PREMOVE 32,8,2\n" +
                "PREMOVE 13,19,1\n" +
                "PREMOVE 22,32,2\n" +
                "PREMOVE 25,27,1\n" +
                "PREMOVE 10,20,2\n" +
                "PREMOVE 18,10,1\n" +
                "PREMOVE 18,32,2\n" +
                "PREMOVE 33,21,1\n" +
                "PREMOVE 5,3,2\n" +
                "PREMOVE 7,11,1\n" +
                "PREMOVE 6,16,2\n" +
                "PREMOVE 22,20,1\n" +
                "PREMOVE 16,4,2\n" +
                "PREMOVE 30,16,1\n" +
                "PREMOVE 19,15,2\n" +
                "PREMOVE 22,26,1\n" +
                "PREMOVE 30,2,2\n" +
                "PREMOVE 20,32,1\n" +
                "PREMOVE 19,25,2\n" +
                "PREMOVE 28,30,1\n" +
                "PREMOVE 23,17,2\n" +
                "PREMOVE 30,32,1\n" +
                "PREMOVE 7,9,2\n" +
                "PREMOVE 22,28,1\n" +
                "PREMOVE 12,8,2\n" +
                "PREMOVE 20,28,1\n" +
                "PREMOVE 6,2,2\n" +
                "PREMOVE 1,33,1\n" +
                "PREMOVE 13,25,2\n" +
                "PREMOVE 6,4,1\n" +
                "PREMOVE 9,15,2\n" +
                "PREMOVE 21,33,1\n" +
                "PREMOVE 4,2,2\n" +
                "PREMOVE 19,27,1\n" +
                "PREMOVE 21,21,2\n" +
                "PREMOVE 31,25,1\n" +
                "PREMOVE 26,6,2\n" +
                "PREMOVE 11,1,1\n" +
                "PREMOVE 24,12,2\n" +
                "PREMOVE 33,33,1\n" +
                "PREMOVE 1,9,2\n" +
                "PREMOVE 10,4,1\n" +
                "PREMOVE 13,23,2\n" +
                "PREMOVE 13,13,1\n" +
                "PREMOVE 12,30,2\n" +
                "PREMOVE 19,3,1\n" +
                "PREMOVE 10,2,2\n" +
                "PREMOVE 16,30,1\n" +
                "PREMOVE 23,3,2\n" +
                "PREMOVE 8,2,1\n" +
                "PREMOVE 13,21,2\n" +
                "PREMOVE 20,24,1\n" +
                "PREMOVE 14,14,2\n" +
                "PREMOVE 31,11,1\n" +
                "PREMOVE 1,19,2\n" +
                "PREMOVE 14,32,1\n" +
                "PREMOVE 14,10,2\n" +
                "PREMOVE 30,28,1\n" +
                "PREMOVE 12,32,2\n" +
                "PREMOVE 5,1,1\n" +
                "PREMOVE 31,3,2\n" +
                "PREMOVE 12,2,1\n" +
                "PREMOVE 19,7,2\n" +
                "PREMOVE 22,10,1\n" +
                "PREMOVE 23,9,2\n" +
                "PREMOVE 15,25,1\n" +
                "PREMOVE 8,12,2\n" +
                "PREMOVE 6,10,1\n" +
                "PREMOVE 32,26,2\n" +
                "PREMOVE 14,12,1\n" +
                "PREMOVE 1,13,2\n" +
                "PREMOVE 1,27,1\n" +
                "PREMOVE 27,3,2\n" +
                "PREMOVE 3,23,1\n" +
                "PREMOVE 5,9,2\n" +
                "PREMOVE 26,18,1\n" +
                "PREMOVE 9,13,2\n" +
                "PREMOVE 15,1,1\n" +
                "PREMOVE 1,31,2\n" +
                "PREMOVE 11,33,1\n" +
                "PREMOVE 15,15,2\n" +
                "PREMOVE 20,18,1\n" +
                "PREMOVE 9,33,2\n" +
                "PREMOVE 27,23,1\n" +
                "PREMOVE 3,11,2\n" +
                "PREMOVE 32,20,1\n" +
                "PREMOVE 28,14,2\n" +
                "PREMOVE 7,3,1\n" +
                "PREMOVE 20,30,2\n" +
                "PREMOVE 2,16,1\n" +
                "PREMOVE 29,19,2\n" +
                "PREMOVE 9,23,1\n" +
                "PREMOVE 26,32,2\n" +
                "PREMOVE 5,21,1\n" +
                "PREMOVE 4,6,2\n" +
                "PREMOVE 19,29,1\n" +
                "PREMOVE 10,24,2\n" +
                "PREMOVE 18,22,1\n" +
                "PREMOVE 26,22,2\n" +
                "PREMOVE 8,20,1\n" +
                "PREMOVE 19,19,2\n" +
                "PREMOVE 25,19,1\n" +
                "PREMOVE 23,27,2\n" +
                "PREMOVE 7,25,1\n" +
                "PREMOVE 4,26,2\n" +
                "PREMOVE 5,17,1\n" +
                "PREMOVE 27,17,2\n" +
                "PREMOVE 10,32,1\n" +
                "PREMOVE 5,23,2\n" +
                "PREMOVE 30,6,1\n" +
                "PREMOVE 4,20,2\n" +
                "PREMOVE 15,5,1\n" +
                "PREMOVE 24,22,2\n" +
                "PREMOVE 33,17,1\n" +
                "PREMOVE 2,10,2\n" +
                "PREMOVE 14,2,1\n" +
                "PREMOVE 10,18,2\n" +
                "PREMOVE 28,8,1\n" +
                "PREMOVE 22,18,2\n" +
                "PREMOVE 20,14,1\n" +
                "PREMOVE 23,7,2\n" +
                "PREMOVE 13,31,1\n" +
                "PREMOVE 2,32,2\n" +
                "PREMOVE 10,30,1\n" +
                "PREMOVE 7,1,2\n" +
                "PREMOVE 2,24,1\n" +
                "PREMOVE 33,9,2\n" +
                "PREMOVE 3,27,1\n" +
                "PREMOVE 6,6,2\n" +
                "PREMOVE 17,7,1\n" +
                "PREMOVE 2,2,2\n" +
                "PREMOVE 33,25,1\n" +
                "PREMOVE 17,31,2\n" +
                "PREMOVE 15,21,1\n" +
                "PREMOVE 15,7,2\n" +
                "PREMOVE 4,10,1\n" +
                "PREMOVE 5,33,2\n" +
                "PREMOVE 4,18,1\n" +
                "PREMOVE 31,33,2\n" +
                "PREMOVE 17,17,1\n" +
                "PREMOVE 11,5,2\n" +
                "PREMOVE 23,5,1\n" +
                "PREMOVE 12,16,2\n" +
                "PREMOVE 31,15,1\n" +
                "PREMOVE 27,11,2\n" +
                "PREMOVE 31,13,1\n" +
                "PREMOVE 26,14,2\n" +
                "PREMOVE 5,25,1\n" +
                "PREMOVE 17,11,2\n" +
                "PREMOVE 18,8,1\n" +
                "PREMOVE 16,24,2\n" +
                "PREMOVE 24,28,1\n" +
                "PREMOVE 29,15,2\n" +
                "PREMOVE 9,25,1\n" +
                "PREMOVE 3,7,2\n" +
                "PREMOVE 8,4,1\n" +
                "PREMOVE 23,21,2\n" +
                "PREMOVE 27,21,1\n" +
                "PREMOVE 24,14,2\n" +
                "PREMOVE 28,2,1\n" +
                "PREMOVE 12,14,2\n" +
                "PREMOVE 9,7,1\n" +
                "PREMOVE 33,31,2\n" +
                "PREMOVE 30,18,1\n" +
                "PREMOVE 28,12,2\n" +
                "PREMOVE 3,21,1\n" +
                "PREMOVE 23,11,2\n" +
                "PREMOVE 29,33,1\n" +
                "PREMOVE 4,22,2\n" +
                "PREMOVE 19,13,1\n" +
                "PREMOVE 29,21,2\n" +
                "PREMOVE 4,12,1\n" +
                "PREMOVE 17,15,2\n" +
                "PREMOVE 22,30,1\n" +
                "PREMOVE 8,6,2\n" +
                "PREMOVE 32,28,1\n" +
                "PREMOVE 15,17,2\n" +
                "PREMOVE 22,22,1\n" +
                "PREMOVE 11,9,2\n" +
                "PREMOVE 1,17,1\n" +
                "PREMOVE 15,33,2\n" +
                "PREMOVE 24,30,1\n" +
                "PREMOVE 24,18,2\n" +
                "PREMOVE 3,31,1\n" +
                "PREMOVE 31,23,2\n" +
                "PREMOVE 2,4,1\n" +
                "PREMOVE 9,5,2\n" +
                "PREMOVE 22,14,1\n" +
                "PREMOVE 6,24,2\n" +
                "PREMOVE 25,9,1\n" +
                "PREMOVE 25,11,2\n" +
                "PREMOVE 3,29,1\n" +
                "PREMOVE 31,21,2\n" +
                "PREMOVE 13,17,1\n" +
                "PREMOVE 32,10,2\n" +
                "PREMOVE 20,4,1\n" +
                "PREMOVE 27,27,2\n" +
                "PREMOVE 10,14,1\n" +
                "PREMOVE 23,25,2\n" +
                "PREMOVE 24,16,1\n" +
                "PREMOVE 28,26,2\n" +
                "PREMOVE 29,23,1\n" +
                "PREMOVE 30,30,2\n" +
                "PREMOVE 8,10,1\n" +
                "PREMOVE 11,3,2\n" +
                "PREMOVE 14,20,1\n" +
                "PREMOVE 23,19,2\n" +
                "PREMOVE 31,7,1\n" +
                "PREMOVE 32,22,2\n" +
                "PREMOVE 14,6,1\n" +
                "PREMOVE 17,9,2\n" +
                "PREMOVE 32,2,1\n" +
                "PREMOVE 31,19,2\n" +
                "PREMOVE 16,2,1\n" +
                "PREMOVE 18,2,2\n" +
                "PREMOVE 7,27,1\n" +
                "PREMOVE 17,21,2\n" +
                "PREMOVE 28,18,1\n" +
                "PREMOVE 29,29,2\n" +
                "PREMOVE 2,12,1\n" +
                "PREMOVE 6,18,2\n" +
                "PREMOVE 18,24,1\n" +
                "PREMOVE 29,17,2\n" +
                "PREMOVE 1,15,1\n" +
                "PREMOVE 8,14,2\n" +
                "PREMOVE 2,20,1\n" +
                "PREMOVE 11,25,2\n" +
                "PREMOVE 3,1,1";
        String[] sl = s.split("\n");
        Calendar c = Calendar.getInstance();
        long start = System.currentTimeMillis();
        //Timestamp t1 = new Timestamp(c.getTime().getTime());
        for (String str : sl) {
            String s2 = str.substring(8, str.length());
            String[] ab = s2.split(",");
            m.lastMove(new PlayerMove(new Coordinate(Integer.parseInt(ab[0]),
                    Integer.parseInt(ab[1])), Integer.parseInt(ab[2])));
        }
        //Timestamp t2 = new Timestamp(c.getTime().getTime());
        long finish = System.currentTimeMillis();
        System.out.println(finish - start);
    }
}
