package lv.timeklis.opencv.rain;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


public class Rain {

    public static void main(String[] args) {

        System.out.println(args[0]);
        System.out.println("OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Rain rain = new Rain(args[0]);
    }

    public Rain(String img) {

        Mat src = Highgui.imread(img, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

//        Mat blured = this.blur(src);

        Mat rotated = this.rotate(src);
        Mat thresholded = this.threshold(rotated);


//        this.findHorizontalLines(color);
//
//        Preview.preview(color, "input");

        // convert back to color image to find lines, numbers and letters
        Mat color = new Mat(src.size(), CvType.CV_8UC3);
        Imgproc.cvtColor(thresholded, color, Imgproc.COLOR_GRAY2BGR);

        Mat lines = this.getLines(thresholded);
        this.drawLines(color, lines);
        //this.fillBlanks(thresholded, color, 60, 8);
        this.fillBlanks(thresholded, color, 200, 2);

        Preview.preview(color, "input");
    }

    public Mat blur(Mat src) {

        Mat dst = new Mat(src.size(), src.type());

        Imgproc.GaussianBlur(src, dst, new Size(new double[]{7, 7}), 1);
        return dst;
    }

    public Mat rotate(Mat src) {

        double angle = -1.1;

        Mat dst = new Mat(src.size(), src.type());

        Point center = new Point((int)src.size().width/2,(int) src.size().width/2);
        Mat r = Imgproc.getRotationMatrix2D(center, angle, 1);

        Imgproc.warpAffine(src, dst, r, src.size());
        return dst;
    }

    public Mat threshold(Mat src) {

        Mat dst = new Mat(src.size(), src.type());

        Imgproc.threshold(src, dst, 200, 255, Imgproc.THRESH_BINARY);

        //Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 20d);

        return dst;
    }

    public void findHorizontalLines(Mat src) {

        int dx = 10;
        int dy = 1;

        int maxw = (int) src.size().width-1;
        int maxh = (int) src.size().height-1;

        for(int x = 0; x<=maxw;x++) {
            for(int y = 0;y<=maxh; y++) {
                double[] data = src.get(y, x);
                if(this.isBlack(data)) {

                    int range_min_x = x-dx>=0?x-dx:0;
                    int range_max_x = x+dx<maxw?x+dx:maxw;

                    int range_min_y = y;
                    int range_max_y = y;

                    for(int range_x = range_min_x; range_x <= range_max_x; range_x++) {
                        for(int range_y = range_min_y; range_y <= range_max_y; range_y++) {

                        }
                    }


                }


                int c = 1;
            }
        }


    }

    private boolean isBlack(double[] data) {

        double b = data[0];
        double g = data[1];
        double r = data[2];
        return b == 0 && g == 0 && r == 0;
    }

    private boolean isBlack(Mat src, int x, int y) {

        double[] data = src.get(y, x);
        return (int) data[0] == 0;
    }

    public Mat getLines(Mat src) {

        Mat lines = new Mat();
        Mat invertedSrc = new Mat(src.size(), src.type());
        Core.bitwise_not(src, invertedSrc);

        Imgproc.HoughLinesP(invertedSrc, lines, 1, Math.PI/185, 90, 100, 5);




//        Imgproc.HoughLines(src, lines, 1, Math.PI/180, 100);

        // http://stackoverflow.com/questions/19651756/convert-vec4i-to-java-opencv
//        for (int x = 0; x < lines.cols(); x++) {
//
//            double[] vec = lines.get(0, x);
//            double[] val = new double[4];
//
//            val[0] = 0;
//            val[1] = ((float) vec[1] - vec[3]) / (vec[0] - vec[2]) * -vec[0] + vec[1];
//            val[2] = src.cols();
//            val[3] = ((float) vec[1] - vec[3]) / (vec[0] - vec[2]) * (src.cols() - vec[2]) + vec[3];
//            lines.put(0, x, val);
//        }

        return lines;
    }

    public void drawLines(Mat dst, Mat lines) {

        Scalar color = new Scalar(new double[] {0,0,255});

        for (int x = 0; x < lines.cols(); x++) {
            double[] line = lines.get(0, x);
            Core.line(dst, new Point(line[0], line[1]), new Point(line[2], line[3]), color);
//            break;
        }

    }

    public void fillBlanks(Mat src, Mat dst, int dx, int dy) {

        int maxw = (int) src.size().width-1;
        int maxh = (int) src.size().height-1;

        for(int y = 0;y<=maxh; y++) {
            for(int x = 0; x<=maxw;x++) {


                int maxSqX = x+dx<=maxw?x+dx:maxw;
                int maxSqY = y+dy<=maxh?y+dy:maxh;

                boolean fill = true;

                for(int sqX = x; sqX<=maxSqX; sqX++) {
                    for(int sqY = y; sqY<=maxSqY; sqY++) {
                        if(this.isBlack(src, sqX, sqY)) {
                            fill = false;
                            break;
                        }
                    }
                    if(!fill) break;
                }

                if(fill) {
                    for(int sqX = x; sqX<=maxSqX; sqX++) {
                        for(int sqY = y; sqY<=maxSqY; sqY++) {
                            double[] data = dst.get(sqY, sqX);
                            data[0]=100;
                            dst.put(sqY, sqX, data);
                        }
                        if(!fill) break;
                    }
                    // continue going to the right
                    for(x = maxSqX; x<=maxw;x++) {
                        boolean fillAnother = true;
                        for(int sqY = y; sqY<=maxSqY; sqY++) {
                            if(this.isBlack(src, x, sqY)) {
                                fillAnother = false;
                                break;
                            }
                        }
                        if(fillAnother) {
                            for(int sqY = y; sqY<=maxSqY; sqY++) {
                                double[] data = dst.get(sqY, x);
                                data[0]=100;
                                dst.put(sqY, x, data);
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }

    }
}
