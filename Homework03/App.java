import java.awt.image.BufferedImage;
import java.util.List;

public class App {

    public static void main(String[] args) {

        try {

            BufferedImage image =
                ImageRGB.loadImage("Homework03/Images/Input1.png");

            int[][] matrix = ImageRGB.convertToGrayMatrix(image);

            List<RLE> compressed =
                Compressor.compress(matrix);

            int[][] decompressed =
                Decompressor.decompress(
                    compressed,
                    matrix.length,
                    matrix[0].length
                );

            BufferedImage reconstructed =
                ImageRGB.grayMatrixToImage(decompressed);

            ImageRGB.saveImage(
                reconstructed,
                "Homework03/Outputs/Output1.png"
            );

            System.out.println("Proceso completo terminado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}