package code;

import code.datastructure.Pair;

import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

class Ship implements Cloneable{
    public Pair<Integer, Integer> pos;
    public int passengerCount;
    public int blackBoxLive;
    public int id;

    public Ship(Pair<Integer, Integer> pos, int passengerCount, int blackBoxLive, int id){
        this.pos = pos;
        this.passengerCount = passengerCount;
        this.blackBoxLive = blackBoxLive;
        this.id = id;
    }

    // returns true if the ship is wreck
    public int update(){
        int lost = 0;
        if(passengerCount == 0) {
            blackBoxLive = Math.max(0, blackBoxLive - 1);
            if(blackBoxLive == 0)
                    lost ++;
        }
        if(passengerCount != 0) lost++;
        passengerCount = Math.max(0, passengerCount - 1);
        return lost;
    }

    public boolean isWreck(){
        return passengerCount == 0 && blackBoxLive == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return passengerCount == ship.passengerCount && blackBoxLive == ship.blackBoxLive && Objects.equals(pos, ship.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, passengerCount, blackBoxLive);
    }

    @Override
    public Ship clone() {
        try {
            Ship clone = (Ship) super.clone();
            clone.pos = this.pos.clone();
            clone.passengerCount = this.passengerCount;
            clone.blackBoxLive = this.blackBoxLive;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String serialize(){
        return pos.toString() + "_" + passengerCount + "_" + blackBoxLive;
    }

    @Override
    public String toString(){
        return pos.toString() + "," + passengerCount + "," + blackBoxLive;
    }
}
public class CoastGuardState implements Cloneable {
    // TODO DS needs to be modified
    // ArrayList has bad PROF
    public static int gridW, gridH;
    public static TreeSet<Pair<Integer, Integer>> stations;

    public Pair<Integer, Integer> pos;
    public int passengerOnBoard;
    public TreeMap<Pair<Integer, Integer>, Ship> ships;

    // info
    public int retrievedBoxes, deadPassengers, savedPassengers;


    @Override
    public CoastGuardState clone() {
        try {
            CoastGuardState clone = (CoastGuardState) super.clone();
            clone.pos = this.pos.clone();
            clone.passengerOnBoard = this.passengerOnBoard;
            clone.ships = new TreeMap<>();
            // deep clone
            for(var entry : this.ships.entrySet())
                clone.ships.put(entry.getKey(), entry.getValue().clone());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoastGuardState that = (CoastGuardState) o;
        // Compare ships
        boolean similarShips = true;
        if(that.ships.size() != this.ships.size())
            similarShips = false;
        else {
            TreeSet<Pair<Integer, Integer>> hs = new TreeSet<>();
            for (var entry : that.ships.entrySet())
                hs.add(entry.getKey());
            for (var entry : this.ships.entrySet())
                if(!hs.contains(entry.getKey()) || !that.ships.get(entry.getKey()).equals(entry.getValue()))
                    similarShips = false;
        }
        return passengerOnBoard == that.passengerOnBoard && Objects.equals(pos, that.pos) && similarShips;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, passengerOnBoard, ships);
    }

    public String serialize(){
        String serializedShips = "";
        for(var entry : ships.entrySet())
            serializedShips += entry.getValue().serialize();
        return pos.toString() + "_" + passengerOnBoard + "_" + serializedShips;
    }
    @Override
    public String toString(){
        return pos.toString() + "," + passengerOnBoard + ";" + ships.toString();
    }

    public String gridRep() {
        char[][] grid = new char[gridH][gridW];
        for(char[] x:grid)
            Arrays.fill(x, '.');
        for(Pair<Integer, Integer> st: stations)
            grid[st.first][st.second] = 'S';
        for(Ship s: ships.values())
            grid[s.pos.first][s.pos.second] = (char)(s.id + '0');
        grid[pos.first][pos.second] = 'B';
        StringBuilder sb = new StringBuilder();
        for(char[] x: grid)
            sb.append(String.valueOf(x)+"\n");
        return sb.toString();
    }
}
