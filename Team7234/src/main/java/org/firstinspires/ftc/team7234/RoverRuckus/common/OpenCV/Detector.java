package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import com.qualcomm.robotcore.hardware.HardwareMap;


import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;

public class Detector {

    private final String BALLS_CLASSIFIER = "Team7234/dat/Classifers/lbpcascades_balls.xml";
    private final String BLOCKS_CLASSIFIER = "Team7234/dat/Classifers/lbpcascades_blocks.xml";

    private CascadeClassifier silverMineralClassifier = new CascadeClassifier(BALLS_CLASSIFIER);
    private CascadeClassifier goldMineralClassifier = new CascadeClassifier(BLOCKS_CLASSIFIER);

    private MatOfRect silverMineralDetections = new MatOfRect();
    private MatOfRect goldMineralDetections = new MatOfRect();

    private final static int FRAME_WIDTH = 800;
    private final static int FRAME_HEIGHT = 450;

    private static FrameGrabber frameGrabber;

    private Mat frame;

    private ArrayList<Mineral> minerals;

    public Detector(){
        frameGrabber = new FrameGrabber(FtcRobotControllerActivity.cameraBridgeViewBase, FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void update(){
        frame = frameGrabber.getFrame();
        findMinerals(minerals);
    }

    public Mat getFrame() {
        return frame;
    }

    public ArrayList<Mineral> getMinerals() { //By setting it up like this, we can avoid calling the resource-intensive detectMultiScale every time we need to check the minerals.
        return minerals;
    }

    public void findMinerals(ArrayList<Mineral> minerals){

        if (!minerals.isEmpty()){
            minerals.clear();
        }
        silverMineralClassifier.detectMultiScale(frame, silverMineralDetections);
        goldMineralClassifier.detectMultiScale(frame, goldMineralDetections);

        for (Rect rect :
                silverMineralDetections.toArray()) {
            minerals.add(new SilverMineral(rect.x, rect.y, rect.width, rect.height));
        }
        for (Rect rect:
                goldMineralDetections.toArray()){
            minerals.add(new GoldMineral(rect.x, rect.y, rect.width, rect.height));
        }

    }
}
