package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

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

        //Set all motors to zero power

        //resets encoders
        resetEncoders();
        //Define and initialize servos

        //Define sensors
    }

    public void resetEncoders(){

    }

}
