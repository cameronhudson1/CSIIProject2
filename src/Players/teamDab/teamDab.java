package Players.teamDab;

import Interface.*;

import java.util.*;

/**
 * A class with all the methods appropriate to call on and receive from game
 * moves by the referee.
 * @author Mark Nash
 */
public class teamDab implements PlayerModulePart1, PlayerModulePart2,
        PlayerModulePart3, PlayerModule{

    /** The graph that stores the spots on the game board */
    private HashMap<Coordinate, Node> graph;

    /** The dimensions of the game board */
    private int dim;

    /** The maximum cells on the game board horizontally or vertically */
    private int max;

    /** This player's player id */
    private int playerId;

    /** A holder list for the vertexes usable for Dijkstra's algorithm run */
    private List<Node> dijVertexHolder;

    /**
     * Method called to initialize a player module. Required task for Part 1.
     * Note that for tournaments of multiple games, only one instance of each
     * PlayerModule is created. The initPlayer method is called at the
     * beginning of each game, and must be able to reset the player
     * for the next game.
     *
     * @param dim      size of the smaller dimension of the playing area for one
     *                 player. The grid of nodes for that player is of size
     *                 dim x (dim+1).
     * @param playerId id (1 or 2) for this player.
     */
    public void initPlayer(int dim, int playerId) {
        this.dim = dim;
        this.max = 2 * dim + 1;
        this.playerId = playerId;
        this.graph = new HashMap<>((int) Math.pow(max, 2) + 4);
        this.dijVertexHolder = new LinkedList<>();
        initGraph();
    }

    /**
     * Method called after every move of the game. Used to keep internal game
     * state current. Required task for Part 1. Note that the engine will only
     * call this method after verifying the validity of the current move. Thus,
     * you do not need to verify the move provided to this method. It is
     * guaranteed to be a valid move.
     *
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
     * <p>
     * Called by Referee when one player has been Invalidated.
     * This function will call all of the required moves for the other player
     * to win.
     * <p>
     * Follows the predecessors on Dijkstra's algorithm back through the path
     * At each required move (predecessor), lastMove() is called to connect
     * that node to it's neighbors.
     */
    public void otherPlayerInvalidated() {
        this.fewestSegmentsToVictory(playerId);

        Node start = graph.get(new Coordinate(-1, playerId == 1 ? 3 : 0));
        Node finish = graph.get(new Coordinate(-1, playerId == 1 ? 1 : 2));

        while (finish.getPredecessor() != start) {
            Coordinate finishCoord = new Coordinate(finish.getPredecessor().
                    getRow(), finish.getPredecessor().getColumn());
            PlayerMove nextMove = new PlayerMove(finishCoord, playerId);

            lastMove(nextMove);
            finish = finish.getPredecessor();
        }
    }

    /**
     * Generates the next move for this player. Note that it is recommended
     * that updating internal game state does NOT occur inside of this method.
     * See lastMove. An initial, working version of this method is required for
     * Part 2. It may be refined subsequently.
     * <p>
     * Just plays the first legal move generated. Nothing fancy.
     *
     * @return a PlayerMove object representing the next move.
     */
    public PlayerMove move() {

        int otherPlayer = (this.playerId == 1 ? 2 : 1);

        Node otherPlayerFinish;
        Node otherPlayerStart;
        Node userOfIntFinish;
        Node userOfIntStart;

        if (this.playerId == 1) {
            otherPlayerFinish = graph.get(new Coordinate(-1, 2));
            otherPlayerStart = graph.get(new Coordinate(-1, 0));
            userOfIntFinish = graph.get(new Coordinate(-1, 1));
            userOfIntStart = graph.get(new Coordinate(-1, 3));
        }

        else {
            userOfIntFinish = graph.get(new Coordinate(-1, 2));
            userOfIntStart = graph.get(new Coordinate(-1, 0));
            otherPlayerFinish = graph.get(new Coordinate(-1, 1));
            otherPlayerStart = graph.get(new Coordinate(-1, 3));
        }

        //Trace otherPlayer's shortest path & set flags
        fewestSegmentsToVictory(otherPlayer);
        Node currNodeOther = otherPlayerFinish;
        while (currNodeOther.getPredecessor() != otherPlayerStart) {
            currNodeOther = currNodeOther.getPredecessor();
            currNodeOther.setUserFlag(otherPlayer);
        }

        //Trace userOfInt's shortest path & place segment
        fewestSegmentsToVictory(this.playerId);
        Node currNodeUserOfInt = userOfIntFinish;
        ArrayList<PlayerMove> moves = new ArrayList<>();
        while (currNodeUserOfInt.getPredecessor() != userOfIntStart) {
            currNodeUserOfInt = currNodeUserOfInt.getPredecessor();
            if (currNodeUserOfInt.getUserFlag() == otherPlayer) {
                int xcoord = currNodeUserOfInt.getRow();
                int ycoord = currNodeUserOfInt.getColumn();
                PlayerMove p = new PlayerMove(new Coordinate(xcoord, ycoord), this.playerId);
                moves.add(p);
            }
        }

        //Trace otherPlayer's shortest path & reset flags
        fewestSegmentsToVictory(otherPlayer);
        currNodeOther = otherPlayerFinish;
        while (currNodeOther.getPredecessor() != otherPlayerStart) {
            currNodeOther = currNodeOther.getPredecessor();
            currNodeOther.setUserFlag(0);
        }

        if (moves.size() > 1) {
            for (PlayerMove move : moves){
                int ycoord = move.getCoordinate().getCol();
                if (ycoord % 2 == 1){
                    return move;
                }
            }
        }

        return moves.get(0);
    }



    /**
     * Part 1 task that tests if a player has won the game
     * given a set of PREMOVEs.
     *
     * Does a breath-first search from start pointer node to ending pointer
     * node. If a path exists, it returns true.
     *
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
        return coord.getCol() != max-1;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the bottom border of the game board
     */
    private boolean isNotBottomBorder(Coordinate coord) {
        return coord.getRow() != max-1;
    }

    /**
     * @param coord the coordinate of interest
     * @return if the coordinate is not on the left border of the game board
     */
    private boolean isNotLeftBorder(Coordinate coord) {
        return coord.getCol() != 0;
    }

    /**
     * An expedited process for checking if a coordinate is a on the border
     * of the game board, and not a playable move.
     * @param c the coordinate of interest
     * @return if a coordinate is not on the border of the game board.
     */
    private boolean isNotABoarder(Coordinate c) {
        return (isNotTopBorder(c) && isNotRightBorder(c) &&
                isNotBottomBorder(c) && isNotLeftBorder(c));
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
     * A helper method to put a coordinate-node pair in the graph, but also
     * assign the node's row and column values when we need to reverse get
     * from the graph.
     * @param c the coordinate of the node to put in the graph.
     * @param n the node to put in the graph.
     */
    private void place(Coordinate c, Node n) {
        graph.put(c, n);
        n.setRow(c.getRow());
        n.setColumn(c.getCol());
    }

    /**
     * Part 2 task that tests if a player can correctly
     * generate all legal moves, assuming that it is that
     * player's turn and given the current game status.
     *
     * Loops through all of the spaces on the game board (except the ones on
     * the border) and adds them to a list to return if it is an empty space.
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

    /**
     * Part 2 task that computes the fewest segments that
     * a given player needs to add to complete a winning
     * path.
     *
     * An implementation of dijkstra's algorithm to this game board. returns
     * the shortest distance.
     *
     * @param i the player of interest
     * @return the fewest number of segments to add to complete a path
     */
    @Override
    public int fewestSegmentsToVictory(int i) {
        Node start = graph.get(new Coordinate(-1, i == 1 ? 3 : 0));
        Node finish =  graph.get(new Coordinate(-1, i == 1 ? 1 : 2));
        initDijkstra(start, finish, i);
        Node current;
        while (!dijVertexHolder.isEmpty()) {
            current = dequeueMin(dijVertexHolder);
            List<Node> neighbors = getDijkstraNeighbors(current, start,
                    finish, i);
            if (current.getDistance() == Integer.MAX_VALUE) {
                break;
            }
            for (Node nbr : neighbors) {
                int distThroughNbr = current.getDistance() + 1;
                if (distThroughNbr < nbr.getDistance()) {
                    nbr.setDistance(distThroughNbr);
                    nbr.setPredecessor(current);
                }
            }
        }
        return finish.getDistance() - 1;
    }

    /**
     * Initializes a list that stores the start and finish nodes as well as
     * any node that is either (an empty space that is not on the border) or
     * a space of that player.
     *
     * @param start the starting pointer node
     * @param finish the finishing pointer node
     * @param id the player id to check for
     */
    private void initDijkstra(Node start, Node finish, int id) {
        start.setDistance(0);
        start.setPredecessor(null);
        finish.setDistance(Integer.MAX_VALUE);
        finish.setPredecessor(null);
        dijVertexHolder.add(start);
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                Coordinate coord = new Coordinate(i, j);
                Node current = graph.get(coord);
                if (current.getPlayerOccupied() == (id == 1 ? 2 : 1)) {
                    continue;
                }
                if (current.getPlayerOccupied() == 0 && !isNotABoarder(coord)) {
                    continue;
                }
                current.setPredecessor(null);
                current.setDistance(Integer.MAX_VALUE);
                dijVertexHolder.add(current);
            }
        }
    }

    /**
     * Takes a current node, a start, and a finish and returns the list of
     * neighbors at the end of that path created from the start node.
     *
     * @param n Node to check Neighbors for
     * @param start Start Node
     * @param finish Finish Node
     * @param id PlayerId
     * @return A list of all of the neighbors at the end of a path
     */
    private List<Node> getDijkstraNeighbors(Node n, Node start, Node finish,
                                            int id) {
        List<Node> neighbors = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(n);
        Set<Node> visited = new HashSet<>();
        visited.add(start);
        visited.add(n);
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            for (Node nbr : current.getNeighbors()) {
                if (nbr == null) {
                    continue;
                }
                if (nbr.getPlayerOccupied() == id && !visited.contains(nbr)) {
                    visited.add(nbr);
                    if (nbr != finish) {
                        queue.add(nbr);
                    }
                    else {
                        neighbors.add(nbr);
                    }
                }
                else if (!visited.contains(nbr) && nbr.getPlayerOccupied() == 0
                        && isNotABoarder(new Coordinate(nbr.getRow(),
                        nbr.getColumn()))) {
                    visited.add(nbr);
                    neighbors.add(nbr);
                }
            }
        }
        return neighbors;
    }

    /**
     * Searches through the priorityQ list and removes (and returns) the item
     * with the smallest distance from the start node
     *
     * @param priorityQ List of Nodes for Dijkstra's Al
     * @return Node with the smallest distance from the start node
     */
    private Node dequeueMin(List<Node> priorityQ) {
        Node minNode = priorityQ.get(0);  // start off with first one
        for (Node n : priorityQ) { // checks first one again...
            if(n.getDistance() < minNode.getDistance()) {
                minNode = n;
            }
        }
        return priorityQ.remove(priorityQ.indexOf(minNode));
    }

    /**
     * Part 3 task that computes whether the given player is guaranteed with
     * optimal strategy to have won the game in no more than the given number
     * of total moves, also given whose turn it is currently.
     *
     * @param userOfInt player to determine winnable status for
     * @param currTurn player whose turn it is currently
     * @param movesLeft num of total moves by which the player of interest must
     *                  be able to guarantee victory to satisfy the
     *                  requirement to return a value of true
     * @return boolean indicating whether it is possible for the indicated
     * player to guarantee a win after the specified number of total moves.
     */
    @Override
    public boolean isWinnable(int userOfInt, int currTurn, int movesLeft) {
        int otherPlayer = (userOfInt == 1 ? 2 : 1);
        if (movesLeft == 0) {
            return hasWonGame(userOfInt);
        }
        else if (currTurn == otherPlayer && hasWonGame(userOfInt)) {
            return true;
        }
        else if (currTurn == userOfInt && hasWonGame(otherPlayer)) {
            return false;
        }
        else {

            //It's User Of Int's Turn
            if (userOfInt == currTurn) {
                for (PlayerMove p : allLegalMoves()) {
                    lastMove(p);
                    if(isWinnable(userOfInt, otherPlayer, movesLeft-1)) {
                        undoMove(p);
                        return true;
                    }
                    undoMove(p);
                }
                return false;
            }

            //It Ain't
            else {
                for (PlayerMove p : allLegalMoves()) {
                    lastMove(p);
                    if(!isWinnable(userOfInt, otherPlayer, movesLeft-1)) {
                        undoMove(p);
                        return false;
                    }
                    undoMove(p);
                }
                return true;
            }
        }
    }

    /**
     * A method to undo a move in order to not have to keep making deep
     * copies of the board.
     *
     * @param m a playerMove that stores the Coordinate in which to reset
     */
    private void undoMove(PlayerMove m) {
        Node n = graph.get(m.getCoordinate());
        n.setPlayerOccupied(0);
        setNeighbors(m.getCoordinate(), n);
    }

    /**
     * prints out the state of a game board for debugging and also fun
     * purposes. If a player has won, you should be able to see all of the
     * player's (either "X" for player 1, or "O" for player 2) symbols line
     * up from one respective end of the board to the other.
     *
     * @return A string representation of the game board to be printed.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < max; i++) {
            sb.append("\n");
            for (int j = 0; j < max; j++) {
                Node n = graph.get(new Coordinate(i, j));
                if (n.getPlayerOccupied() == 1) {
                    sb.append("X ");
                }
                else if (n.getPlayerOccupied() == 2) {
                    sb.append("O ");
                }
                else {
                    sb.append("  ");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof teamDab) {
            teamDab t = (teamDab)obj;
            if (dim != t.dim || playerId != t.playerId) {
                return false;
            }
            for (Coordinate c : graph.keySet()) {
                if (!graph.get(c).equals(t.graph.get(c))) {
                    return false;
                }
            }
        }
        return true;
    }
}
