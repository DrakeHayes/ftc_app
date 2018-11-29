package org.firstinspires.ftc.team7234.RoverRuckus.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.MineralPosition;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.MineralsResult;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();

    private final AllianceColor allianceColor;
    private final FieldPosition fieldPosition;
    private final String TAG = "Autonomous";

    private static final int sampleRate = 1; //How many samples to take, per second.
    private static final int timeDelay = 1000/sampleRate; //Delay between samples in milliseconds

    private ArrayList<Mineral> minerals = new ArrayList<>();
    private LinkedList<MineralPosition> positions = new LinkedList<>();



    private ScheduledExecutorService timer;
    private Runnable mineralSensor;

    ElapsedTime elapsedTime = new ElapsedTime();



    public AutoBase(AllianceColor allianceColor, FieldPosition fieldPosition){
        this.allianceColor = allianceColor;
        this.fieldPosition = fieldPosition;
    }



    public enum CurrentState{
        PREP,
        LOWER,
        FORWARD,
        EVALUATE_MINERALS,
        TURN_TO_MINERAL,
        GO_TO_CORNER,
        STOP
    }

    private CurrentState state = CurrentState.PREP;


    @Override
    public void init() {

        robot.init(hardwareMap, true);
        if (fieldPosition == FieldPosition.CRATER){
            robot.detector.setMaskCrater(true);
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

                        positions.add(MineralsResult.evaluatePosition(minerals));

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
        ///robot.extension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop() {

        switch (state){
            case PREP:
                elapsedTime.reset();
                robot.extension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.extension.setPower(1.0);
                state = CurrentState.LOWER;
                break;
            case LOWER:
                if (elapsedTime.milliseconds() >= 13000){ //TODO: Change to encoder Counts
                    elapsedTime.reset();
                    if (timer != null){
                        timer.shutdownNow();
                    }

                    robot.extension.setPower(0.0);
                    state = CurrentState.STOP;
                }

                break;
            case EVALUATE_MINERALS:

                break;
            case TURN_TO_MINERAL:
                break;
            case FORWARD:
                break;
            case STOP:
                break;
        }


    }



    @Override
    public void stop() {
        if (this.timer != null){
            Log.i(TAG, "Stopping timer");
            this.timer.shutdownNow();
        }

        robot.detector.stop();

    }
}
