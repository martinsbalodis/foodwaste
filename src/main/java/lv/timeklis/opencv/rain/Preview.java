package lv.timeklis.opencv.rain;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Preview {

    public static void preview(Mat img, String title) {

        try {
            MatOfByte matOfByte = new MatOfByte();

            Highgui.imencode(".jpg", img, matOfByte);

            byte[] byteArray = matOfByte.toArray();
            BufferedImage bufImage = null;

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);

            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(new Dimension(img.width(), img.height()));
            frame.setVisible(true);
            frame.add(new JLabel(new ImageIcon(bufImage)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
