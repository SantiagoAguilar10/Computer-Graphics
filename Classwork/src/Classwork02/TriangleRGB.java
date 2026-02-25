package src.Classwork02;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class TriangleRGB {

    public static void main(String[] args) {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

        int width = img.getWidth();
        int height = img.getHeight();

        float Ax = 0;
        float Ay = 1000;

        float Bx = 1000;
        float By = 1000;

        float Cx = 500;
        float Cy = 0;

        // For cicle for every pixel in the image
        // If Condition to check if a point is inside the triangle
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                double delta1 = ((By - Cy)*(x - Cx) + (Cx - Bx)*(y - Cy)) / ((By - Cy)*(Ax - Cx) + (Cx - Bx)*(Ay - Cy)); //Formula for delta 1
                double delta2 = ((Cy - Ay)*(x - Cx) + (Ax - Cx)*(y - Cy)) / ((By - Cy)*(Ax - Cx) + (Cx - Bx)*(Ay - Cy)); //Formula for delta 2
                double delta3 = 1 - delta1 - delta2; //Formula for delta 3
                if ((delta1 >= 0 && delta2 >= 0 && delta3 >= 0) && delta1 + delta2 + delta3 == 1) { // If delta's sum is 1 and all delta's are greater than or equal to 0, then the point is inside the triangle
                    img.setRGB(x, y, new Color((int)(delta1 * 255), (int)(delta2 * 255), (int)(delta3 * 255)).getRGB());
                    // The line above sets the color of the pixel at (x, y) based on the delta values. The red component is determined by delta1, the green component by delta2, and the blue component by delta3. 
                    //Each delta value is multiplied by 255 to scale it to the range of RGB color values (0-255). The resulting color is then set for the pixel at (x, y) in the image.
                }
            }
        }

        // Output the image to a file
        File outputImage = new File("src/Classwork02/TriangleRGB.jpg");
        try{
            ImageIO.write(img, "jpg", outputImage);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
