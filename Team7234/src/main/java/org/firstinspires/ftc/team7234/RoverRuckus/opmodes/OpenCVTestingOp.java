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

    private Runnable mineralSensor;


    @Override
    public void init() {
        try {
            robot.init(hardwareMap);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void start() {
        try { //Starts the mineral sensor process on another Thread
            mineralSensor = new Runnable() {
                @Override
                public void run() {
                    try {
                        robot.detector.update();

                        minerals = robot.detector.getMinerals();

                        Log.i(TAG, "Detected " + goldCount(minerals) + " Gold Minerals");
                        Log.i(TAG, "Detected " + silverCount(minerals) + " Silver Minerals");

                        telemetry.addData("Gold Minerals Seen ", goldCount(minerals));
                        telemetry.addData("Silver Minerals Seen ", silverCount(minerals));
                        telemetry.addData("Expected Mineral Position: ",robot.detector.expectedPosition() );

                    }
                    catch (Exception ex){
                        Log.w(TAG, ex.getMessage());
                    }

                }
            };
            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(mineralSensor, 500, timeDelay, TimeUnit.MILLISECONDS);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }

    }

    @Override
    public void loop() {
        //Empty, as I am instead looping via an executor service.
    }

    @Override
    public void stop() {
        if (this.timer != null){
            Log.i(TAG, "Stopping timer");
            this.timer.shutdownNow();
        }

        robot.detector.stop();

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
