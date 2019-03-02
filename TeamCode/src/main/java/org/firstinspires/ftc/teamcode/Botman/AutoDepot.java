package org.firstinspires.ftc.teamcode.Botman;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Depot Side", group = "Botman")
public class AutoDepot extends OpMode {

    HardwareBotman robot = new HardwareBotman();


    private final String TAG = "Autonomous";

    private static final int sampleRate = 4; //How many samples to take, per second.
    private static final int timeDelay = 1000/sampleRate; //Delay between samples in milliseconds

    private final double EXTENSION_TARGET = 10650;
    private final double LEFT_MINERAL_THETA = 22;
    private final double RIGHT_MINERAL_THETA = -23;
    private final double DEPOT_THETA = 5;
    private double PARKING_TARGET = 4000;
    private double CRATER_ANGLE = 85;

    private boolean modeChange = false;
    private boolean axisChanged = false;
    private boolean gyroRead = false;

 //   private final double DEPOT_THETA = -135; Rotation target for going from


    ElapsedTime elapsedTime = new ElapsedTime();


    public enum CurrentState{
        PREP,               //Preparation for the match, sets up the timers and starts moving the robot
        LOWER,              //Lower from the hook and onto the field
        FORWARD,            //Briefly move forward from the hook so that we're not touching
        EVALUATE_MINERALS,  //Stop scanning for the minerals and start
        TURN_TO_MINERAL,    //Turn to the mineral in order to knock it off
        REMOVE_MINERAL,     //Go forward to knock off the mineral

        DEPOT_TURN_TO_DEPOT,
        DEPOT_DRIVE_TO_DEPOT,
        DEPOT_LOWER_ARM,
        DEPOT_DISPENSE_MARKER,
        DEPOT_LIFT_ARM,
        DEPOT_TURN_TO_CRATER,
        DEPOT_DRIVE_FORWARDS,
        DEPOT_DRIVE_TO_CRATER,
        DEPOT_PARK_IN_CRATER,
        STOP
    }

    private CurrentState state = CurrentState.PREP;


