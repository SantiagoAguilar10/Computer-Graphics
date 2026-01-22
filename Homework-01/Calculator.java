import java.util.Scanner;

public class Calculator {

    private Scanner scanner; // Scanner for user inputs

    public Calculator() {
        scanner = new Scanner(System.in); // Initialize scanner
    }

    // Getters and Setters for Scanner

    public Scanner getScanner() {
        return scanner;
    }
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        int option;
        // Do-while loop to keep the program running until the user decides to exit
        do {
            showMenu(); // Display the menu
            option = getIntInput("Select an option: "); 

            // Handling user selection of a geometric shape
            switch (option) {
                case 1:
                    square();
                    break;
                case 2:
                    rectangle();
                    break;
                case 3:
                    triangle();
                    break;
                case 4:
                    circle();
                    break;
                case 5:
                    pentagon();
                    break;
                case 6:
                    pentagram();
                    break;
                case 7:
                    semiCircle();
                    break;
                case 0:
                    System.out.println("Thanks for using my calculator :)");
                    break;
                default:
                    System.out.println("Invalid option :( Try again.");
            }

            if (option != 0) {
                pause(); // Pause to read results
            }

        } while (option != 0);
        // Close the scanner and therefore the program
        scanner.close();
    }

    private void showMenu() {
        System.out.println("\n Geometric Shapes Calculator");
        System.out.println("1. Square");
        System.out.println("2. Rectangle");
        System.out.println("3. Triangle");
        System.out.println("4. Circle");
        System.out.println("5. Regular Pentagon");
        System.out.println("6. Pentagram");
        System.out.println("7. Semi-Circle");
        System.out.println("0. Exit");
    }


    // Geometric Shape Methods

    private void square() {
    System.out.println("\nSquare");

    double side = getDoubleInput("Enter side length: ");

    double area = side * side;
    double perimeter = 4 * side;

    System.out.println("Area: " + area);
    System.out.println("Perimeter: " + perimeter);

    }


    private void rectangle() {
    System.out.println("\nRectangle");

    double width = getDoubleInput("Enter width: ");
    double height = getDoubleInput("Enter height: ");

    double area = width * height;
    double perimeter = 2 * (width + height);

    System.out.println("Area: " + area);
    System.out.println("Perimeter: " + perimeter);

    }


    private void triangle() {
    System.out.println("\nTriangle");
    System.out.println("1. Equilateral");
    System.out.println("2. Isosceles");
    System.out.println("3. Scalene");

    int triangleType = getIntInput("Select a triangle type: ");

    switch (triangleType) {
        case 1:
            equilateralTriangle();
            break;
        case 2:
            isoscelesTriangle();
            break;
        case 3:
            scaleneTriangle();
            break;
        default:
            System.out.println("Invalid option.");
        }
    }

    private void equilateralTriangle() {
        double side = getDoubleInput("Enter side length: ");

        // In this case, there's no invalid triangle since all sides are equal
        double area = (Math.sqrt(3) / 4) * side * side;
        double perimeter = 3 * side;
        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);

    }

    private void isoscelesTriangle() {
        double equalSide = getDoubleInput("Enter length of equal sides: ");
        double base = getDoubleInput("Enter base length: ");

        // Error handling for invalid triangle
        if (base >= 2 * equalSide) {
            System.out.println("The lengths provided do not form a valid isosceles triangle.");
            return;
        }

        double height = Math.sqrt(equalSide * equalSide - (base * base) / 4);
        double area = (base * height) / 2;
        double perimeter = 2 * equalSide + base;
        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);

    }

    private void scaleneTriangle() {
        double sideA = getDoubleInput("Enter length of side A: ");
        double sideB = getDoubleInput("Enter length of side B: ");
        double sideC = getDoubleInput("Enter length of side C: ");

        // Error handling for invalid triangle
        if (sideA + sideB <= sideC || sideA + sideC <= sideB || sideB + sideC <= sideA) {
            System.out.println("The lengths provided do not form a valid scalene triangle.");
            return;
        }

        double s = (sideA + sideB + sideC) / 2; // semi-perimeter
        double area = Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC)); // Heron's formula
        double perimeter = sideA + sideB + sideC;

        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);

    }


    private void circle() {
    System.out.println("\nCircle");

    double radius = getDoubleInput("Enter radius: ");

    double area = Math.PI * radius * radius;
    double perimeter = 2 * Math.PI * radius;

    System.out.println("Area: " + area);
    System.out.println("Perimeter: " + perimeter);

    }

    private void pentagon() {
    System.out.println("\nPentagon");

    // This can be done just with the side length
    double side = getDoubleInput("Enter side length: ");

    double perimeter = 5 * side;
    double area = (1/4.0) * Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) * side * side;

    System.out.println("Area: " + area);
    System.out.println("Perimeter: " + perimeter);

    }


    private void pentagram() {
        System.out.println("\nPentagram");
        double side = getDoubleInput("Enter side length: ");
        double perimeter = 5 * side;
        // The following formula calculates the area of a regular pentagram
        // I had to use Math.tan and Math.toRadians to convert degrees to radians for the tangent function
        double area = (5 / 4.0) * side * side * Math.tan(Math.toRadians(54));
        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);
    }

    private void semiCircle() {
    System.out.println("\nSemi-Circle");

    double radius = getDoubleInput("Enter radius: ");

    double area = 0.5 * Math.PI * radius * radius;
    double perimeter = Math.PI * radius + 2 * radius;

    System.out.println("Area: " + area);
    System.out.println("Perimeter: " + perimeter);

    }


 

    // Input validation methods

    // Integer validation method
    private int getIntInput(String message) {
        System.out.print(message);
        while (!scanner.hasNextInt()) { // validate integer input
            System.out.print("Invalid input :( Try again: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    // Double (float) validation method
    private double getDoubleInput(String message) {
        System.out.print(message);
        while (!scanner.hasNextDouble()) { // validate double input
            System.out.print("Invalid input :( Try again: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    // Pause method to read results and/or select another option
    private void pause() {
        System.out.println("\nPress the enter key to continue");
        // The nextline method is used to wait for user input
        // The reason there are 2 scanner.nextLine() is that the first one consumes the newline character left by nextInt or nextDouble ('\n')
        // meaning that the second one waits for the user to actually press the enter key
        scanner.nextLine(); // consume newline ('\n')
        scanner.nextLine(); // wait for user input

    }
}
