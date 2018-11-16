package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import android.content.Context;
import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;


import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.team7234.R;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class Detector {

    private File BALLS_CLASSIFIER;
    private File BLOCKS_CLASSIFIER;

    private final String TAG = "BotmanDetector";

    private CascadeClassifier silverMineralClassifier;
    private CascadeClassifier goldMineralClassifier;

    private MatOfRect silverMineralDetections = new MatOfRect();
    private MatOfRect goldMineralDetections = new MatOfRect();

    private final static int FRAME_WIDTH = 640;
    private final static int FRAME_HEIGHT = 480;

    private static FrameGrabber frameGrabber;

    private Mat frame;

    private ArrayList<Mineral> minerals = new ArrayList<>();

    private Context context;

    public boolean isFinished = false;

    public Detector(){
        context = FtcRobotControllerActivity.getActivityContext();

        frameGrabber = new FrameGrabber(FtcRobotControllerActivity.cameraBridgeViewBase, FRAME_WIDTH, FRAME_HEIGHT);

        try {
            //Copy the resources into temporary files so that OpenCV can load it
            InputStream is1 = context.getResources().openRawResource(R.raw.lbpcascades_balls);
            InputStream is2 = context.getResources().openRawResource(R.raw.lbpcascades_blocks);

            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);

            BALLS_CLASSIFIER = new File(cascadeDir, "lbpcascades_balls.xml");
            BLOCKS_CLASSIFIER = new File(cascadeDir, "lbpcascades_blocks.xml");

            FileOutputStream os1 = new FileOutputStream(BALLS_CLASSIFIER);
            FileOutputStream os2 = new FileOutputStream(BLOCKS_CLASSIFIER);

            byte[] buffer1 = new byte[4096];
            int bytesRead1;

            while ((bytesRead1 = is1.read(buffer1)) != -1){
                os1.write(buffer1, 0, bytesRead1);
            }
            is1.close();
            os1.close();

            byte[] buffer2 = new byte[4096];
            int bytesRead2;
            while ((bytesRead2 = is2.read(buffer2)) != -1){
                os2.write(buffer2, 0, bytesRead2);
            }
            is2.close();
            os2.close();


            silverMineralClassifier = new CascadeClassifier(BALLS_CLASSIFIER.getAbsolutePath());
            goldMineralClassifier = new CascadeClassifier(BLOCKS_CLASSIFIER.getAbsolutePath());
            silverMineralClassifier.load(BALLS_CLASSIFIER.getAbsolutePath());
            goldMineralClassifier.load(BLOCKS_CLASSIFIER.getAbsolutePath());

        }
        catch (Exception ex){
            Log.e(TAG, "Error loading cascade: " + ex.getMessage());
        }
        finally {
            Log.i(TAG, "Balls Classifier Path: " + BALLS_CLASSIFIER.getAbsolutePath() );
            Log.i(TAG, "Blocks Classifier Path: " + BLOCKS_CLASSIFIER.getAbsolutePath() );
        }



    }

    public void update(){
        Log.i(TAG, "Update Method Called");
        frame = frameGrabber.getFrame();
        findMinerals(minerals);
    }
    public void stop(){
        Log.i(TAG, "Stopping Detector");
        frameGrabber.stop();
    }

    public Mat getFrame() {
        return frame;
    }

    public ArrayList<Mineral> getMinerals() { //By setting it up like this, we can avoid calling the resource-intensive detectMultiScale every time we need to check the minerals.
        return minerals;
    }


    public void findMinerals(ArrayList<Mineral> minerals){
        isFinished = false;

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

        isFinished=true;

    }
}
