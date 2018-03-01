package hazeremoval;


import static org.opencv.core.CvType.CV_8UC3;

import org.opencv.core.Mat;
import static org.opencv.core.Mat.zeros;

import org.opencv.core.Rect;
import org.opencv.core.Size;

import static org.opencv.highgui.HighGui.WINDOW_AUTOSIZE;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.namedWindow;

import static org.opencv.highgui.HighGui.waitKey;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import static org.opencv.videoio.VideoWriter.fourcc;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;
import static org.opencv.videoio.Videoio.CAP_PROP_POS_AVI_RATIO;

/**
 *
 * @author crl
 */
public class HazeRemovalVideo extends Thread {

    private final CommonFun obj = new CommonFun();
    
    public void run(){
        mainHazeRemovalVideo();
    }
    private void mainHazeRemovalVideo() {
        //for video defogging
        VideoCapture vid = new VideoCapture("Sample_Ship.mp4");
        if (!vid.isOpened()) {
            return;
        }

        double rate = vid.get(CAP_PROP_FPS);
        int delay = (int) (1000 / rate);
        boolean stop = false;

        int frame_width = (int) vid.get(CAP_PROP_FRAME_WIDTH);
        int frame_height = (int) vid.get(CAP_PROP_FRAME_HEIGHT);
        VideoWriter video = new VideoWriter("out.avi", fourcc('M', 'J', 'P', 'G'), 10, new Size(frame_width, frame_height), true);

        Mat frame = new Mat();
        Mat darkChannel;
        Mat T;
        Mat fogfree;
        double alpha = 0.05;    //alpha smoothing
        int Airlightp;          //airlight value of previous frame
        int Airlight;           //airlight value of current frame
        int FrameCount = 0;     //frame number
        int ad = 0;                 //temp airlight value
        String title = "before and after";
        namedWindow(title, WINDOW_AUTOSIZE);

        while (!stop) {
            if (vid.isOpened()) {
                vid.read(frame);
                if (frame.empty()) {
                    break;
                }
                FrameCount++;
                if (vid.get(CAP_PROP_POS_AVI_RATIO) == 1) {
                    break;
                }

                //create mat for showing the frame before and after processing
                Mat beforeafter = zeros(frame.rows(), 2 * frame.cols(), CV_8UC3);
                Rect roil = new Rect(0, 0, frame.cols(), frame.rows());
                Rect roir = new Rect(frame.cols(), 0, frame.cols(), frame.rows());

                //first frame, without airlight smoothing
                if (FrameCount == 1) {
                    darkChannel = obj.getMedianDarkChannel(frame, 5);
                    Airlight = obj.estimateA(darkChannel);
                    T = obj.estimateTransmission(darkChannel, Airlight);
                    ad = Airlight;
                    fogfree = obj.getDehazed(frame, T, Airlight);
                } //other frames, with airlight smoothing
                else {

                    Airlightp = ad;
                    darkChannel = obj.getMedianDarkChannel(frame, 5);
                    Airlight = obj.estimateA(darkChannel);
                    T = obj.estimateTransmission(darkChannel, Airlight);
                    ad = (int) (alpha * (double) Airlight + (1 - alpha) * (double) Airlightp);//airlight smoothing
                    fogfree = obj.getDehazed(frame, T, ad);
                }

                Mat mat1 = new Mat(beforeafter, roil);
                Mat mat2 = new Mat(beforeafter, roir);
                frame.copyTo(mat1);
                fogfree.copyTo(mat2);
                video.write(fogfree);
                
                imshow(title, beforeafter);

                if (waitKey(delay) >= 0) {
                    stop = true;
                }
            }

        }
        Thread.currentThread().interrupt();
    }


}
