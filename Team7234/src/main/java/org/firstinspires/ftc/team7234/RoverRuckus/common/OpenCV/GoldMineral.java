package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

public class GoldMineral extends Mineral {
    GoldMineral(int x, int y, int width, int height){
        super(x,y,width,height);
    }

    @Override
    MineralType getType() {
        return MineralType.GOLD;
    }
}
