package org.firstinspires.ftc.team7234.RoverRuckus.opmodes.Autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team7234.RoverRuckus.common.AutoBase;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;

@Autonomous(name = "Red Crater Autonomous", group = "Botman")
public class RedCrater extends AutoBase {
    public RedCrater(){
        super(AllianceColor.RED, FieldPosition.CRATER);
    }
}
