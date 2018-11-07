package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.opencv.core.Core;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();

    VuforiaLocalizer vuforiaLocalizer;


    @Override
    public void init() {

        robot.init(hardwareMap);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);



    }

    @Override
    public void loop() {

    }
}
