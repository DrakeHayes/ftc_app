package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.opencv.core.Core;

import java.util.ArrayList;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();

    VuforiaLocalizer vuforiaLocalizer;

    private ArrayList<Mineral> minerals = new ArrayList<>();


    @Override
    public void init() {

        robot.init(hardwareMap);




    }

    @Override
    public void start() {
        robot.detector.update();
    }

    @Override
    public void loop() {

    }
}
