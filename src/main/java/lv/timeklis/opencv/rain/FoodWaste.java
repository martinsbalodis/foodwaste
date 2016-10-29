package lv.timeklis.opencv.rain;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FoodWaste {

    public static void main(String[] args) {

        System.out.println(args[0]);
        System.out.println("OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        FoodWaste foodWaste = new FoodWaste(args[0]);
    }

    public FoodWaste(String img) {

        Mat src = Highgui.imread(img, Highgui.CV_LOAD_IMAGE_COLOR);
//        Preview.preview(src, "input");

        Mat gray = new Mat(src.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGB2GRAY);

        findCircles(src, gray);

//        Preview.preview(gray, "gray");

    }

    private void findCircles(Mat src, Mat gray) {

        Mat circles = new Mat();

        int iCannyUpperThreshold = 100;
        int iMinRadius = 50;
        int iMaxRadius = 1000;
        int iAccumulator = 100;

        Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT,
                1.5, 70, iCannyUpperThreshold, iAccumulator,
                iMinRadius, iMaxRadius);

        for (int x = 0; x < circles.cols(); x++) {
            double vCircle[] = circles.get(0, x);

            Mat copy = src.clone();

            Point center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);
            // draw the circle center
            Core.circle(copy, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            // draw the circle outline
            Core.circle(copy, center, radius, new Scalar(0, 0, 255), 3, 8, 0);

            Preview.preview(copy, "circled");
        }


    }
}
