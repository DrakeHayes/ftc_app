package org.firstinspires.ftc.team7234.RoverRuckus.common;

import android.graphics.Camera;
import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.opencv.android.OpenCVLoader;

public class HardwareBotman {

    public HardwareBotman(){}

    //region Members
    public DcMotor rightWheel;
    public DcMotor leftWheel;

    DcMotor armExtension;
    DcMotor armElbow;
    DcMotor armWrist;

    DcMotor collector;

    DcMotor latchExtender;


    BNO055IMU imu;
    Orientation angles;

    //endregion

    /* local OpMode members. */
    private HardwareMap hwMap   = null;
    private ElapsedTime period  = new ElapsedTime();

    public int time;

    private final String logTag = HardwareBotman.class.getName();

    public void init(HardwareMap ahwMap){

        period.reset();
        //Save reference to Hardware map
        hwMap = ahwMap;

        //Define and initialize Motors
        leftWheel = hwMap.get(DcMotor.class, "Left Wheel");
        rightWheel = hwMap.get(DcMotor.class, "Right Wheel");


        leftWheel.setDirection(DcMotorSimple.Direction.FORWARD);
        rightWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set all motors to zero power
        leftWheel.setPower(0.);
        rightWheel.setPower(0.);


        //resets encoders
        resetEncoders();
        //Define and initialize servos

        //Define sensors

        //Set up OpenCV


        time = (int)period.milliseconds();
    }



    public void resetEncoders(){
        while (leftWheel.getCurrentPosition() != 0 && rightWheel.getCurrentPosition() != 0) {
            leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


}
