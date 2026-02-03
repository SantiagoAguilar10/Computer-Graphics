package Homework02.Coordinates;

public class Menu {

    private IOdata io = new IOdata();
    private Convertor convertor = new Convertor();

    public void run() {
        int choice;
        do {
            displayMenu();
            choice = io.getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> polarToCartesian();
                case 2 -> cartesianToPolar();
                case 3 -> System.out.println("Thanks for using my converter. :)");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 3);
    }

    // Convert polar to Cartesian coordinates
    private void polarToCartesian() {
        double r = io.getDoubleInput("Enter r: ");
        double theta = io.getDoubleInput("Enter theta: ");
        double[] result = convertor.polarToCartesian(r, theta);
        System.out.printf("(x: %.2f, y: %.2f)%n", result[0], result[1]);
    }

    // Convert Cartesian to polar coordinates
    private void cartesianToPolar() {
        double x = io.getDoubleInput("Enter x: ");
        double y = io.getDoubleInput("Enter y: ");
        double[] result = convertor.cartesianToPolar(x, y);
        System.out.printf("(r: %.2f, theta: %.2f)%n", result[0], result[1]);
    }

    private void displayMenu() {
        System.out.println("1. Polar to Cartesian");
        System.out.println("2. Cartesian to Polar");
        System.out.println("3. Exit");
    }
}
