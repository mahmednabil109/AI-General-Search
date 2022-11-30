package code;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class Problem<T> {
    public T initialState;
    public abstract boolean isGoal(Node<T> node);
    public abstract ArrayList<Function<Node<T>, Node<T>>> getPossibleOperations(Node<T> node);
}
