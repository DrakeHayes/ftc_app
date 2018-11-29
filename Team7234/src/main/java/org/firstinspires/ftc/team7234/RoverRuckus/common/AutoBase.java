package org.firstinspires.ftc.team7234.RoverRuckus.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.GoldMineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.MineralPosition;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.MineralsResult;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.SilverMineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;

import java.util.ArrayList;
import java.util.LinkedList;
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

    private static final int sampleRate = 1; //How many samples to take, per second.
    private static final int timeDelay = 1000/sampleRate; //Delay between samples in milliseconds

    private ArrayList<Mineral> allMinerals = new ArrayList<>();
    private LinkedList<MineralPosition> positions = new LinkedList<>();

    private MineralPosition finalPos;

    private final double LEFT_MINERAL_THETA = -15;
    private final double RIGHT_MINERAL_THETA = 15;



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
        REMOVE_MINERAL,
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


                        List<Mineral> minerals = robot.detector.getMinerals();
                        allMinerals.addAll(minerals);

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

                    state = CurrentState.FORWARD;
                }

                break;
            case FORWARD:         //Moves the robot forward to completely detach from the latch.  May need modification.
                if (elapsedTime.milliseconds() >= 1000){
                    elapsedTime.reset();

                    robot.leftWheel.setPower(0.0);
                    robot.rightWheel.setPower(0.0);
                    state = CurrentState.STOP;
                }
                else {
                    robot.leftWheel.setPower(.75);
                    robot.rightWheel.setPower(.75);
                }
                break;
            case EVALUATE_MINERALS:

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

                ArrayList<Integer> goldPositions = new ArrayList<>();
                ArrayList<Integer> silverPositions = new ArrayList<>();

                for (Mineral g :
                        goldReadings) {
                    goldPositions.add(g.getX());
                }
                for (Mineral s :
                        silverReadings) {
                    silverPositions.add(s.getX());
                }

                //region Averaging
                OptionalDouble avgGold = goldPositions.stream().mapToDouble(a -> a).average();
                OptionalDouble avgSilver = silverPositions.stream().mapToDouble(a -> a).average();

                ArrayList<Integer> leftSilverPositions = new ArrayList<>();
                ArrayList<Integer> rightSilverPositions = new ArrayList<>();

                if (avgSilver.isPresent()){
                    for (Integer i :
                            silverPositions) {
                        if (i < avgSilver.getAsDouble()) {
                            leftSilverPositions.add(i);
                        }
                        else {
                            rightSilverPositions.add(i);
                        }
                    }
                }
                else {
                    finalPos = MineralPosition.CENTER;
                }


                OptionalDouble leftSilver = leftSilverPositions.stream().mapToDouble(a -> a).average();

                OptionalDouble rightSilver = rightSilverPositions.stream().mapToDouble(a -> a).average();

                //endregion

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

                state = CurrentState.TURN_TO_MINERAL;
                break;
            case TURN_TO_MINERAL:
                switch (finalPos){
                    case CENTER:
                        state = CurrentState.REMOVE_MINERAL;
                        break;
                    case LEFT:
                        robot.leftWheel.setPower(-0.5);
                        robot.rightWheel.setPower(0.5);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + LEFT_MINERAL_THETA);
                        if (robot.heading() < LEFT_MINERAL_THETA){
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                    case RIGHT:
                        robot.leftWheel.setPower(0.5);
                        robot.rightWheel.setPower(-0.5);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + LEFT_MINERAL_THETA);
                        if (robot.heading() < RIGHT_MINERAL_THETA){
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                }
                break;

            case REMOVE_MINERAL:
                break;
            case STOP:
                robot.leftWheel.setPower(0.0);
                robot.rightWheel.setPower(0.0);
                robot.extension.setPower(0.0);
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
