package code;

import code.datastructure.GenericQueue;
import java.util.function.Function;

public class GeneralSearch {

    // TODO Need to be modified to allow IDS
    /// Modified GeneralSearch procedure
    /// the procedure takes a problem and a makeQ Function (instead of "Quing-Func");
    /// as the Queuing policies are specified when creating the queues
    public static Node search(Problem p, Function<State, GenericQueue<Node>> makeQ)
    {
        GenericQueue<Node> queue = makeQ.apply(p.initialState);
        while(true){
            if(queue.isEmpty()) return null;
            Node node = queue.removeFront();
            if(p.isGoal(node)) return node;
            for(Function<Node, Node> op : p.getPossibleOperations(node))
                queue.add(op.apply(node));
        }
    };
}
