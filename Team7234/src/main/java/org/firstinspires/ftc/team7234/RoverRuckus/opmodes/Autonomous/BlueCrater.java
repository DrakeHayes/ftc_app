package org.firstinspires.ftc.team7234.RoverRuckus.opmodes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team7234.RoverRuckus.common.AutoBase;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;

@Autonomous(name = "Blue Crater Autonomous", group = "Botman")
public class BlueCrater extends AutoBase {
    public BlueCrater(){
        super(AllianceColor.BLUE, FieldPosition.CRATER);
    }
}
