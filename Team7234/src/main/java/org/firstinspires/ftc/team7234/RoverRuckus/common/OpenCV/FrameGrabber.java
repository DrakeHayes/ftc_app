package org.firstinspires.ftc.team7234.RoverRuckus.common.OpenCV;

import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class FrameGrabber implements CameraBridgeViewBase.CvCameraViewListener2 {


    private Mat frame, tmp1, tmp2;


    public FrameGrabber(CameraBridgeViewBase cameraBridgeViewBase, int frameWidthRequest, int frameHeightRequest) {
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);

        cameraBridgeViewBase.setMinimumWidth(frameWidthRequest);
        cameraBridgeViewBase.setMinimumHeight(frameHeightRequest);
        cameraBridgeViewBase.setMaxFrameSize(frameWidthRequest, frameHeightRequest);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //create the frame and tmp images
        frame = new Mat(height, width, CvType.CV_8UC4, new Scalar(0,0,0));
        tmp1 = new Mat(height, width, CvType.CV_8UC4);
        tmp2 = new Mat(width, height, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

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
