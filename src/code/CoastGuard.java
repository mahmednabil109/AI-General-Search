package code;

import code.datastructure.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class CoastGuard extends Problem<CoastGuardState> {

    public static int CoastGuardCapacity;

    public CoastGuard() {
    }

    public static String solve(String problem, String algo, boolean visualize) {
        // TODO register the strategies some where
        GeneralSearch<CoastGuardState> gs = new GeneralSearch<>();
        CoastGuard coastGardProblem = new CoastGuard();

        Node<CoastGuardState> solution = gs.search(
                coastGardProblem.parse(problem),
                // BFS
                (s) -> new GQueue<Node<CoastGuardState>>()
        );
        // TODO handle "visualize" by traversing up the solution node
        return "";
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
        for(int i = 0;i<ships.length;i+=3) {
            Pair<Integer, Integer> pos = new Pair(Integer.parseInt(ships[i]), Integer.parseInt(ships[i + 1]));
            initState.ships.put(
                    pos,
                    new Ship(pos, Integer.parseInt(ships[i+2]), 20)
            );
        }
        CoastGuard problem = new CoastGuard();
        problem.initialState = initState;
        return problem;
    }

    @Override
    public boolean isGoal(Node<CoastGuardState> node) {
        if (node == null) return false;

        CoastGuardState state = node.state;
        return state.ships.isEmpty() && state.passengerOnBoard == 0;
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
        node.action = "Retrieve Passengers";
        // update ships state and path cost
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    public Node<CoastGuardState> retrieveBBOXOperation(Node<CoastGuardState> prevNode) {
        CoastGuardState state = prevNode.state.clone();
        // TODO
        CoastGuardNode node = new CoastGuardNode(state);
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    public Node<CoastGuardState> dropOperation(Node<CoastGuardState> prevNode) {
        if (prevNode.hashCode() == 1863076288) System.out.println("here");
        CoastGuardState state = prevNode.state.clone();
        state.passengerOnBoard = 0;
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "Drop Passenger at a Station";
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    public Node<CoastGuardState> moveOperation(Node<CoastGuardState> prevNode, Consumer<CoastGuardState> move, String actionStr) {
        CoastGuardState state = prevNode.state.clone();
        // apply move operation
        move.accept(state);
        // create the next node
        CoastGuardNode node = new CoastGuardNode(state);
        node.action = "move in direction " + actionStr;
        // update ships state
        int actionCost = node.update();
        node.pathCost = actionCost + prevNode.pathCost;
        return node;
    }

    @Override
    public ArrayList<Function<Node<CoastGuardState>, Node<CoastGuardState>>> getPossibleOperations(Node<CoastGuardState> node) {
        ArrayList<Function<Node<CoastGuardState>, Node<CoastGuardState>>> operations = new ArrayList<>();
        CoastGuardState state = node.state;

        // retrieve passengers operation
        Ship current = state.ships.get(state.pos);
        if (current != null && current.passengerCount != 0 && state.passengerOnBoard != CoastGuardCapacity)
            operations.add(this::retrievePassengerOperation);

        // drop operation
        if (CoastGuardState.stations.contains(state.pos) && state.passengerOnBoard > 0)
            operations.add(this::dropOperation);

        // move operations
        if (state.pos.first > 0)
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.first -= 1, "up")
            );
        if (state.pos.first < CoastGuardState.gridH - 1)
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.first += 1, "down")
            );
        if (state.pos.second > 0 && !node.action.equals("move in direction right"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.second -= 1, "left")
            );
        if (state.pos.second < CoastGuardState.gridW - 1 && !node.action.equals("move in direction left"))
            operations.add(
                    (Node<CoastGuardState> n) -> this.moveOperation(n, (s) -> s.pos.second += 1, "right")
            );

        return operations;

    }


    public static void main(String[] args) {

        CoastGuard.CoastGuardCapacity = 30;
        CoastGuardState.gridW = 3;
        CoastGuardState.gridH = 3;

        CoastGuard problem = new CoastGuard();
        CoastGuardState initState = new CoastGuardState();
        initState.passengerOnBoard = 0;
        initState.pos = new Pair<Integer, Integer>(2, 2);
        initState.ships = new TreeMap<>();
        initState.ships.put(
                new Pair<>(2, 2),
                new Ship(new Pair<>(2, 2), 2, 2)
        );
        initState.ships.put(
                new Pair<>(0, 2),
                new Ship(new Pair<>(0, 2), 2, 2)
        );
        CoastGuardState.stations = new TreeSet<>();
        CoastGuardState.stations.add(new Pair<>(1, 1));
        problem.initialState = initState;

        GeneralSearch<CoastGuardState> solver = new GeneralSearch<>();
        Node<CoastGuardState> solution = solver.search(
                problem,
                (CoastGuardState state) -> {
                    CoastGuardNode node = new CoastGuardNode(state);
                    GenericQueue<Node<CoastGuardState>> queue = new GPriorityQueue<>();
                    queue.add(node);
                    return queue;
                }
        );
        System.out.println(solution);
        Node<CoastGuardState> ptr = solution;

        while (ptr != null && ptr.parent != null) ptr = ptr.parent;

        File file = null;
        try {
            file = new File("./graph.dot");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);

            writer.write("digraph {\n");
            dump_graph((CoastGuardNode) ptr, writer, (CoastGuardNode) solution, "");
            writer.write("\n}");

            writer.flush();
            writer.close();
            System.out.println("[DONE] DUMP GRAPH");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void dump_graph(CoastGuardNode ptr, FileWriter writer, CoastGuardNode goal, String u) throws IOException {
        if (ptr == null) return;
//        u += "0";
        if (ptr == goal) {
            writer.write("\tNode_" + u + ptr.toString() + " [shape=\"doublecircle\"]" + "\n");
        } else {
            writer.write("\tNode_" + u + ptr.toString() + "\n");
        }

        int i = 1;
        for (Node<CoastGuardState> child : ptr.children) {
            String s = u + i;
            writer.write(
                    "\tNode_" + u + ptr.toString() + " -> " +
                            "Node_" + s + child.toString() + " [label=\"" + child.action + "\"]" + "\n"
            );
            dump_graph((CoastGuardNode) child, writer, goal, s);
            i++;
        }
    }
}
