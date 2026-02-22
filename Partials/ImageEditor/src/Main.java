package Partials.ImageEditor.src;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        System.out.println(System.getProperty("user.dir"));

        try {

            BufferedImage img = ImageLoader.loadImage("Partials/ImageEditor/images/input.png");

            ImageEditor editor = new ImageEditor(img);

            editor.invertColors();

            editor.save("Partials/ImageEditor/images/output.png");

            System.out.println("Imagen procesada correctamente.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}