#Team 7234: Botman
This directory contains the code for controlling FTC Team 7234's
robot for FTC Relic Recovery.

##Files
+ BotmanAutoSkeleton.java
    + Contains code for operation in Autonomous period
    + Coded by Ricky Harris
+ BotmanTeleOp.java
    + Contains code for operation in Driver Controlled Period
    + Allows Driver to control direction, speed, and rotation of the robot fluidly while in motion
    + Control Scheme:
        + Left Joystick controls the movement of the robot using angle and magnitude
        + Right Joystick x controls the rotation of the robot
        + Triggers control the arm movement
            + Left Trigger to Raise
            + Right Trigger to Lower
        + A button opens the Gripper
        + B button closes the Gripper
    + Notable Variables:
        + driveCurve: Defines the exponential curve of the magnitude, allowing for better control at low speeds.
    + Coded by Donald Brown
+ HardwareBotman.java
    + Contains code for hardware mapping of robot and various methods for controlling the robot
    + Methods:
        + init
            + Defines and initializes each part of the robot
            + Sets all motors to forward
            + Sets all motor powers to 0
            + Sets all motors to run without encoders
            + Sets all servos to inital positions
        + arrayDrive
            + Runs the wheels directly with four doubles in order:
                1. Front Left
                2. Front Right
                3. Back Left
                4. Back Right
        + MecanumDrive
            + Runs the wheels using angle, magnitude and rotation
            + Uses equations calculated by Team 2022, and available at <http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf>
            + Values have limited range, values outside those ranges will throw IllegalArgumentException
                + Magnitude must be within [0, 1]
                + Rotation must be within [-1, 1]
        + gripperOpen
            + Opens the gripper
        + gripperClose
            + Closes the gripper
##Credits
This code was contributed to by:

####Donald Brown
####Ricky Harris
####Matt Fullenwider