    @Override
    public void init() {

        robot.init(hardwareMap, true);
        robot.extension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void init_loop(){
        if(!modeChange) {
            robot.gyroConfigMode();
            elapsedTime.reset();
            modeChange = true;
        } else if(!axisChanged && elapsedTime.milliseconds()>200){
            robot.changeAxis();
            elapsedTime.reset();
            axisChanged = true;
        } else if(!gyroRead && axisChanged) {
            robot.gyroReadMode();
            gyroRead = true;
            elapsedTime.reset();
        } else if(gyroRead && elapsedTime.milliseconds() > 200) {
            telemetry.addData("1 Program State: ", state);
            telemetry.addData("2 Robot Heading: ", robot.heading());
        }
        robot.ObjectDetetion(telemetry);

    }

    @Override
    public void start() {
        robot.extension.setDirection(DcMotor.Direction.REVERSE);
        robot.RunByEncoders();
        robot.tfod.deactivate();
    }

    @Override
    public void loop() {

        telemetry.addData("Program State: ", state);
        telemetry.addData("Robot Heading: ", robot.heading());

        switch (state){
            case PREP:
                elapsedTime.reset();
                robot.extension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.rightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.extension.setPower(1.0);
                state = CurrentState.LOWER;
                elapsedTime.reset();
                break;
            case LOWER:
                if (robot.extension.getCurrentPosition() >= EXTENSION_TARGET || elapsedTime.seconds() > 5){
                    elapsedTime.reset();
                    robot.extension.setPower(0.0);
                    state = CurrentState.FORWARD;
                }

                break;
            case FORWARD:         //Moves the robot forward to completely detach from the latch.  May need modification.
                if (robot.leftWheel.getCurrentPosition()>1000){

                    robot.leftWheel.setPower(0.0);
                    robot.rightWheel.setPower(0.0);
                    state = CurrentState.TURN_TO_MINERAL;
                }
                else {
                    robot.leftWheel.setPower(.4);
                    robot.rightWheel.setPower(.4);
                }
                break;

            case TURN_TO_MINERAL:
                switch (robot.mineralState){
                    case GOLD_CENTER:
                        robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        state = CurrentState.REMOVE_MINERAL;
                        break;
                    case GOLD_LEFT:
                        robot.leftWheel.setPower(-0.1);
                        robot.rightWheel.setPower(0.1);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + LEFT_MINERAL_THETA);
                        if (robot.heading() > LEFT_MINERAL_THETA){
                            robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                    case GOLD_RIGHT:
                        robot.leftWheel.setPower(0.1);
                        robot.rightWheel.setPower(-0.1);
                        Log.v(TAG, "Now turning to left mineral, robot heading is: " + robot.heading() + ", Target Heading is: " + RIGHT_MINERAL_THETA);
                        if (robot.heading() < RIGHT_MINERAL_THETA){
                            robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            state = CurrentState.REMOVE_MINERAL;
                        }
                        break;
                    default:
                        robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        state = CurrentState.REMOVE_MINERAL;
                        break;
                }
                break;

            case REMOVE_MINERAL:
                robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                if (robot.leftWheel.getCurrentPosition()>1900){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    state = CurrentState.DEPOT_TURN_TO_DEPOT;
                }
                else {
                    robot.rightWheel.setPower(0.4);
                    robot.leftWheel.setPower(0.4);
                }
                break;

            case DEPOT_TURN_TO_DEPOT:
                switch (robot.mineralState){
                    case GOLD_CENTER:
                        robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        state = CurrentState.DEPOT_DRIVE_TO_DEPOT;
                        break;
                    case GOLD_LEFT:
                        robot.leftWheel.setPower(0.1);
                        robot.rightWheel.setPower(-0.1);
                        Log.v(TAG, "Now turning to the depot, robot heading is: " + robot.heading() + ", Target Heading is: " + -DEPOT_THETA);
                        if (robot.heading() < -20){
                            robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            state = CurrentState.DEPOT_DRIVE_TO_DEPOT;
                        }
                        break;
                    case GOLD_RIGHT:
                        robot.leftWheel.setPower(-0.1);
                        robot.rightWheel.setPower(0.1);
                        Log.v(TAG, "Now turning to the depot, robot heading is: " + robot.heading() + ", Target Heading is: " + DEPOT_THETA);
                        if (robot.heading() > DEPOT_THETA){
                            robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            state = CurrentState.DEPOT_DRIVE_TO_DEPOT;
                        }
                        break;
                    default:
                        robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        state = CurrentState.DEPOT_DRIVE_TO_DEPOT;
                        break;
                }
                break;


            case DEPOT_DRIVE_TO_DEPOT:
                robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                if (robot.leftWheel.getCurrentPosition()>=500){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    elapsedTime.reset();
                    state = CurrentState.DEPOT_LOWER_ARM;
                }
                else {
                    robot.leftWheel.setPower(.3);
                    robot.rightWheel.setPower(.3);
                }
                break;

            case DEPOT_LOWER_ARM:
                if (elapsedTime.seconds()>3.5) {
                    robot.collector.setPower(0);
                    elapsedTime.reset();
                    state=CurrentState.DEPOT_LIFT_ARM;
                }
                else if (elapsedTime.seconds()<0.5){
                    robot.armCollectorRotation.setPower(.4);
                }
                else {
                    robot.collector.setPower(-1);
                    robot.armCollectorRotation.setPower(0);
                }
                break;

        /*    case DEPOT_DISPENSE_MARKER:

                if (elapsedTime.seconds()>3) {
                    robot.collector.setPower(0);
                    state=CurrentState.DEPOT_TURN_TO_CRATER;
                }
                else {
                    robot.collector.setPower(-1);
                }
                break;
*/
            case DEPOT_LIFT_ARM:
                if (elapsedTime.seconds()>0.75) {
                    robot.armCollectorRotation.setPower(0);
                    if (robot.mineralState== HardwareBotman.minerals.GOLD_CENTER){
                        CRATER_ANGLE=85;
                    }
                    else if (robot.mineralState== HardwareBotman.minerals.GOLD_RIGHT){
                        CRATER_ANGLE=75;
                    }
                    state=CurrentState.DEPOT_TURN_TO_CRATER;
                }
                else {
                    robot.armCollectorRotation.setPower(-.8);
                }
                break;

            case DEPOT_TURN_TO_CRATER:
                if (robot.heading() > CRATER_ANGLE){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    if (robot.mineralState== HardwareBotman.minerals.GOLD_LEFT){
                        PARKING_TARGET=3000;
                        state = CurrentState.DEPOT_DRIVE_TO_CRATER;
                    }
                    else if (robot.mineralState== HardwareBotman.minerals.GOLD_RIGHT){
                        PARKING_TARGET=4100;
                        state = CurrentState.DEPOT_DRIVE_FORWARDS;
                    }
                    else{
                        state = CurrentState.DEPOT_DRIVE_TO_CRATER;
                    }
                    robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                }
                else {
                    if (robot.mineralState== HardwareBotman.minerals.GOLD_LEFT){
                        robot.rightWheel.setPower(0.3);
                        robot.leftWheel.setPower(-0.3);
                    }
                    else if(robot.mineralState== HardwareBotman.minerals.GOLD_CENTER){
                        robot.rightWheel.setPower(0.3);
                        robot.leftWheel.setPower(-0.1);
                    }
                    else if(robot.mineralState== HardwareBotman.minerals.GOLD_RIGHT){
                        robot.rightWheel.setPower(0.4);
                        robot.leftWheel.setPower(0);
                    }
                }
                break;

            case DEPOT_DRIVE_FORWARDS:
                robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                if (robot.leftWheel.getCurrentPosition()>= 500){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    robot.leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    state = CurrentState.DEPOT_DRIVE_TO_CRATER;
                }
                else {
                    robot.rightWheel.setPower(0.4);
                    robot.leftWheel.setPower(0.4);
                }
                break;

            case  DEPOT_DRIVE_TO_CRATER:
                robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                if (robot.leftWheel.getCurrentPosition()>=PARKING_TARGET){
                    robot.rightWheel.setPower(0.0);
                    robot.leftWheel.setPower(0.0);
                    elapsedTime.reset();
                    state = CurrentState.DEPOT_PARK_IN_CRATER;
                }
                else {
                    robot.driveByGyro(0.4, 130);
                }
                break;

            case DEPOT_PARK_IN_CRATER:
                if (elapsedTime.seconds()>0.5) {
                    robot.armCollectorRotation.setPower(0);
                    state=CurrentState.STOP;
                }
                else {
                    robot.armCollectorRotation.setPower(0.4);
                }
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

    }
}
