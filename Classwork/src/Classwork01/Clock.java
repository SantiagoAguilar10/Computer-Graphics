package src.Classwork01;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class Clock {
    public static void main(String[] args) {
        BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);

        int radius = 100;
        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;

        // 360 degrees
        for (int angle = 0; angle < 360; angle++) {
            double radian = Math.toRadians(angle); // Change the current angle to radian
            int x = centerX + (int) (radius * Math.cos(radian)); // Calculate x coordinate
            int y = centerY + (int) (radius * Math.sin(radian)); // Calculate y coordinate
            img.setRGB(x, y, Color.white.getRGB()); // Set pixel color to white
        }

        int radius2 = 80;

        for (int angle = 0; angle < 360; angle++) {
            if (angle%30 == 0){
                double radian = Math.toRadians(angle); // Change the current angle to radian
                int x = centerX + (int) (radius2 * Math.cos(radian)); // Calculate x coordinate
                int y = centerY + (int) (radius2 * Math.sin(radian)); // Calculate y coordinate
                img.setRGB(x, y, Color.red.getRGB()); // Set pixel color to white
                
            }
        }

        int length_h = 50;
        int length_m = 70;

        int hour = 1;
        int minute = 30;

        int hourDegrees = Math.abs((270+(hour*30))%360);
        int minuteDegrees = Math.abs(270+(minute*6))%360;



        for (int i = 0; i < 360; i++) {
            if (i == hourDegrees){
                double radian = Math.toRadians(i); // Change the current angle to radian
                for (int j = 0; j < length_h; j++) {
                    int x = centerX + (int) (j * Math.cos(radian)); // Calculate x coordinate
                    int y = centerY + (int) (j * Math.sin(radian)); // Calculate y coordinate
                    img.setRGB(x, y, Color.white.getRGB()); // Set pixel color to white
                }
            }
        }

        for (int i = 0; i < 360; i++) {
            if (i == minuteDegrees){
                double radian = Math.toRadians(i); // Change the current angle to radian
                for (int j = 0; j < length_m; j++) {
                    int x = centerX + (int) (j * Math.cos(radian)); // Calculate x coordinate
                    int y = centerY + (int) (j * Math.sin(radian)); // Calculate y coordinate
                    img.setRGB(x, y, Color.white.getRGB()); // Set pixel color to white
                }
            }
        }

        

        File outputImage = new File("src/Classwork01/clock.jpg");
        try{
            ImageIO.write(img, "jpg", outputImage);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}