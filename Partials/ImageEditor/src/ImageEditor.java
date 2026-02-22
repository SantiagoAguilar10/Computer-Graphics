package Partials.ImageEditor.src;

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
    

    // Crop the image using  2(x,y) coordinates as the opposite corners of a rectangle
    public void crop(int x1, int y1, int x2, int y2) {

        // The method calculates the minimum x and y coordinates to determine the corners of the rectangle.
        int xmin = Math.min(x1, x2);
        int ymin = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);

        // Validación básica de límites
        if (xmin < 0 || ymin < 0 || xmin + width > currentImage.getWidth() || ymin + height > currentImage.getHeight()) {
            throw new IllegalArgumentException("Crop coordinates are out of bounds");
        }

        BufferedImage newImage = new BufferedImage(width, height, currentImage.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = currentImage.getRGB(xmin + x, ymin + y);
                newImage.setRGB(x, y, color);
            }   
        }

        currentImage = newImage;
    }


    // Save current image to specified path
    public void save(String path) throws IOException {
        ImageLoader.saveImage(currentImage, path);
    }
}