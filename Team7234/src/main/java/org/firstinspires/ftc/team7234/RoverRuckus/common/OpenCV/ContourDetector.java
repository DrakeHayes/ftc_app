package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import android.content.Context;
import android.util.Log;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContourDetector implements MineralDetector {

    private final String TAG = "ContourDetector";

    private final static int FRAME_WIDTH = 640;
    private final static int FRAME_HEIGHT = 480;

    private static FrameGrabber frameGrabber;

    private Mat frame;

    private ArrayList<Mineral> minerals = new ArrayList<>();

    private Context context;

    public boolean isFinished = false;

    private boolean maskCrater;

    ContourDetector(){
        maskCrater = false;

        context = FtcRobotControllerActivity.getActivityContext();

        frameGrabber = new FrameGrabber(FtcRobotControllerActivity.cameraBridgeViewBase, FRAME_WIDTH, FRAME_HEIGHT);

        Log.i(TAG, "Successfully started contour detector");

    }
    @Override
    public Mat getFrame() {
        return frame;
    }

    @Override
    public void update() {
        Log.i(TAG, "Update Method Called");
        frame = frameGrabber.getFrame();
        findMinerals(minerals);
    }

    @Override
    public void setMaskCrater(boolean maskCrater) {
        this.maskCrater = maskCrater;
    }

    @Override
    public ArrayList<Mineral> getMinerals() {
        return minerals;
    }

    @Override
    public void findMinerals(List<Mineral> minerals) {

        List<Mineral> out = new ArrayList<>();

        List<MatOfPoint> blockContours = new ArrayList<>();
        List<MatOfPoint> ballContours = new ArrayList<>();

        Mat blockHierarchy = new Mat();
        Mat ballHierarchy = new Mat();

        Mat blurred = new Mat();

        if (maskCrater){
            frame.copyTo(blurred, craterMask(frame));
        }
        else {
            frame.copyTo(blurred);
        }

        Imgproc.blur(blurred, blurred, new Size(15, 15));

        //Min and Max HSV for Gold Minerals
        Scalar blockMin = new Scalar(0, 70, 150);
        Scalar blockMax = new Scalar(33, 255, 255);

        //Min and Max HSV for Silver Minerals
        Scalar ballMin = new Scalar(0, 0, 220);
        Scalar ballMax = new Scalar(180, 20, 255);

        Mat hsvImage = new Mat();

        Imgproc.cvtColor(blurred, hsvImage, Imgproc.COLOR_BGR2HSV);

        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Mat blockMask = new Mat();
        Mat ballMask = new Mat();

        Core.inRange(hsvImage, blockMin, blockMax, blockMask);
        Core.inRange(hsvImage, ballMin, ballMax, ballMask);

        Imgproc.erode(blockMask, blockMask, erodeElement);
        Imgproc.erode(ballMask, ballMask, erodeElement);

        Imgproc.dilate(blockMask, blockMask, dilateElement);
        Imgproc.dilate(ballMask, ballMask, dilateElement);

        Imgproc.findContours(blockMask, blockContours, blockHierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(ballMask, ballContours, ballHierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);


        //TODO: Modify this to filter out small contours

        Optional<MatOfPoint> goldMineralLoc = blockContours //Find largest block
                .stream()
                .max(Comparator.comparing(Imgproc::contourArea));

        if (goldMineralLoc.isPresent()){
            Rect r = Imgproc.boundingRect(goldMineralLoc.get());
            out.add(new GoldMineral(r.x, r.y, r.width, r.height));
        }


        List<MatOfPoint> sortedSilver = ballContours //Sorts the silver minerals by size
                .stream()
                .sorted(Comparator.comparing(Imgproc::contourArea))
                .collect(Collectors.toList());

        if (sortedSilver.size() ==1){
            Rect r = Imgproc.boundingRect(sortedSilver.get(0));
            out.add(new SilverMineral(r.x, r.y, r.width, r.height));
        }
        else if (sortedSilver.size() > 2){
            for (int i = 0; i < 2; i++) {
                Rect r = Imgproc.boundingRect(sortedSilver.get(i));
                out.add(new SilverMineral(r.x, r.y, r.width, r.height));
            }
        }

        minerals.addAll(out); //Adds minerals found to original list

        Log.i(TAG, "Detection complete, "
                + ((goldMineralLoc.isPresent()) ? "Found Gold Mineral" : "Did not find gold mineral")
                + ", and did find"
                + ((sortedSilver.size() > 2) ? "2 Silver Minerals." : sortedSilver.size() + " Silver minerals.")
        );
    }

    @Override
    public void stop(){
        Log.i(TAG, "Stopping Contour Detector");
        frameGrabber.stop();
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
}
