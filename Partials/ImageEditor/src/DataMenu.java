package Partials.ImageEditor.src;

import java.util.Scanner;
import java.io.IOException;

public class DataMenu {

    private Scanner scanner;
    private ImageEditor editor;

    public DataMenu(ImageEditor editor) {
        this.editor = editor;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        int option = -1;

        while (option != 5) {

            printMenu();

            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        editor.invertColors();
                        System.out.println("Inverted colors process finished successfully.");
                        break;

                    case 2:
                        handleCrop();
                        break;

                    case 3:
                        handleRotate();
                        break;

                    case 4:
                        handleSave();
                        break;

                    case 5:
                        System.out.println("Check your image at the 'Outputs' Folder");
                        break;

                    default:
                        System.out.println("Invalid Option.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Input must be a number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\nIMAGE EDITOR");
        System.out.println("1. Invert Colors");
        System.out.println("2. Crop");
        System.out.println("3. Rotate");
        System.out.println("4. Save image");
        System.out.println("5. Exit");
        System.out.print("Select an option: ");
    }

    private void handleCrop() {

        System.out.print("Enter x1: ");
        int x1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y1: ");
        int y1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter x2: ");
        int x2 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y2: ");
        int y2 = Integer.parseInt(scanner.nextLine());

        editor.crop(x1, y1, x2, y2);

        System.out.println("Crop process finished successfully.");
    }

    private void handleRotate() {

        System.out.print("Enter x1: ");
        int x1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y1: ");
        int y1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter x2: ");
        int x2 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y2: ");
        int y2 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter rotation angle: (90, 180, 270, 360): ");
        int angle = Integer.parseInt(scanner.nextLine());

        //editor.rotate(x1, y1, x2, y2, angle);

        System.out.println("Rotation process finished successfully.");
    }

    private void handleSave() throws IOException {

        System.out.print("Enter the name of the output file ('my_image.png'):");
        String path = scanner.nextLine();

        editor.save(path);

        System.out.println("Image saved successfully at: Outputs/" + path);
    }
}