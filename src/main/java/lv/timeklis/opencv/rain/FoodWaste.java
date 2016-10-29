package lv.timeklis.opencv.rain;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FoodWaste {

    private static double[] green = {0, 255, 0};

    public static void main(String[] args) {

        System.out.println(args[0]);
        System.out.println("OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        FoodWaste foodWaste = new FoodWaste(args[0]);
    }

    public FoodWaste(String img) {

        Mat src = Highgui.imread(img, Highgui.CV_LOAD_IMAGE_COLOR);
        Preview.preview(src, "input");

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

        for (int i = 0; i < circles.cols(); i++) {
            double vCircle[] = circles.get(0, i);


            Mat copy = new Mat(src.size(), CvType.CV_8UC1);
            src.copyTo(copy);
//            Mat copy = src.clone();

            Point center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);
            // draw the circle center
//            Core.circle(copy, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            // draw the circle outline
//            Core.circle(copy, center, radius, new Scalar(0, 0, 255), 1, 8, 0);


            // calculate food pixels
            int width = (int) src.size().width;
            int height = (int) src.size().height;

            int totalPointCount = 0;
            int foodPointCount = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double dx = x - center.x;
                    double dy = y - center.y;
                    double distanceSquared = Math.sqrt(dx * dx + dy * dy);

                    if (distanceSquared <= radius) {
                        totalPointCount++;
                        double[] point = src.get(y, x);
                        if (isFoodColor(point)) {
                            foodPointCount++;
                            // color food
                            copy.put(y, x, green);
                        }
                    }
                }
            }


            // circle the dish
            Core.circle(copy, center, radius, new Scalar(0, 0, 255), 2, 8, 0);


            // draw text of food %
            int foodPart = (int) Math.round((float) foodPointCount / (float) totalPointCount * 100);
            String text = Integer.toString(foodPart) + " %";
            Core.putText(copy, text, new Point(10, 35), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 255));

            Preview.preview(copy, "circled");
        }


    }

    private boolean isFoodColor(double[] data) {

        double b = data[0];
        double g = data[1];
        double r = data[2];

        double br = Math.abs(b - r);
        double bg = Math.abs(b - g);
        double gr = Math.abs(g - r);

        return br > 60 || bg > 60 || gr > 60;
    }
}
