package code;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Node<T> implements Comparable<Node<T>>{
    public Node<T> parent;
    public ArrayList<Node<T>> children = new ArrayList<>();
    public T state;
    int compareCost;
    int pathCost;
    int depth;
    // represents the action taken to get to this node
    String action = "";

    public void mark_expanded() {
        if (this.parent != null)
            parent.children.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return compareCost == node.compareCost && pathCost == node.pathCost && Objects.equals(state, node.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, compareCost, pathCost);
    }

    @Override
    public int compareTo(Node<T> o) {
        return this.pathCost - o.pathCost;
    }
}
