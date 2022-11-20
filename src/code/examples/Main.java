package code.examples;

import code.CoastGuardState;
import code.Node;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<Node> list = new ArrayList<>(
                List.of(
                        new Node[]{
                                new Node()
                        }
                )
        );

        CoastGuardState s = new CoastGuardState();
        s.setField("posX", 2)
                .setField("grid", list)
                .setField("posX", 4);

        System.out.println(s.getField("posX"));
        System.out.println(s.getField("grid"));
    }
}
