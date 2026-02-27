package Partials.ImageEditor.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {

            // Ask user for image file name
            System.out.print("Enter image file name (inside images folder) include '.png': ");
            String fileName = scanner.nextLine();

            // Build full path dynamically
            String path = "Partials/ImageEditor/images/" + fileName;

            // Load the image using ImageLoader
            BufferedImage img = ImageLoader.loadImage(path);

            // Create an instance of ImageEditor using the loaded image
            ImageEditor editor = new ImageEditor(img);

            // Create an instance of DataMenu using the ImageEditor instance and start the menu
            DataMenu menu = new DataMenu(editor, scanner);
            menu.start();

        } catch (IOException e) {
            System.out.println("Initial Error: " + e.getMessage());
        }

        scanner.close();
    }
}