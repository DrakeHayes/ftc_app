package org.firstinspires.ftc.team7234.RelicRecovery.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.team7234.RelicRecovery.common.AutoBase;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.FieldLocation;

@Disabled
@Autonomous(name = "NEWER Blue Far Auto", group = "Inheritance Experiment")
public class BlueFar extends AutoBase {
    public BlueFar(){
        super(AllianceColor.BLUE, FieldLocation.FAR, "BlueFar");
    }
}
