package org.firstinspires.ftc.team7234.RoverRuckus.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.GoldMineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.MineralPosition;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.SilverMineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();

    private final AllianceColor allianceColor;
    private final FieldPosition fieldPosition;
    private final String TAG = "Autonomous";

    private static final int sampleRate = 4; //How many samples to take, per second.
    private static final int timeDelay = 1000/sampleRate; //Delay between samples in milliseconds

    private ArrayList<Mineral> allMinerals = new ArrayList<>();


    private MineralPosition finalPos = null;

    private final double EXTENSION_TARGET = -38245;
    private final double LEFT_MINERAL_THETA = -15;
    private final double RIGHT_MINERAL_THETA = 15;

    private final double DEPOT_THETA = -135; //Rotation target for going from



    private ScheduledExecutorService timer;
    private Runnable mineralSensor;

    private int samplesTaken = 0;

    ElapsedTime elapsedTime = new ElapsedTime();



    public AutoBase(AllianceColor allianceColor, FieldPosition fieldPosition){
        this.allianceColor = allianceColor;
        this.fieldPosition = fieldPosition;
    }



    public enum CurrentState{
        PREP,               //Preparation for the match, sets up the timers and starts moving the robot
        LOWER,              //Lower from the hook and onto the field
        FORWARD,            //Briefly move forward from the hook so that we're not touching
        EVALUATE_MINERALS,  //Stop scanning for the minerals and start
        TURN_TO_MINERAL,    //Turn to the mineral in order to knock it off
        REMOVE_MINERAL,     //Go forward to knock off the mineral

        CRATER_TURN_TO_DEPOT, //Turn towards the depot, from the crater
        CRATER_NAV_TO_DEPOT,  //Navigate to the depot from the crater

        DEPOT_TURN_TO_DEPOT,
        DEPOT_GO_TO_DEPOT,

        DEPOSIT_OBJECT,
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

                        List<Mineral> minerals = robot.detector.getMinerals();
                        allMinerals.addAll(minerals);
                        samplesTaken++;

                    }
                    catch (Exception ex){
                        Log.w(TAG, ex.getMessage());
                    }

                }
            };
            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(mineralSensor, 250, timeDelay, TimeUnit.MILLISECONDS);
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

        telemetry.addData("Program State: ", state);
        telemetry.addData("Expected Mineral Position: ", finalPos);
        telemetry.addData("Samples Taken: ", samplesTaken);
        telemetry.addData("Robot Heading: ", robot.heading());

        switch (state){
            case PREP:
                elapsedTime.reset();
                robot.extension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.extension.setPower(1.0);
                state = CurrentState.LOWER;
                break;
            case LOWER:
                if (robot.extension.getCurrentPosition() < EXTENSION_TARGET){
                    elapsedTime.reset();

                    if (timer != null){

                        timer.shutdownNow();
                    }

                    robot.extension.setPower(0.0);

                    state = CurrentState.FORWARD;
                }

                break;
            case FORWARD:         //Moves the robot forward to completely detach from the latch.  May need modification.
                if (elapsedTime.milliseconds() >= 1000){
                    elapsedTime.reset();

                    robot.leftWheel.setPower(0.0);
                    robot.rightWheel.setPower(0.0);
                    state = CurrentState.EVALUATE_MINERALS;
                }
                else {
                    robot.leftWheel.setPower(.75);
                    robot.rightWheel.setPower(.75);
                }
                break;
            case EVALUATE_MINERALS:
                try {
                    ArrayList<Mineral> goldReadings = new ArrayList<>();
                    ArrayList<Mineral> silverReadings = new ArrayList<>();

                    for (Mineral m :
                            allMinerals) {
                        if (m instanceof GoldMineral) {
                            goldReadings.add(m);
                        }
                        else if (m instanceof SilverMineral){
                            silverReadings.add(m);
                        }
                    }


                    //region Averaging
                    OptionalDouble avgGold = goldReadings.stream().mapToDouble(Mineral::getX).average();
                    OptionalDouble avgSilver = silverReadings.stream().mapToDouble(Mineral::getX).average();

                    ArrayList<Mineral> leftSilverReadings = new ArrayList<>();
                    ArrayList<Mineral> rightSilverReadings = new ArrayList<>();

                    if (avgSilver.isPresent()){
                        for (Mineral m :
                                silverReadings) {
                            if (m.getX() < avgSilver.getAsDouble()) {
                                leftSilverReadings.add(m);
                            }
                            else {
                                rightSilverReadings.add(m);
                            }
                        }
                    }
                    else {
                        finalPos = MineralPosition.CENTER;
                    }


                    OptionalDouble leftSilver = leftSilverReadings.stream().mapToDouble(Mineral::getX).average();
                    OptionalDouble rightSilver = rightSilverReadings.stream().mapToDouble(Mineral::getX).average();

                    //endregion
                    //region Set Positions
                    if (avgGold.isPresent() && leftSilver.isPresent() && rightSilver.isPresent()){
                        if (avgGold.getAsDouble() < leftSilver.getAsDouble()){
                            finalPos = MineralPosition.LEFT;
                        }
                        else if (avgGold.getAsDouble() > rightSilver.getAsDouble()){
                            finalPos = MineralPosition.RIGHT;
                        }
                        else{
                            finalPos = MineralPosition.CENTER;
                        }
                    }
                    else if (avgGold.isPresent() && leftSilver.isPresent()){
                        if (avgGold.getAsDouble() < leftSilver.getAsDouble()){
                            finalPos = MineralPosition.LEFT;
                        }
                        else{
                            finalPos = MineralPosition.CENTER;
                        }
                    }
                    else if (avgGold.isPresent() && rightSilver.isPresent()){
                        if (avgGold.getAsDouble() > rightSilver.getAsDouble()){
                            finalPos = MineralPosition.RIGHT;
                        }
                        else{
                            finalPos = MineralPosition.CENTER;
                        }
                    }
                    else {
                        finalPos = MineralPosition.CENTER;
                    }
                    //endregion
                }
                catch (Exception ex){
                    finalPos = MineralPosition.CENTER;
                }
                finally {
                    state = CurrentState.TURN_TO_MINERAL;
                }

                break;
            case TURN_TO_MINERAL:
                switch (finalPos){
                    case CENTER:
                        elapsedTime.reset();
                        state = CurrentState.REMOVE_MINERAL;
                        break;
                    case LEFT:
                        robot.leftWheel.setPower(-0.5);
                        robot.rightWheel.setPower(0.5);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + LEFT_MINERAL_THETA);
                        if (robot.heading() < LEFT_MINERAL_THETA){
                            elapsedTime.reset();
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                    case RIGHT:
                        robot.leftWheel.setPower(0.5);
                        robot.rightWheel.setPower(-0.5);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + LEFT_MINERAL_THETA);
                        if (robot.heading() < RIGHT_MINERAL_THETA){
                            elapsedTime.reset();
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                    default:
                        elapsedTime.reset();
                        state = CurrentState.REMOVE_MINERAL;
                        break;
                }
                break;

            case REMOVE_MINERAL:
                if (elapsedTime.milliseconds() >= 2000){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    switch (fieldPosition){
                        case DEPOT:
                            state = CurrentState.DEPOT_TURN_TO_DEPOT;
                            break;
                        case CRATER:
                            state = CurrentState.CRATER_NAV_TO_DEPOT;
                    }
                    state = CurrentState.STOP;
                }
                else {
                    robot.rightWheel.setPower(1.0);
                    robot.leftWheel.setPower(1.0);
                }
                break;

            case DEPOT_TURN_TO_DEPOT:
                switch (finalPos){
                    case CENTER:
                        break;
                    case LEFT:
                        break;
                    case RIGHT:
                        break;
                }
                state = CurrentState.STOP;
                break;
            case DEPOT_GO_TO_DEPOT:
                state = CurrentState.STOP;
                break;


            case CRATER_TURN_TO_DEPOT:
                state = CurrentState.STOP;
                break;

            case CRATER_NAV_TO_DEPOT:
                state = CurrentState.STOP;
                break;

            case STOP:
                robot.leftWheel.setPower(0.0);
                robot.rightWheel.setPower(0.0);
                robot.extension.setPower(0.0);
                break;
        }


    }

    /*
    NOTES FOR FUTURE
    Goals:
        + Replace Time moving forward with encoder counts
        + Verify functionality of heading methods
        +
     */



    @Override
    public void stop() {
        if (this.timer != null){

            Log.i(TAG, "Stopping timer");
            this.timer.shutdownNow();
        }

        robot.detector.stop();

    }
}
