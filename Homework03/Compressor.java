import java.util.ArrayList; // ArrayList is a resizable array implementation of the List interface, used to store the compressed RLE data.
import java.util.List;

public class Compressor {

    public static List<RLE> compress(int[][] matrix) {
        // This method takes a 2D array of grayscale values (the image matrix) and compresses it using Run-Length Encoding (RLE).
        // It initializes an empty list of RLE objects to store the compressed data.

        List<RLE> compressed = new ArrayList<>(); // Initializes an empty list to hold the compressed RLE data. 
        // Each RLE object will represent a sequence of pixels with the same value.

        int height = matrix.length;
        int width = matrix[0].length;

        int currentValue = matrix[0][0];
        int count = 0;

        // For cicle through each pixel in the matrix, comparing the current pixel value with the previous one.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int value = matrix[y][x];

                if (value == currentValue) {// If the current pixel value is the same as the previous one, it increments the count of consecutive pixels.
                    count++;
                } else { // If it's not, it creates a new RLE object with the current value and count, adds it to the compressed list, and resets the current value and count for the new pixel value.
                    compressed.add(new RLE(currentValue, count));
                    currentValue = value;
                    count = 1;
                }
            }
        }

        // Save the last sequence of pixels after the loop ends, since it won't be added inside the loop.
        compressed.add(new RLE(currentValue, count));

        return compressed;
    }
}