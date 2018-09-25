package org.firstinspires.ftc.team7234.RoverRuckus.opmodes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.team7234.RoverRuckus.common.HardwareBotman;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "Botman TeleOp", group = "Botman")
public class BotmanTeleOp extends OpMode {

    private HardwareBotman robot = new HardwareBotman();
    private final String logTag = HardwareBotman.class.getName();

    @Override
    public void init() {
        try {
            robot.init(hardwareMap);
        }
        catch (IllegalArgumentException ex){
            Log.e(logTag, ex.toString());
        }
        long t = robot.time;


        Log.i(logTag, "Robot Initialized in " + t + " ns");

    }

    @Override
    public void loop() {


    }


}
