package org.firstinspires.ftc.team7234.RoverRuckus.common.Imaging;

public class SilverMineral extends Mineral {

    SilverMineral(int x, int y, int width, int height){
        super(x,y,width,height);
    }

    @Override
    MineralType getType() {
        return MineralType.SILVER;
    }
}