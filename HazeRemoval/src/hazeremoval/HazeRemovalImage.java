package hazeremoval;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import org.opencv.core.Core;
import static org.opencv.core.CvType.CV_8UC1;

import static org.opencv.core.CvType.CV_8UC3;

import org.opencv.core.Mat;
import static org.opencv.core.Mat.zeros;

import org.opencv.core.Rect;

import static org.opencv.highgui.HighGui.WINDOW_AUTOSIZE;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.namedWindow;

import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import static org.opencv.imgproc.Imgproc.medianBlur;

/**
 *
 * @author crl
 */
public class HazeRemovalImage {
    private final CommonFun obj = new CommonFun();
    
    public void mainImageHazeRemoval() {

        Mat fog = imread("aero1.jpg");
        Mat darkChannel;
        Mat T;
        Mat fogfree;
        Mat beforeafter = zeros(fog.rows(), 2 * fog.cols(), CV_8UC3);
        Rect roil = new Rect(0, 0, fog.cols(), fog.rows());
        Rect roir = new Rect(fog.cols(), 0, fog.cols(), fog.rows());
        int Airlight;
        namedWindow("before and after", WINDOW_AUTOSIZE);

        darkChannel = obj.getMedianDarkChannel(fog, 5);
        Airlight = obj.estimateA(darkChannel);
        T = obj.estimateTransmission(darkChannel, Airlight);
        fogfree = obj.getDehazed(fog, T, Airlight);
//
        Mat mat1 = new Mat(beforeafter, roil);
        Mat mat2 = new Mat(beforeafter, roir);
        fog.copyTo(mat1);
        fogfree.copyTo(mat2);        
        imshow("before and after", beforeafter);        
        waitKey(1);
    }
}
