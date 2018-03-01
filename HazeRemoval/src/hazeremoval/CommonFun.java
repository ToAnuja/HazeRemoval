/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hazeremoval;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import org.opencv.core.Core;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import org.opencv.core.Mat;
import static org.opencv.core.Mat.zeros;
import static org.opencv.imgproc.Imgproc.medianBlur;

/**
 *
 * @author anuja
 */
public class CommonFun {
    

    //median filtered dark channel
     Mat getMedianDarkChannel(Mat src, int patch) {

        Mat rgbmin = zeros(src.rows(), src.cols(), CV_8UC1);
        Mat MDCP = new Mat();
        double[] intensity;

        for (int m = 0; m < src.rows(); m++) {
            for (int n = 0; n < src.cols(); n++) {
                intensity = src.get(m, n);
                double minval = min(min(intensity[0], intensity[1]), intensity[2]);
                rgbmin.put(m, n, minval);
            }
        }
        medianBlur(rgbmin, MDCP, patch);
        return MDCP;
    }

    //estimate airlight by the brightest pixel in dark channel (proposed by He et al.)
     int estimateA(Mat DC) {
        double maxDC = 0;
        Core.MinMaxLocResult res = Core.minMaxLoc(DC);
        maxDC = res.maxVal;
        return (int) maxDC;
    }

    //estimate transmission map
     Mat estimateTransmission(Mat DCP, int ac) {
        double w = 0.75;
        Mat transmission = zeros(DCP.rows(), DCP.cols(), CV_8UC1);
        double[] intensity;

        for (int m = 0; m < DCP.rows(); m++) {
            for (int n = 0; n < DCP.cols(); n++) {
                intensity = DCP.get(m, n);
                double value = (1 - w * intensity[0] / ac) * 255;
                transmission.put(m, n, value);
            }
        }
        return transmission;
    }

    //dehazing foggy image
     Mat getDehazed(Mat source, Mat t, int al) {
        double tmin = 0.1;
        double tmax;

        double[] inttran;
        double[] intsrc;
        Mat dehazed = zeros(source.rows(), source.cols(), CV_8UC3);

        for (int i = 0; i < source.rows(); i++) {
            for (int j = 0; j < source.cols(); j++) {
                inttran = t.get(i, j);
                intsrc = source.get(i, j);
                tmax = (inttran[0] / 255) < tmin ? tmin : (inttran[0] / 255);
                double[] ch = new double[3];
                for (int k = 0; k < 3; k++) {
                    ch[k] = abs((intsrc[k] - al) / tmax + al) > 255 ? 255 : abs((intsrc[k] - al) / tmax + al);
                }
                dehazed.put(i, j, ch);
            }
        }
        return dehazed;
    }
}
