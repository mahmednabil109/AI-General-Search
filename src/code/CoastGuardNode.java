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

    public String visualize(){
        StringBuilder sb = new StringBuilder();
        sb.append(depth+"."+(parent==null?"Initial state":action)+":\n");
        sb.append("Passengers on boat: "+ state.passengerOnBoard+", ");
        sb.append("Saved passengers: "+ state.savedPassengers+", ");
        sb.append("Retrieved Boxes: "+ state.retrievedBoxes+"\n");
        boolean firstShip = true;
        for(Ship s: state.ships.values()) {
            if(firstShip)
                firstShip = false;
            else
                sb.append(", ");
            sb.append("Ship"+s.id+" ");
            if(s.passengerCount == 0)
                sb.append("is wrecked, BBox lives = "+s.blackBoxLive);
            else
                sb.append("passengers = "+s.passengerCount);
        }
        if(state.ships.size()>0)sb.append("\n");
        sb.append(state.gridRep());
        return sb.toString();
    }

    @Override
    public String toString(){
        return "_" + this.state.serialize() + "_C"+this.pathCost+"_C"+this.compareCost;
    }
}
