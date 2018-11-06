package org.firstinspires.ftc.team7234.RoverRuckus.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.team7234.RoverRuckus.common.HardwareBotman;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "Botman TeleOp", group = "Botman")
public class BotmanTeleOp extends OpMode {

    private HardwareBotman robot = new HardwareBotman();
    private final String logTag = HardwareBotman.class.getName();

    double leftSpeed;
    double rightSpeed;

    @Override
    public void init() {
        try {
            robot.init(hardwareMap);
        }
        catch (IllegalArgumentException ex){
            Log.e(logTag, ex.toString());
        }


        Log.i(logTag, "Robot Initialized in " + robot.time + " ms");

    }

    @Override
    public void loop() {

        rightSpeed = gamepad1.right_stick_y;
        leftSpeed = gamepad1.left_stick_y;

        robot.rightWheel.setPower(rightSpeed);
        robot.leftWheel.setPower(leftSpeed);


    }


    @Override
    public void stop(){
        robot.rightWheel.setPower(0.0);
        robot.leftWheel.setPower(0.0);
    }

}
