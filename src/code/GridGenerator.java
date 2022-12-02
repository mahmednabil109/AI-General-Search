package code;

import code.datastructure.Pair;

import java.util.ArrayList;
import java.util.Random;

public class GridGenerator {
    static final int DIM_MIN = 5, DIM_MAX = 15;
    static final int BOAT_CAP_MIN = 30, BOAD_CAP_MAX = 100;
    static final int SHIP_PASS_MIN = 1, SHIP_PASS_MAX = 100;

    public static String generate() {
        StringBuilder sb = new StringBuilder();
        int cols = randIntBetween(DIM_MIN, DIM_MAX);
        int rows = randIntBetween(DIM_MIN, DIM_MAX);
        int boatCap = randIntBetween(BOAT_CAP_MIN, BOAD_CAP_MAX);
        ArrayList<Pair<Integer, Integer>> remPos = new ArrayList<>();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                remPos.add(new Pair(i, j));

        Pair<Integer, Integer> boatPos = removeRandom(remPos);

        // leave at least one position for the stations
        int shipCnt = randIntBetween(1, remPos.size()-1);
        ArrayList<Pair<Integer, Integer>> ships = new ArrayList<>();
        for (int i = 0; i < shipCnt; i++)
            ships.add(removeRandom(remPos));

        int stationCnt = randIntBetween(1, remPos.size());
        ArrayList<Pair<Integer, Integer>> stations = new ArrayList<>();
        for (int i = 0; i < stationCnt; i++)
            stations.add(removeRandom(remPos));

        sb.append(cols + "," + rows + ";");
        sb.append(boatCap + ";");
        sb.append(boatPos.first + "," + boatPos.second + ";");

        for (Pair<Integer, Integer> station : stations)
            sb.append(station.first + "," + station.second + ",");
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");

        for (Pair<Integer, Integer> ship : ships)
            sb.append(ship.first + "," + ship.second + "," + randIntBetween(SHIP_PASS_MIN, SHIP_PASS_MAX) + ",");
        sb.deleteCharAt(sb.length() - 1);
        sb.append(";");

        return sb.toString();
    }

    // generates random number inclusive [min:max]
    private static int randIntBetween(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private static Pair<Integer, Integer> removeRandom(ArrayList<Pair<Integer, Integer>> ls) {
        int idx = randIntBetween(0, ls.size() - 1);
        return ls.remove(idx);
    }

}
