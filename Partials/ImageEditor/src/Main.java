package Partials.ImageEditor.src;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {

            BufferedImage img = ImageLoader.loadImage("Partials/ImageEditor/images/input.png");
            ImageEditor editor = new ImageEditor(img);

            DataMenu menu = new DataMenu(editor);
            menu.start();

        } catch (IOException e) {
            System.out.println("Error inicial: " + e.getMessage());
        }
    }
}