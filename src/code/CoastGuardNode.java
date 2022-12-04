package code;

import code.datastructure.Pair;

import java.util.ArrayList;

public class CoastGuardNode extends Node<CoastGuardState>{
    public CoastGuardNode(CoastGuardState state){
        super();
        this.state = state;
    }

    public int update(){
        // TODO NEED to calc Cost and pass it up
        // update the ships
        int lost = 0;
        ArrayList<Pair<Integer, Integer>> toBeDeleted = new ArrayList<>();
        for(var entry : state.ships.entrySet()){
            Ship s = entry.getValue();
            lost += s.update();
            if(s.isWreck())
                toBeDeleted.add(entry.getKey());
        }
        // remove wrecked ships
        for(var key : toBeDeleted)
            state.ships.remove(key);
        this.state.deadPassengers += lost - toBeDeleted.size();
        return lost;
    }

    @Override
    public String toString(){
        return "_" + this.state.serialize() + "_C"+this.pathCost+"_C"+this.compareCost;
    }
}
