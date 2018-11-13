package org.firstinspires.ftc.team7234.RoverRuckus.opmodes;


import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.team7234.RoverRuckus.common.HardwareCameraOnly;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.GoldMineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.SilverMineral;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "OpenCV Testing", group = "BotmanTest")
public class OpenCVTestingOp extends OpMode {

    private HardwareCameraOnly robot = new HardwareCameraOnly();

    private static final int sampleRate = 1; //How many samples to take, per second.
    private static final int timeDelay = 1000/sampleRate; //Delay between samples in milliseconds

    private ScheduledExecutorService timer;

    ArrayList<Mineral> minerals = new ArrayList<>();

    private static final String TAG = "OpenCV Testing OpMode";


    @Override
    public void init() {
        try {
            robot.init(hardwareMap);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        Runnable mineralSensor = new Runnable() {
            @Override
            public void run() {
                robot.detector.update();
                minerals = robot.detector.getMinerals();

                Log.i(TAG, "Detected " + goldCount(minerals) + " Gold Minerals");
                Log.i(TAG, "Detected " + silverCount(minerals) + " Silver Minerals");

            }
        };

        this.timer = Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(mineralSensor, 0, timeDelay, TimeUnit.MILLISECONDS);

    }

    @Override
    public void loop() {
        //Empty, as I am instead looping via an executor service. I have no Idea if this will work, but I'm doing it anyway.
    }

    private static int goldCount(ArrayList<Mineral> minerals){
        int c = 0;
        for (Mineral min :
                minerals) {
            if (min instanceof GoldMineral){
                c++;
            }
        }
        return c;
    }
    private static int silverCount(ArrayList<Mineral> minerals){
        int c = 0;
        for (Mineral min :
                minerals) {
            if (min instanceof SilverMineral){
                c++;
            }
        }
        return c;
    }

}
