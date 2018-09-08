package org.firstinspires.ftc.team7234.RelicRecovery.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team7234.RelicRecovery.common.AutoBase;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RelicRecovery.common.enums.FieldLocation;

@Autonomous(name = "NEWER Red Close Auto", group = "Inheritance Experiment")
public class RedClose extends AutoBase {
    public RedClose(){
        super(AllianceColor.RED, FieldLocation.CLOSE, "RedClose");
    }
}
