package src.Classwork01;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class Landscape {
    
    public static void main(String[] args) {
        

        BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        //Paint BG white
        for (int x = 0; x < 500; x++){ 
            for (int y = 0; y < 500; y++){
                img.setRGB(x, y, Color.white.getRGB());
            }
        }

        //Draw Sun
        int centerx = 100;
        int centery = 100;

        int radius = 50;

        for(int angle = 0; angle < 360; angle++){
            double radian = Math.toRadians(angle);
            int x = centerx + (int)(radius * Math.cos(radian));
            int y = centery + (int)(radius * Math.sin(radian));
            img.setRGB(x, y, Color.yellow.getRGB());
        }

        // Color the inside of the sun
        for (int x = centerx - radius; x <= centerx + radius; x++) {
            for (int y = centery - radius; y <= centery + radius; y++) {
                int dx = x - centerx;
                int dy = y - centery;

                if (dx * dx + dy * dy <= radius * radius) {
                    img.setRGB(x, y, Color.yellow.getRGB());
                }
            }
        }

        // Sun Rays
        // Same as clock hands
        int rayLength = 80;
        for (int angle = 0; angle < 360; angle += 45) {
            double radian = Math.toRadians(angle);
            for (int r = radius; r < rayLength; r++) {
                int x = centerx + (int)(r * Math.cos(radian));
                int y = centery + (int)(r * Math.sin(radian));
                img.setRGB(x, y, Color.orange.getRGB());
            }
        }

        // Draw Waves
        // Wave settings
        int WaveWidth = 500; 
        int WaveHeight = 500;
        int WaveCenterY = 400;
        int fluctuation = 20;

        for(int i = 0; i < WaveWidth; i++){ 
            double angle = (double)i / WaveWidth * 2 * Math.PI * 5.5;  // Amount of waves
            int y = (int)(WaveCenterY - fluctuation * Math.sin(angle)); // Sine wave calculation
            img.setRGB(i, y, Color.green.getRGB()); 

            for(int j = y; j < WaveHeight; j++){    
                img.setRGB(i, j, Color.green.getRGB());
            }
        }


        File outputImage = new File("src/Classwork01/Landscape.jpg");
        try{
            ImageIO.write(img, "jpg", outputImage);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
