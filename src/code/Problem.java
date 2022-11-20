package code;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class Problem {
    public State initialState;
    public Function<Node, Node> operations;
    public abstract boolean isGoal(Node node);
    public abstract ArrayList<Function<Node, Node>> getPossibleOperations(Node node);
}
