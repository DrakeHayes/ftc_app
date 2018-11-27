package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import java.util.ArrayList;
import java.util.Collections;

public class MineralsResult {

    private ArrayList<Mineral> minerals = new ArrayList<>();

    private MineralPosition position;



    MineralsResult(ArrayList<Mineral> minerals){
        this.minerals = minerals;
        position = evaluatePosition(this.minerals);
    }

    private MineralPosition getPosition() {
        return position;
    }


    public static MineralPosition evaluatePosition(ArrayList<Mineral> minerals){

        Collections.sort(minerals, Mineral.MineralSort.WIDTH_SORT);

        ArrayList<Mineral> closestMinerals = new ArrayList<>(3);

        for (int i = 0; i < 3; i++) {
            closestMinerals.add(minerals.get(i));
        }

        Collections.sort(minerals, Mineral.MineralSort.X_SORT);
        int goldCount = 0;
        int goldPos = -1; //Value if failure

        for (Mineral min :
                closestMinerals) {
            if (min instanceof GoldMineral){
                goldCount ++;
                goldPos = closestMinerals.indexOf(min);
            }
        }
        if (goldCount != 1){ //If more than 1 result is returned
            return MineralPosition.INVALID;
        }
        switch (goldPos){
            case 0:
                return MineralPosition.LEFT;
            case 1:
                return MineralPosition.CENTER;
            case 2:
                return MineralPosition.RIGHT;
            default:
                return MineralPosition.INVALID; //Paranoia
        }
    }
}
