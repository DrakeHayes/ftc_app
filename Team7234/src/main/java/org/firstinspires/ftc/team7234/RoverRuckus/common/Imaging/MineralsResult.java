package org.firstinspires.ftc.team7234.RoverRuckus.common.Imaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MineralsResult {

    private ArrayList<Mineral> minerals = new ArrayList<>();

    private MineralPosition position;



    MineralsResult(List<Mineral> minerals){
        this.minerals.addAll(minerals);
        position = evaluatePosition(this.minerals);
    }

    private MineralPosition getPosition() {
        return position;
    }


    public static MineralPosition evaluatePosition(List<Mineral> minerals){

        Collections.sort(minerals, Mineral.MineralSort.WIDTH_SORT);

        ArrayList<Mineral> closestMinerals = new ArrayList<>(3);

        closestMinerals.addAll(minerals.subList(0, 2));


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
