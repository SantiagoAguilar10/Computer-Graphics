package Partials.ImageEditor.src;

import java.util.Scanner;
import java.io.IOException;

public class DataMenu {

    // Attributes
    private Scanner scanner;
    private ImageEditor editor;

    // Constructor
    // Initializes DataMenu with an ImageEditor instance
    // Scanner initialized for user input
    public DataMenu(ImageEditor editor, Scanner scanner) {
        this.editor = editor;
        this.scanner = scanner;
    }

    public void start() {

        // Initialize the option variable with an unvalid value to enter the loop
        int option = -1;

        // While loop to keep the menu running
        while (option != 5) {

            // Display the manu options to the user
            printMenu();

            try {
                option = Integer.parseInt(scanner.nextLine()); // Get user input and convert it to integer

                // Switch statement to handle user's choices and call the respective methods in ImageEditor
                switch (option) {
                    case 1:
                        // As invertColors does not require additional user input, it is called directly here.
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
                        System.out.println("Thanks for using my Image Editor!");
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

    // Method to display the menu options to the user
    private void printMenu() {
        System.out.println("\nIMAGE EDITOR");
        System.out.println("\nThe Image's current dimensions are: " + editor.getCurrentImage().getWidth() + "x" + editor.getCurrentImage().getHeight()+ "\n");
        System.out.println("1. Invert Colors");
        System.out.println("2. Crop");
        System.out.println("3. Rotate");
        System.out.println("4. Save image");
        System.out.println("5. Exit");
        System.out.print("Select an option: ");
    }

    // Particular methods to handle the specific user choices in the menu.

    // As invertColors does not require additional user input, it is called directly in the switch statement at the start method.

    // Handle crop.
    // The user should input 2(x, y) coordinates as the opposite corners of a quadrilateral.
    // The method will call the crop method in ImageEditor with the user inputs as parameters.
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

    // Handle Rotate.
    // The user should input 2(x, y) coordinates as the opposite corners of a quadrilateral.
    // The method will call the rotate method in ImageEditor with the user inputs as parameters.
    private void handleRotate() {

        System.out.print("Enter x1: ");
        int x1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y1: ");
        int y1 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter x2: ");
        int x2 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter y2: ");
        int y2 = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter rotation angle: (90, 180, 270): ");
        int angle = Integer.parseInt(scanner.nextLine());

        editor.rotate(x1, y1, x2, y2, angle);

        System.out.println("Rotation process finished successfully.");
    }


    // Handle Save.
    // The user should input the name of the output file
    // Example: "I_need_more_scholarship_money.png"
    private void handleSave() throws IOException {

        System.out.print("Enter the name of the output file (example: 'my_image.png')\nMake sure to include '.png':");
        String path = scanner.nextLine();

        // The method will call the save method in ImageEditor with the user input as parameter.
        editor.save(path);

        // Confirmation message to the user after saving the image.
        System.out.println("Image saved successfully at: Outputs/" + path);
    }
}