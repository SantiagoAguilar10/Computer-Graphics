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

                currentImage.setRGB(x, y, p); // Set the new pixel value in the image
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

        // Coordinates Validation System
        // The cropped area should be withing the bounds of the original image.
        if (xmin < 0 || ymin < 0 || xmin + width > currentImage.getWidth() || ymin + height > currentImage.getHeight()) {
            throw new IllegalArgumentException("Crop coordinates are out of bounds");
        }

        // A new BufferedImage is created to store the cropped area.
        // The type of the new image is the same as the original
        BufferedImage newImage = new BufferedImage(width, height, currentImage.getType());


        // The method uses nested loops to iterate througuh the pixels of the selected area in the original image and copy them to the new image.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = currentImage.getRGB(xmin + x, ymin + y);
                newImage.setRGB(x, y, color);
            }   
        }

        currentImage = newImage; // The current image is updated to the cropped version.
    }

    public void rotate(int x1, int y1, int x2, int y2, int angle) {

        // Validation of parameters
        if (angle != 90 && angle != 180 && angle != 270) {
            throw new IllegalArgumentException("Angle must be 90, 180 or 270.");
        }

        // Ensure coordinates are properly ordered (in case user enters them reversed)
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x2 - x1);
        int height = Math.abs(y2 - y1);

        // Validate that the selected area is within the image bounds
        if (x < 0 || y < 0 ||
            x + width > currentImage.getWidth() ||
            y + height > currentImage.getHeight()) {
            throw new IllegalArgumentException("Selection out of bounds.");
        }

        // Calculate the center of the selected area as an anchor point for rotation
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        // Create a "temporary" image to store the selected area before rotation
        BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Copying the selected area to the temporary image
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                temp.setRGB(i, j, currentImage.getRGB(x + i, y + j));
            }
        }

        // Clearing the original area by filling it with black
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                currentImage.setRGB(x + i, y + j, 0x000000);
            }
        }

        // Apply rotation to each pixel in the temporary image 
        // and map it back to the original image based on the calculated new coordinates
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // Coordinates of the original pixel in the current image
                int originalX = x + i;
                int originalY = y + j;

                // Coordinates relative to the anchor point (center of the selected area)
                double dx = originalX - cx;
                double dy = originalY - cy;

                double rotatedX = 0;
                double rotatedY = 0;

                // Apply rotation formulas based on the specified angle
                switch (angle) {

                    case 90:
                        // (x', y') = (-y, x)
                        // The new coordinates are calculated by swapping the relative x and y values
                        // and reversing the y value for a 90 degrees rotation
                        rotatedX = -dy;
                        rotatedY = dx;
                        break;

                    case 180:
                        // (x', y') = (-x, -y)
                        // Both relative x and y values are inverted for a 180 degrees rotation
                        rotatedX = -dx;
                        rotatedY = -dy;
                        break;

                    case 270:
                        // (x', y') = (y, -x)
                        // The new coordinates are calculated by swapping the relative x and y values
                        // and reversing the x value for a 270 degrees rotation
                        rotatedX = dy;
                        rotatedY = -dx;
                        break;
                }

                // Calculate the new coordinates in the original image by adding the rotated relative coordinates to the anchor point
                int newX = (int) Math.round(cx + rotatedX);
                int newY = (int) Math.round(cy + rotatedY);

                // Drawing the rotated pixels back to the original image
                // Only if the new coordinates are within the bounds of the image
                // IndexOutOfBoundsException is prevented by this validation
                if (newX >= 0 && newX < currentImage.getWidth() &&
                    newY >= 0 && newY < currentImage.getHeight()) {

                    currentImage.setRGB(newX, newY, temp.getRGB(i, j));
                }
            }
        }
    }


    // Save current image to specified path
    public void save(String name) throws IOException {
        String path = "Partials/ImageEditor/outputs/" + name ;
        ImageLoader.saveImage(currentImage, path);
    }
}