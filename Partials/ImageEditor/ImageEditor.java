package Partials.ImageEditor;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageEditor {

    private BufferedImage currentImage;

    // Constructor
    public ImageEditor(BufferedImage image) {
        this.currentImage = image;
    }

    // Getter
    public BufferedImage getCurrentImage() {
        return currentImage;
    }

    // Invert Colors
    public void invertColors() {

        // Get width & height
        int width = currentImage.getWidth();
        int height = currentImage.getHeight();

        // For nested loop to modify each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // Get RGB values from each pixel
                int p = currentImage.getRGB(x, y);
                
                // RGB values are stored in a single integer
                // To extract them individually, bitwise operations are required
                // 255 minus the original value results in the inverted color for that channel
                int a = (p >> 24) & 0xff;           // Transparency (alpha). No need to invert alpha.
                int r = 255 - ((p >> 16) & 0xff);   // Red channel
                int g = 255 - ((p >> 8) & 0xff);    // Green channel
                int b = 255 - (p & 0xff);           // Blue channel

                // Combine the inverted RGB values back into a single integer
                p = (a << 24) | (r << 16) | (g << 8) | b;

                currentImage.setRGB(x, y, p);
            }
        }
    }




    // Save current image to specified path
    public void save(String path) throws IOException {
        ImageLoader.saveImage(currentImage, path);
    }
}