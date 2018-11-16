package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import android.app.Activity;
import android.util.Log;
import android.view.SurfaceView;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class FrameGrabber implements CameraBridgeViewBase.CvCameraViewListener2 {


    private Mat frame, tmp1, tmp2;
    CameraBridgeViewBase cameraBase;

    private final String TAG = "FrameGrabber";

    public FrameGrabber(final CameraBridgeViewBase cameraBridgeViewBase, final int frameWidthRequest, final int frameHeightRequest) {

        try {
            cameraBase = cameraBridgeViewBase;
            FtcRobotControllerActivity.runOnUi(new Runnable() {
                @Override
                public void run() {
                    cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

                    cameraBridgeViewBase.setMinimumWidth(frameWidthRequest);
                    cameraBridgeViewBase.setMinimumHeight(frameHeightRequest);
                    cameraBridgeViewBase.setMaxFrameSize(frameWidthRequest, frameHeightRequest);
                    cameraBridgeViewBase.setCvCameraViewListener(FrameGrabber.this);
                    cameraBridgeViewBase.enableView();
                }
            });
        }
        catch (Exception ex){
            Log.w(TAG,"Exception in constructor: " + ex.getMessage());
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //create the frame and tmp images
        frame = new Mat(height, width, CvType.CV_8UC4, new Scalar(0,0,0));
        tmp1 = new Mat(height, width, CvType.CV_8UC4);
        tmp2 = new Mat(width, height, CvType.CV_8UC4);
    }

    public void stop(){
        FtcRobotControllerActivity.runOnUi(new Runnable() {
            @Override
            public void run() {
                cameraBase.disableView();
                cameraBase.setVisibility(SurfaceView.INVISIBLE);

            }
        });

    }

    @Override
    public void onCameraViewStopped() {
        stop();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        processFrame(inputFrame);
        return frame;
    }

    Mat getFrame(){
        return frame;
    }

    private void processFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        tmp1 = inputFrame.rgba();
        Core.rotate(tmp1, frame, Core.ROTATE_90_CLOCKWISE);

        Core.transpose(frame, tmp1);
        Imgproc.resize(tmp1, tmp2, tmp2.size(), 0, 0, 0);
        Core.transpose(tmp2, frame);

    }
}
