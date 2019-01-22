package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team7234.RoverRuckus.common.Imaging.ContourDetector;

/**
 * Empty Hardware Class for camera testing without an attached robot.
 */
public class HardwareCameraOnly {

    public HardwareCameraOnly(){

    }

    public ContourDetector detector;

    private HardwareMap hwMap = null;

    private ElapsedTime period = new ElapsedTime();
    private int time;

    public void init(HardwareMap ahwMap){
        hwMap = ahwMap;
        detector = new ContourDetector();
    }


}
