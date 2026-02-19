import java.util.List;

public class Decompressor {

    public static int[][] decompress(List<RLE> compressed, int height, int width) {
        // Generates a 2D array of grayscale values from a list of RLE objects, to reverse the compression process.
        int[][] matrix = new int[height][width];

        // Initializes x and y coordinates to keep track of the position in the matrix
        int y = 0;
        int x = 0;

        // Iterates through each RLE pair in the list.
        for (RLE pair : compressed) {
            // For each RLE pair, it fills the matrix with the specified value for the number of times indicated by the count.
            for (int i = 0; i < pair.count; i++) {
                // Sets the current position in the matrix to the value from the RLE pair.
                matrix[y][x] = pair.value;
                x++;
                // If the x coordinate reaches the width of the matrix, it resets x to 0 and increments y to move to the next row.
                if (x == width) {
                    x = 0;
                    y++; // Moves to the next row after filling the current row. This ensures that the matrix is filled correctly according to its dimensions.
                }
            }
        }

        return matrix;
    }
}