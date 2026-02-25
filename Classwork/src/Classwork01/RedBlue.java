package src.Classwork01;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class RedBlue {
    public static void main(String[] args) {
        BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        // Get image dimensions
        int width = img.getWidth();
        int height = img.getHeight();

        // Drawing with nested loops
        for (int y = 0; y < height; y++) { // rows
            for (int x = 0; x < width; x++) { // columns
                // Determine which triangle the pixel belongs to
                if (y > (height * x) / width) { // If below the diagonal
                    img.setRGB(x, y, Color.red.getRGB());
                } else {
                    img.setRGB(x, y, Color.blue.getRGB());
                }
            }
        }    
        
        File outputImage = new File("src/Classwork01/redblue.jpg");
        try{
            ImageIO.write(img, "jpg", outputImage);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}