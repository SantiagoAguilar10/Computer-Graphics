import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageRGB {

    public static BufferedImage loadImage(String path) throws IOException { // Loads an image from the specified file path and returns it as a BufferedImage object. It throws an IOException if the file cannot be read.
        return ImageIO.read(new File(path)); // Reads the image file and returns it as a BufferedImage object
    }

    public static int[][] convertToGrayMatrix(BufferedImage image) { 
    // Converts a BufferedImage to a 2D array of grayscale values. 
    // Each pixel's RGB values are converted to a single grayscale value using a standard formula.

        int width = image.getWidth();
        int height = image.getHeight();

        int[][] matrix = new int[height][width]; // Initializes a 2D array to hold the grayscale values, with dimensions corresponding to the image's height and width

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = image.getRGB(x, y); // Retrieves the RGB value of the pixel at coordinates (x, y) as a single integer. 
                // The RGB value is typically represented as a 24-bit integer where the red, green, and blue components are packed together.

                // RGB components are extracted using bitwise operations:
                int r = (rgb >> 16) & 0xFF; // >> 16 to get the red component, then & 0xFF to isolate the last 8 bits
                int g = (rgb >> 8) & 0xFF; // >> 8 to get the green component, then & 0xFF to isolate the last 8 bits
                int b = rgb & 0xFF; // No displacement needed for blue, just & 0xFF to isolate the last 8 bits

                // Conversion to scales of gray
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                // 0.299, 0.587, and 0.114 are the standard "weights" for converting RGB to grayscale

                matrix[y][x] = gray;
            }
        }

        return matrix;
    }

    public static BufferedImage grayMatrixToImage(int[][] matrix) {

        int height = matrix.length;
        int width = matrix[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        // Type Byte Gray indicates the image will be a grayscale image where each pixel is represented by a single byte (0-255)


        // Iterates through each pixel in the grayscale matrix, converts the grayscale value to an RGB format, and sets the corresponding pixel in the BufferedImage.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // Gets the grayscale value from the matrix
                int gray = matrix[y][x];

                // Converts the grayscale value to RGB format
                int rgb = (gray << 16) | (gray << 8) | gray;
                // Sets the color in the Image
                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    public static void saveImage(BufferedImage image, String path) throws Exception {
        ImageIO.write(image, "png", new File(path));
    }
}
