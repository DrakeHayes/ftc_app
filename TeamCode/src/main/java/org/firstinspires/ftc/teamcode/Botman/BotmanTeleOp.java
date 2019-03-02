package org.firstinspires.ftc.teamcode.Botman;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name = "Botman TeleOp", group = "Botman")
public class BotmanTeleOp extends OpMode {

    private HardwareBotman robot = new HardwareBotman();
    private final String logTag = HardwareBotman.class.getName();

    //Define the speed of the motors as doubles
    private double leftSpeed;
    private double rightSpeed;

    private double extendSpeed;

    private double armExtendSpeed;
    private double armRotSpeed;

    private double collectingArmRotSpeed;
    private double collectingArmExtSpeed;

    private double collector;

    private double lockHeading;

    private boolean collectorToggle;
//    private boolean doGyroDrive;
//    private boolean gyroToggle;

    private ElapsedTime elapsedTime;

    private int minTwist;
    private int maxTwist;

    @Override
    public void init() {
        elapsedTime = new ElapsedTime();
            /* This line of code tells that robot to ignore the camera.  We do not want to
            use the camera as of now because we are currently running into problems.  The phone
            is not mounted in an ideal place for element detection.*/
        try {
            robot.init(hardwareMap, false);
        }
        catch (Exception ex){
            Log.e(logTag, ex.toString());
        }

        Log.i(logTag, "Robot Initialized in " + robot.time + " ms");

        robot.resetEncoders();
        robot.extension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.Elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        collectorToggle = false;


     //   robot.armExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
     //   robot.Elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        elapsedTime.reset();
    }

    @Override
    public void start() {
        robot.leftWheel.setDirection(DcMotor.Direction.REVERSE);
        robot.rightWheel.setDirection(DcMotor.Direction.FORWARD);
        elapsedTime.reset();
      //  minTwist = robot.Elbow.getCurrentPosition();
      //  maxTwist = -150; //TODO: test and find correct position
    }


    @Override
    public void loop() {
       /*  This line of code determines which joysticks control which motors.  Earlier our
        driver was having trouble controlling the robot.  So, we inverted the controls.  */
        rightSpeed = gamepad1.right_stick_y;
        leftSpeed = gamepad1.left_stick_y;

        /*
        if (gamepad1.left_trigger>0||gamepad1.left_trigger<0){
            robot.leftWheel.setPower(leftSpeed);
        }
        else {
            robot.leftWheel.setPower(0);
        }

        if (gamepad1.left_trigger>0||gamepad1.left_trigger<0){
            robot.rightWheel.setPower(leftSpeed);
        }
        else {
            robot.rightWheel.setPower(0);
        }
        */

        extendSpeed = gamepad1.left_trigger - gamepad1.right_trigger;

        collectingArmRotSpeed= 0.85*gamepad2.right_stick_y;
        if (gamepad2.left_bumper) {
            collectingArmExtSpeed=1;
        }
        else if (gamepad2.right_bumper) {
            collectingArmExtSpeed=-1;
        }
        else {
            collectingArmExtSpeed=0;
        }

        armExtendSpeed = gamepad2.left_trigger - gamepad2.right_trigger;
        //armRotSpeed = (gamepad2.left_stick_y > 0) ? gamepad2.left_stick_y * gamepad2.left_stick_y : -1*gamepad2.left_stick_y*gamepad2.left_stick_y;

        armRotSpeed = Range.clip(0.85*gamepad2.left_stick_y,-0.4,0.4);
        //Set the power of the motors to previously defined speeds

        /*if (robot.armTwist.getCurrentPosition() > minTwist && armRotSpeed > 0){
            armRotSpeed = 0;
        }
        else if (robot.armTwist.getCurrentPosition() < maxTwist && armRotSpeed < 0){
            armRotSpeed = 0;
        }*/

        robot.extension.setPower(extendSpeed);
        robot.armExtension.setPower(armExtendSpeed);
        robot.Elbow.setPower(armRotSpeed);
        robot.rightWheel.setPower(rightSpeed);
        robot.leftWheel.setPower(leftSpeed);
        robot.armCollectorRotation.setPower(collectingArmRotSpeed);
        robot.collectingArmExtension.setPower(collectingArmExtSpeed);


/*
        if (doGyroDrive){
            robot.driveByGyro(0.9*(rightSpeed+leftSpeed)/2, lockHeading);
        }
        else {
            robot.rightWheel.setPower(rightSpeed);
            robot.leftWheel.setPower(leftSpeed);
        }

        if (gyroToggle){ //toggles the gyro drive mode
            if (gamepad1.a){
                doGyroDrive = !doGyroDrive;
                lockHeading = robot.heading(); //Updates the heading lock whenever the gyro drive is toggled.
                gyroToggle = false;
            }
        }
        else if (!gamepad1.a){
            gyroToggle = true;
        }
*/
        if (collectorToggle && elapsedTime.milliseconds() > 5000){
            if (gamepad2.a){
                collectorToggle = false;
                robot.collector.setPower(1.0);
            }
            else if (gamepad2.b){
                collectorToggle = false;
                robot.collector.setPower(-1.0);
            }
            else if (gamepad2.x){
                collectorToggle = false;
                robot.collector.setPower(0.0);
            }
        }
        else if (!(gamepad2.a || gamepad2.b || gamepad2.x)){
            collectorToggle = true;
        }


/*
     //   telemetry.addData("Extension Position", robot.extension.getCurrentPosition());
        telemetry.addData("Robot Heading: ", robot.heading());
        telemetry.addData("Robot Pitch", robot.pitch());
        telemetry.addData("Robot Roll", robot.roll());

      //  telemetry.addData("Arm Twist", robot.Elbow.getCurrentPosition());

*/

    }


    @Override
    public void stop(){
        //Stop the motors
        robot.rightWheel.setPower(0.0);
        robot.leftWheel.setPower(0.0);
        robot.extension.setPower(0.0);
    }

}
