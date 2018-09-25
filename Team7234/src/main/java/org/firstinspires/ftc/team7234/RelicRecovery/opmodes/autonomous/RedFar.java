package org.firstinspires.ftc.team7234.RelicRecovery.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.team7234.RelicRecovery.common.AutoBase;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.FieldLocation;

@Disabled
@Autonomous(name = "NEWER Red Far Auto", group = "Inheritance Experiment")
public class RedFar extends AutoBase {
    public RedFar(){
        super(AllianceColor.RED, FieldLocation.FAR, "RedClose");
    }
}
