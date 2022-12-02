package code;

import code.datastructure.GenericQueue;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.function.Function;

public class GeneralSearch<T> {

    public HashSet<Node<T>> memo;
    public static int MaxDepth = -1;

    // TODO Need to be modified to allow IDS
    /// Modified GeneralSearch procedure
    /// the procedure takes a problem and a makeQ Function (instead of "Quing-Func");
    /// as the Queuing policies are specified when creating the queues
    public Node<T> search(Problem<T> p, Function<T, GenericQueue<Node<T>>> makeQ) {
        this.memo = new HashSet<>();
        GenericQueue<Node<T>> queue = makeQ.apply(p.initialState);
        while (true) {
            if (queue.isEmpty()) return null;
            Node<T> node = queue.removeFront();
            node.mark_expanded();
            // to remove dublicates
            memo.add(node);
//            System.out.println(node.depth);

            if (p.isGoal(node)) return node;

            if(node.hashCode() == 1863076288)
                System.out.println("here");

            for (Function<Node<T>, Node<T>> op : p.getPossibleOperations(node)) {
                Node<T> newNode = op.apply(node);
                newNode.parent = node;

                if (memo.contains(newNode)) {
                    continue;
                }
                newNode.depth = node.depth + 1;

                queue.add(newNode);
            }
        }
    }

}
