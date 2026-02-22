package Partials.ImageEditor.src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageLoader {

    // Loads an image from the specified file path and returns it as a BufferedImage object.
    // It throws an IOException if the file cannot be read.
    // Reads the image file and returns it as a BufferedImage object.
    public static BufferedImage loadImage(String path) throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            throw new IOException("The file does not exist: " + path);
        }

        BufferedImage image = ImageIO.read(file);

        return image;
    }

    // Saves the image to the specified path in PNG format
    // Throws an Exception if the image cannot be saved
    public static void saveImage(BufferedImage image, String path) throws IOException {
        File file = new File(path);
        ImageIO.write(image, "png", file);
    }


}
