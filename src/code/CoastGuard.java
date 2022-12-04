package code;

import code.datastructure.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class CoastGuard extends Problem<CoastGuardState> {

    public static int CoastGuardCapacity;
    public static CoastGuardNode initNode;

    public CoastGuard() {
    }

    public static String solve(String problem, String algo, boolean visualize) {
        // TODO register the strategies some where
        GeneralSearch<CoastGuardState> gs = new GeneralSearch<>();
        CoastGuard coastGardProblem = new CoastGuard();

        Function<CoastGuardState, GenericQueue<Node<CoastGuardState>>> makeQ = (CoastGuardState state) -> {
            CoastGuardNode node = new CoastGuardNode(state);
            GenericQueue<Node<CoastGuardState>> queue = new GStack<>();
            queue.add(node);
            return queue;
        };

        switch (algo) {
            case "BF":
                makeQ = (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    initNode = node;
                    GenericQueue<Node<CoastGuardState>> queue = new GQueue<>();
                    queue.add(node);
                    return queue;
                };
                break;
            case "DF":
                makeQ = (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    GenericQueue<Node<CoastGuardState>> queue = new GStack<>();
                    queue.add(node);
                    return queue;
                };
                break;

            case "ID":
                makeQ = (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    GenericQueue<Node<CoastGuardState>> queue = new GStack<>();
                    queue.add(node);
                    return queue;
                };
                break;
            case "UC":
            case "GR1":
            case "GR2":
            case "AS1":
            case "AS2":
                makeQ = (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    GenericQueue<Node<CoastGuardState>> queue = new GPriorityQueue<>();
                    queue.add(node);
                    return queue;
                };
                break;
            default:
                makeQ = (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    GenericQueue<Node<CoastGuardState>> queue = new GStack<>();
                    queue.add(node);
                    return queue;
                };
        }
        // TODO handle "visualize" by traversing up the solution node
        Node<CoastGuardState> solution = gs.search(
                CoastGuard.parse(problem),
                makeQ
        );
        if (solution == null)
            return "";
        String actions = coastGardProblem.backtrack(solution);

        return actions.substring(1) + ";" + solution.state.deadPassengers + ";" + solution.state.retrievedBoxes + ";" + gs.expandedNodesCount;
    }

    public static String GenGrid() {
        return GridGenerator.generate();
    }

    public static CoastGuard parse(String strProblem) {
        String[] parts = strProblem.split(";");
        String[] dims = parts[0].split(",");
        CoastGuardState.gridW = Integer.parseInt(dims[0]);
        CoastGuardState.gridH = Integer.parseInt(dims[1]);
        CoastGuard.CoastGuardCapacity = Integer.parseInt(parts[1]);

        CoastGuardState initState = new CoastGuardState();
        initState.passengerOnBoard = 0;

        String[] boatPos = parts[2].split(",");
        initState.pos = new Pair<Integer, Integer>(Integer.parseInt(boatPos[0]), Integer.parseInt(boatPos[1]));

        String[] stations = parts[3].split(",");
        CoastGuardState.stations = new TreeSet<>();
        for (int i = 0; i < stations.length; i += 2) {
            CoastGuardState.stations.add(
                    new Pair<Integer, Integer>(Integer.parseInt(stations[i]), Integer.parseInt(stations[i + 1])));
        }

        String[] ships = parts[4].split(",");
        initState.ships = new TreeMap<>();
        for (int i = 0; i < ships.length; i += 3) {
            Pair<Integer, Integer> pos = new Pair(Integer.parseInt(ships[i]), Integer.parseInt(ships[i + 1]));
            initState.ships.put(
                    pos,
                    new Ship(pos, Integer.parseInt(ships[i + 2]), 20));
        }
        CoastGuard problem = new CoastGuard();
        problem.initialState = initState;
        return problem;
    }

    public static void main(String[] args) {

        String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
        GeneralSearch.MaxDepth = -1;

        var sol = solve(grid0, "BF", false);
        CoastGuard cg = new CoastGuard();

        File file = null;
        try {
            file = new File("./graph.dot");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            writer.write("digraph {\n");
            dump_graph(initNode, writer, cg, "");
            writer.write("\n}");

            writer.flush();
            writer.close();
            System.out.println("[DONE] DUMP GRAPH");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void dump_graph(CoastGuardNode ptr, FileWriter writer, CoastGuard cg, String u)
            throws IOException {
        if (ptr == null)
            return;
        // u += "0";
        if (cg.isGoal(ptr)) {
            writer.write("\tNode_" + Objects.hash(ptr) + ptr + " [shape=\"doublecircle\"]" + "\n");
        } else {
            writer.write("\tNode_" + Objects.hash(ptr) + ptr + "\n");
        }

        int i = 1;
        for (Node<CoastGuardState> child : ptr.children) {
            String s = u + i;
            writer.write(
                    "\tNode_" + Objects.hash(ptr) + ptr + " -> " +
                            "Node_" + Objects.hash(child) + child + " [label=\"" + child.action + "\"]" + "\n");
            dump_graph((CoastGuardNode) child, writer, cg, s);
            i++;
        }
    }

    private String backtrack(Node<CoastGuardState> node) {
        if (node.parent == null)
            return "";
        return backtrack(node.parent) + "," + node.action;
    }

    @Override
    public boolean isGoal(Node<CoastGuardState> node) {
        if (node == null)
            return false;

        CoastGuardState state = node.state;
        return state.ships.isEmpty() && state.passengerOnBoard == 0;
    }

    public Node<CoastGuardState> heuristicFunc1(Node<CoastGuardState> node) {
        CoastGuardState state = node.state;
        /*
         * this heuristic works by estimating the remaining cost to the goal;
         * with the number of remaining ships.
         */
        node.compareCost += state.ships.size();
        return node;
    }

    private Integer _ManhattanDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        return Math.abs(p1.first - p2.first) + Math.abs(p1.second - p2.second);
    }

    public Node<CoastGuardState> heuristicFunc2(Node<CoastGuardState> node) {
        CoastGuardState state = node.state;

        // get closest ship
        Ship closest = null;
        for (var entry : state.ships.entrySet()) {
            Ship curShip = entry.getValue();
            if (closest == null)
                closest = curShip;
            else if (_ManhattanDistance(state.pos, curShip.pos) < _ManhattanDistance(state.pos, closest.pos))
                closest = curShip;
        }
        // add cost
        if (closest != null) {
            node.compareCost += Math.min(
                    _ManhattanDistance(state.pos, closest.pos),
                    closest.passengerCount);
        }
        return node;
    }

    public Node<CoastGuardState> retrievePassengerOperation(Node<CoastGuardState> prevNode) {
        // clone the state
        CoastGuardState state = prevNode.state.clone();
        // retrieve passengers
        Ship ship = state.ships.get(state.pos);
        int canRetrieve = CoastGuardCapacity - state.passengerOnBoard;
        if (canRetrieve > ship.passengerCount) {
            state.passengerOnBoard += ship.passengerCount;
            ship.passengerCount = 0;
        } else {
            state.passengerOnBoard += canRetrieve;
            ship.passengerCount -= canRetrieve;
        }
        // create the next node
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "pickup";
        // update ships state and path cost
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    public Node<CoastGuardState> retrieveBBOXOperation(Node<CoastGuardState> prevNode) {
        // clone the state
        CoastGuardState state = prevNode.state.clone();
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "retrieve";
        // the black box operation can be done by removing the ship from the state
        // entirely
        node.state.ships.remove(state.pos);
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        node.state.retrievedBoxes += 1;
        return node;
    }

    public Node<CoastGuardState> dropOperation(Node<CoastGuardState> prevNode) {
        if (prevNode.hashCode() == 1863076288)
            System.out.println("here");
        CoastGuardState state = prevNode.state.clone();
        state.passengerOnBoard = 0;
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "drop";
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    public Node<CoastGuardState> moveOperation(Node<CoastGuardState> prevNode, Consumer<CoastGuardState> move,
                                               String actionStr) {
        CoastGuardState state = prevNode.state.clone();
        // apply move operation
        move.accept(state);
        // create the next node
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = actionStr;
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    @Override
    public ArrayList<Function<Node<CoastGuardState>, Node<CoastGuardState>>> getPossibleOperations(
            Node<CoastGuardState> node) {
        ArrayList<Function<Node<CoastGuardState>, Node<CoastGuardState>>> operations = new ArrayList<>();
        CoastGuardState state = node.state;

        // retrieve passengers operation
        Ship current = state.ships.get(state.pos);
        if (current != null && current.passengerCount != 0 && state.passengerOnBoard != CoastGuardCapacity)
            operations.add(this::retrievePassengerOperation);

        // drop operation
        if (CoastGuardState.stations.contains(state.pos) && state.passengerOnBoard > 0)
            operations.add(this::dropOperation);

        // retrieve BBox operation
        if (current != null && current.passengerCount == 0 && !current.isWreck())
            operations.add(this::retrieveBBOXOperation);

        // move operations
        if (state.pos.first > 0 && !node.action.equals("down"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.first -= 1, "up"));
        if (state.pos.first < CoastGuardState.gridH - 1 && !node.action.equals("up"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.first += 1, "down"));
        if (state.pos.second > 0 && !node.action.equals("right"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.second -= 1, "left"));
        if (state.pos.second < CoastGuardState.gridW - 1 && !node.action.equals("left"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.second += 1, "right"));

        return operations;
    }
}
