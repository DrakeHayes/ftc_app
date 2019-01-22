package org.firstinspires.ftc.team7234.RoverRuckus.common.Imaging;

import android.content.Context;
import android.util.Log;


import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.team7234.R;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class CascadeDetector implements MineralDetector{

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

    private boolean maskCrater;

    public CascadeDetector(){
        maskCrater = false;

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
        Log.i(TAG, "Stopping CascadeDetector");
        frameGrabber.stop();
    }

    public Mat getFrame() {
        return frame;
    }


    public void setMaskCrater(boolean t){
        maskCrater = t;
    }

    public ArrayList<Mineral> getMinerals() { //By setting it up like this, we can avoid calling the resource-intensive detectMultiScale every time we need to check the minerals.
        return minerals;
    }


    public void findMinerals(List<Mineral> minerals){

        Mat testFrame = new Mat();

        if (maskCrater){
            frame.copyTo(testFrame, craterMask(frame));
        }
        else {
            frame.copyTo(testFrame);
        }

        isFinished = false;

        if (!minerals.isEmpty()){
            minerals.clear();
        }
        silverMineralClassifier.detectMultiScale(testFrame, silverMineralDetections);
        goldMineralClassifier.detectMultiScale(testFrame, goldMineralDetections);

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

    private Mat craterMask(Mat frame){
        ArrayList<Mat> channels = new ArrayList<>();


        Mat hsv = new Mat(); //HSV version of the image
        Mat mask = new Mat(frame.rows(), frame.cols(), CvType.CV_8U, Scalar.all(255)); //Mask to be applied, all vals are 0xFF or true

        Mat vec = new Mat(); //1D projection of the Value, using averages


        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV); //Converts image into hsv

        Core.split(hsv, channels);

        Core.reduce(channels.get(2), vec, 1, Core.REDUCE_AVG);  //Use 1 for a single column, so averaging each row

        Core.MinMaxLocResult result = Core.minMaxLoc(vec);

        Point max = result.minLoc; //Position of the minimum value, should be where the crater wall is, assuming the camera is perp to ground

        Imgproc.rectangle(mask, //Fills area above min value with 0x00 or false
                new Point(0, 0),
                new Point(frame.cols(), max.y),
                new Scalar(0),
                -1
        );




        return mask;
    }

    @Override
    public MineralPosition expectedPosition() {
        try {
            ArrayList<Mineral> goldReadings = new ArrayList<>();
            ArrayList<Mineral> silverReadings = new ArrayList<>();

            for (Mineral m :
                    minerals) {
                if (m instanceof GoldMineral) {
                    goldReadings.add(m);
                } else if (m instanceof SilverMineral) {
                    silverReadings.add(m);
                }
            }


            //region Averaging
            OptionalDouble avgGold = goldReadings.stream().mapToDouble(m -> (m.getX() + m.getWidth() / 2.0)).average();
            OptionalDouble avgSilver = silverReadings.stream().mapToDouble(m -> (m.getX() + m.getWidth() / 2.0)).average();

            ArrayList<Mineral> leftSilverReadings = new ArrayList<>();
            ArrayList<Mineral> rightSilverReadings = new ArrayList<>();

            if (avgSilver.isPresent()) {
                for (Mineral m :
                        silverReadings) {
                    if (m.getX() < avgSilver.getAsDouble()) {
                        leftSilverReadings.add(m);
                    } else {
                        rightSilverReadings.add(m);
                    }
                }
            } else {
                return MineralPosition.CENTER;
            }


            OptionalDouble leftSilver = leftSilverReadings.stream().mapToDouble(m -> (m.getX() + m.getWidth() / 2.0)).average();
            OptionalDouble rightSilver = rightSilverReadings.stream().mapToDouble(m -> (m.getX() + m.getWidth() / 2.0)).average();

            //endregion
            //region Set Positions
            if (avgGold.isPresent() && leftSilver.isPresent() && rightSilver.isPresent()) {
                Log.d(TAG, "Average Gold Position: " + avgGold.getAsDouble());
                Log.d(TAG, "Right Silver Position: " + rightSilver.getAsDouble());
                Log.d(TAG, "Left Silver Position: " + leftSilver.getAsDouble());
                if (avgGold.getAsDouble() < leftSilver.getAsDouble()) {
                    return MineralPosition.LEFT;
                }
                else if (avgGold.getAsDouble() > rightSilver.getAsDouble()) {
                    return MineralPosition.RIGHT;
                }
                else {
                    return MineralPosition.CENTER;
                }

            } else if (avgGold.isPresent() && leftSilver.isPresent()) {
                Log.d(TAG, "Average Gold Position: " + avgGold.getAsDouble());
                Log.d(TAG, "Left Silver Position: " + leftSilver.getAsDouble());
                if (avgGold.getAsDouble() < leftSilver.getAsDouble()) {
                    return MineralPosition.LEFT;
                } else {
                    return MineralPosition.CENTER;
                }

            } else if (avgGold.isPresent() && rightSilver.isPresent()) {
                Log.d(TAG, "Average Gold Position: " + avgGold.getAsDouble());
                Log.d(TAG, "Right Silver Position: " + rightSilver.getAsDouble());
                if (avgGold.getAsDouble() > rightSilver.getAsDouble()) {
                    return MineralPosition.RIGHT;
                } else {
                    return MineralPosition.CENTER;
                }

            } else {
                return MineralPosition.CENTER;
            }
            //endregion
        }
        catch (Exception ex){
            Log.w(TAG, "Exception " + ex + " Encountered in Locating Mineral, defaulting to center.");
            return MineralPosition.CENTER;
        }

    }

}
