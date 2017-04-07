package Players.teamDab;

import Interface.*;

import java.util.*;

/**
 * A class with all the methods appropriate to call on and receive from game
 * moves by the referee.
 * @author Mark Nash
 */
public class teamDab implements PlayerModulePart2, PlayerModulePart1 {

    /** The graph that stores the spots on the game board */
    private HashMap<Coordinate, Node> graph;

    /** The dimensions of the game board */
    private int dim;

    /** The maximum cells on the game board */
    private int max;

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
        this.max = 2 * dim + 1;
        this.playerId = playerId;
        this.graph = new HashMap<>((int)Math.pow(max, 2) + 4);
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
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Coordinate coord = new Coordinate(i, j);
                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) {
                    Node n = new Node(0);
                    place(coord, n);
                    setNeighbors(new Coordinate(i, j), n);
                }
                else {
                    Node n = new Node(0);
                    place(coord, n);
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
        Node n0 = new Node(2);
        place(new Coordinate(-1, 0), n0);
        Node n1 = new Node(1);
        place(new Coordinate(-1, 1), n1);
        Node n2 = new Node(2);
        place(new Coordinate(-1, 2), n2);
        Node n3 = new Node(1);
        place(new Coordinate(-1, 3), n3);
        for (int i = 1; i < max; i += 2) {
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
        return coord.getCol() != max;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the bottom border of the game board
     */
    private boolean isNotBottomBorder(Coordinate coord) {
        return coord.getRow() != max;
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
        for (int i = 0; i < (max); i++) {
            str += "\n";
            for (int j = 0; j < (max); j++) {
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

    public void place(Coordinate c, Node n) {
        graph.put(c, n);
        n.setRow(c.getRow());
        n.setColumn(c.getCol());
    }

    /**
     * Part 2 task that tests if a player can correctly
     * generate all legal moves, assuming that it is that
     * player's turn and given the current game status.
     *
     * @return a List of all legal PlayerMove objects. They do not have to be
     * in any particular order.
     */
    @Override
    public List<PlayerMove> allLegalMoves() {
        LinkedList<PlayerMove> ll = new LinkedList<>();
        for (int i = 1; i < max - 1; i++) {
            for (int j = 1; j < max - 1; j++) {
                if (graph.get(new Coordinate(i, j)).getPlayerOccupied() == 0) {
                    ll.add(new PlayerMove(new Coordinate(i, j), playerId));
                }
            }
        }
        return ll;
    }

    private void visitEndCells(Node current, Set<Node> visited,
                               List<Node> endCells, int id) {

    }

    /**
     * Part 2 task that computes the fewest segments that
     * a given player needs to add to complete a winning
     * path.
     *
     * @param i the player of interest
     * @return the fewest number of segments to add to complete a path
     */
    @Override
    public int fewestSegmentsToVictory(int i) {
        return 0;
    }

    public static void main(String[] args) {
        /*
        teamDab t = new teamDab();
        t.initPlayer(6, 1);
        String str = "PREMOVE 7,1,1\n" +
                "PREMOVE 6,4,2\n" +
                "PREMOVE 11,3,1\n" +
                "PREMOVE 11,7,2\n" +
                "PREMOVE 5,5,1\n" +
                "PREMOVE 8,10,2\n" +
                "PREMOVE 10,10,1\n" +
                "PREMOVE 1,9,2\n" +
                "PREMOVE 7,5,1\n" +
                "PREMOVE 6,6,2\n" +
                "PREMOVE 4,4,1\n" +
                "PREMOVE 3,11,2\n" +
                "PREMOVE 5,3,1\n" +
                "PREMOVE 8,2,2\n" +
                "PREMOVE 9,9,1\n" +
                "PREMOVE 10,6,2\n" +
                "PREMOVE 1,1,1\n" +
                "PREMOVE 2,4,2\n" +
                "PREMOVE 3,1,1\n" +
                "PREMOVE 2,8,2\n" +
                "PREMOVE 9,7,1\n" +
                "PREMOVE 4,8,2\n" +
                "PREMOVE 5,11,1\n" +
                "PREMOVE 2,6,2";
        String[] s1 = str.split("\n");
        String[][] s2;
        for (String s : s1) {
            String b = s.substring(8, s.length());
            String[] a = b.split(",");
            t.lastMove(new PlayerMove(new Coordinate(Integer.parseInt(a[0]), Integer
                    .parseInt(a[1])), Integer.parseInt(a[2])));
        }
        System.out.println(t);
        */
    }

}
