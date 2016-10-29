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
        Preview.preview(src, "input");
    }
}
