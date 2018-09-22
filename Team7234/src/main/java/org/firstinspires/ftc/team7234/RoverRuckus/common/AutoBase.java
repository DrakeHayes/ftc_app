package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();
    @Override
    public void init() {

        robot.init(hardwareMap);

    }

    @Override
    public void loop() {

    }
}
