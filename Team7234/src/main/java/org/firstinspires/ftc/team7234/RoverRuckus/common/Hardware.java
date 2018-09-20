package org.firstinspires.ftc.team7234.RoverRuckus.common;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.opencv.android.OpenCVLoader;

public class Hardware {

    public Hardware(){}

    //region Members
    DcMotor leftTank;
    DcMotor leftRear;
    DcMotor rightTank;
    DcMotor rightRear;


    BNO055IMU imu;
    Orientation angles;
    //endregion

    /* local OpMode members. */
    private HardwareMap hwMap   = null;
    private ElapsedTime period  = new ElapsedTime();

    public void init(HardwareMap ahwMap){
        //Save reference to Hardware map
        hwMap = ahwMap;

        //Define and initialize Motors
        leftTank    = hwMap.get(DcMotor.class, "Left Tank");
        leftRear    = hwMap.get(DcMotor.class, "Left Rear");
        rightTank   = hwMap.get(DcMotor.class, "Right Tank");
        rightRear   = hwMap.get(DcMotor.class, "Right Rear");

        //Set all motors to zero power
        leftTank.setPower(0.0);
        leftRear.setPower(0.0);
        rightTank.setPower(0.0);
        rightRear.setPower(0.0);

        //resets encoders
        resetEncoders();
        //Define and initialize servos

        //Define sensors

        //Set up OpenCV
        
    }

    public void resetEncoders(){
        leftTank.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightTank.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftTank.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightTank.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

}
