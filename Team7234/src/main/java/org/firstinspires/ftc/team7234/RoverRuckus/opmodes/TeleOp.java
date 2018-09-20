package org.firstinspires.ftc.team7234.RoverRuckus.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.team7234.RoverRuckus.common.Hardware;

public class TeleOp extends OpMode {

    private Hardware robot = new Hardware();
    private final String logTag = Hardware.class.getName();

    @Override
    public void init() {
        robot.init(hardwareMap);

    }

    @Override
    public void loop() {

    }


}
