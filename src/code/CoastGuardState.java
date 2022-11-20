package code;

import java.util.ArrayList;

public class CoastGuardState extends State {
    @StateA
    public int posX;
    @StateA
    public ArrayList<Node> grid;

    public CoastGuardState() {
        super.init(this);
    }
}
