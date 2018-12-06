package org.firstinspires.ftc.team7234.RoverRuckus.opmodes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team7234.RoverRuckus.common.AutoBase;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.AllianceColor;
import org.firstinspires.ftc.team7234.RoverRuckus.common.enums.FieldPosition;
/*
Because the field is split down the center, we can use the same code to perform some of the same
tasks.  So, the BlueCrater, BlueDepot, RedCrater, and RedDepot opmodes all extend to the AutoBase
opmode.
 */
@Autonomous(name = "Red Depot Autonomous", group = "Botman")
public class RedDepot extends AutoBase {
    public RedDepot(){
        super(AllianceColor.RED, FieldPosition.DEPOT);
    }
}
