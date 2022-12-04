package code;

import code.datastructure.GenericQueue;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.function.Function;

public class GeneralSearch<T> {

    public HashSet<String> memo;
    public static int MaxDepth = -1;
    public int expandedNodesCount = 0;

    // TODO Need to be modified to allow IDS
    /// Modified GeneralSearch procedure
    /// the procedure takes a problem and a makeQ Function (instead of "Quing-Func");
    /// as the Queuing policies are specified when creating the queues
    public Node<T> search(Problem<T> p, Function<T, GenericQueue<Node<T>>> makeQ) {
        this.memo = new HashSet<>();
        expandedNodesCount = 0;
        GenericQueue<Node<T>> queue = makeQ.apply(p.initialState);
        while (true) {
            if (queue.isEmpty()) return null;
            Node<T> node = queue.removeFront();
            node.mark_expanded();
            expandedNodesCount ++;
            // to remove duplicates
            memo.add(node.toString());

            if (p.isGoal(node)) return node;
            for (Function<Node<T>, Node<T>> op : p.getPossibleOperations(node)) {
                Node<T> newNode = op.apply(node);
                newNode.parent = node;
                if (memo.contains(newNode.toString())) {
                    continue;
                }
                memo.add(newNode.toString());
                newNode.depth = node.depth + 1;
                // for debugging
                if(MaxDepth != -1 && newNode.depth > MaxDepth) continue;
                queue.add(newNode);
            }
        }
    }

}
