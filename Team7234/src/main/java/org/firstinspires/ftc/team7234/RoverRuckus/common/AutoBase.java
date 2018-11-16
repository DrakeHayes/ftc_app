package org.firstinspires.ftc.team7234.RoverRuckus.common;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV.Mineral;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;
import org.opencv.core.Core;

import java.util.ArrayList;

public class AutoBase extends OpMode {

    HardwareBotman robot = new HardwareBotman();

    private final AllianceColor allianceColor;
    private final FieldPosition fieldPosition;
    private final String logTag;

    private ArrayList<Mineral> minerals = new ArrayList<>();



    public AutoBase(AllianceColor allianceColor, FieldPosition fieldPosition, String logTag){
        this.allianceColor = allianceColor;
        this.fieldPosition = fieldPosition;
        this.logTag = logTag;
    }



    public enum CurrentState{
        PREP,
        LOWER,
        FORWARD,
        EVALUATE_MINERALS,
        TURN_TO_MINERAL,
        GO_TO_CORNER,
    }

    private CurrentState state = CurrentState.PREP;


    @Override
    public void init() {

        robot.init(hardwareMap, true);

    }

    @Override
    public void start() {
        robot.detector.update();
        minerals = robot.detector.getMinerals();
    }

    @Override
    public void loop() {


    }
}
