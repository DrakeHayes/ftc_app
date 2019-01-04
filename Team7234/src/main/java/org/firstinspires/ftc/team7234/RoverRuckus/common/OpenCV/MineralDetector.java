package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public interface MineralDetector {

    public void update();
    public void stop();
    public void setMaskCrater(boolean t);
    public ArrayList<Mineral> getMinerals();
    public void findMinerals(List<Mineral> minerals);
    public MineralPosition expectedPosition();
    public Mat getFrame();

}
