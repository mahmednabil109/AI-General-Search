package code;

import code.datastructure.GStack;

import java.util.ArrayList;
import java.util.function.Function;

public class CoastGuard extends Problem {

    public CoastGuard(){}

    public static String solve(String problem, String algo, boolean visualize){
        // TODO register the strategies some where
        Node solution = GeneralSearch.search(
                parse(problem),
                (s) -> new GStack<Node>()
        );
        // TODO handle "visualize" by traversing up the solution node
        return "";
    }

    public static String GenGrid(){
        return "";
    }

    public static CoastGuard parse(String problem){
        return new CoastGuard();
    }

    @Override
    public boolean isGoal(Node node) {
        return false;
    }

    @Override
    public ArrayList<Function<Node, Node>> getPossibleOperations(Node node) {
        return null;
    }
}
