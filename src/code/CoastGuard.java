package code;

import code.datastructure.*;

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
    public boolean usePathCost = true;
    public Function<Node<CoastGuardState>, Integer> heuristic;

    public CoastGuard() {
    }

    public static String solve(String problem, String algo, boolean visualize) {
        GeneralSearch<CoastGuardState> gs = new GeneralSearch<>();
        CoastGuard coastGardProblem = new CoastGuard();
        // make it DF by default
        GenericQueue<Node<CoastGuardState>> queue = new GStack<>();

        switch (algo) {
            case "BF":
                queue = new GQueue<>();
                break;
            case "UC":
                queue = new GPriorityQueue<>();
                break;
            case "GR1":
                coastGardProblem.usePathCost = false;
                coastGardProblem.heuristic = coastGardProblem::heuristicFunc1;
                queue = new GPriorityQueue<>();
                break;
            case "GR2":
                coastGardProblem.usePathCost = false;
                coastGardProblem.heuristic = coastGardProblem::heuristicFunc2;
                queue = new GPriorityQueue<>();
                break;
            case "AS1":
                coastGardProblem.heuristic = coastGardProblem::heuristicFunc1;
                queue = new GPriorityQueue<>();
                break;
            case "AS2":
                coastGardProblem.heuristic = coastGardProblem::heuristicFunc2;
                queue = new GPriorityQueue<>();
        }
        GenericQueue<Node<CoastGuardState>> finalQueue = queue;
        Function<CoastGuardState, GenericQueue<Node<CoastGuardState>>> makeQ = (CoastGuardState state) -> {
            CoastGuardNode node = new CoastGuardNode(state);
            finalQueue.add(node);
            return finalQueue;
        };

        if (algo.equals("ID"))
            gs.MaxDepth = 0;
        Node<CoastGuardState> solution = null;
        do {
            solution = gs.search(
                    CoastGuard.parse(problem),
                    makeQ
            );
            gs.MaxDepth += 1;
        } while (algo.equals("ID") && solution == null);
        if (solution == null)
            return "";
        String actions = coastGardProblem.backtrack(solution);
        if (visualize)
            backtrackVisualize(solution);
        return actions.substring(1)
                + ";"
                + solution.state.deadPassengers
                + ";"
                + solution.state.retrievedBoxes
                + ";"
                + gs.expandedNodesCount;
    }

    private static void backtrackVisualize(Node<CoastGuardState> node) {
        if (node == null)
            return;
        backtrackVisualize(node.parent);
        System.out.println(((CoastGuardNode) node).visualize());
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
        for (int i = 0, id = 0; i < ships.length; i += 3, id++) {
            Pair<Integer, Integer> pos = new Pair(Integer.parseInt(ships[i]), Integer.parseInt(ships[i + 1]));
            initState.ships.put(
                    pos,
                    new Ship(pos, Integer.parseInt(ships[i + 2]), 20, id));
        }
        CoastGuard problem = new CoastGuard();
        problem.initialState = initState;
        return problem;
    }

    public static void main(String[] args) {

        String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";

        var sol = solve(grid0, "IDS", true);
        System.out.println(sol);
//        CoastGuard cg = new CoastGuard();
//
//        File file = null;
//        try {
//            file = new File("./graph.dot");
//            file.createNewFile();
//            FileWriter writer = new FileWriter(file);
//
//            writer.write("digraph {\n");
//            dump_graph(initNode, writer, cg, "");
//            writer.write("\n}");
//
//            writer.flush();
//            writer.close();
//            System.out.println("[DONE] DUMP GRAPH");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    // dump_graph is used to convert the whole traversed graph into dot language;
    // to be visualised with graphize
    // used for debugging
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
        if (node == null)
            return "";
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

    public Integer heuristicFunc1(Node<CoastGuardState> node) {
        CoastGuardState state = node.state;
        /*
         * this heuristic works by estimating the remaining cost to the goal;
         * with the number of remaining ships.
         */
        return state.ships.size();
    }

    private Integer _ManhattanDistance(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        return Math.abs(p1.first - p2.first) + Math.abs(p1.second - p2.second);
    }

    public Integer heuristicFunc2(Node<CoastGuardState> node) {
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
            return Math.min(
                    _ManhattanDistance(state.pos, closest.pos),
                    closest.passengerCount);
        }
        return 0;
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
        if (usePathCost)
            node.compareCost = node.pathCost;
        if (heuristic != null)
            node.compareCost += heuristic.apply(node);
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
        if (usePathCost)
            node.compareCost = node.pathCost;
        if (heuristic != null)
            node.compareCost += heuristic.apply(node);
        node.state.retrievedBoxes += 1;
        return node;
    }

    public Node<CoastGuardState> dropOperation(Node<CoastGuardState> prevNode) {
        if (prevNode.hashCode() == 1863076288)
            System.out.println("here");
        CoastGuardState state = prevNode.state.clone();
        state.savedPassengers += state.passengerOnBoard;
        state.passengerOnBoard = 0;
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "drop";
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        if (usePathCost)
            node.compareCost = node.pathCost;
        if (heuristic != null)
            node.compareCost += heuristic.apply(node);
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
        if (usePathCost)
            node.compareCost = node.pathCost;
        if (heuristic != null)
            node.compareCost += heuristic.apply(node);
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
