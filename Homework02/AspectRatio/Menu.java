package Homework02.AspectRatio;

public class Menu {

    private IOdata ioData = new IOdata();
    private Ratio ratioCalculator = new Ratio();

    public void run() {
        System.out.println("Aspect Ratio Calculator"); 
        int width = ioData.getIntInput("Enter width: ");
        int height = ioData.getIntInput("Enter height: ");
        String aspectRatio = ratioCalculator.getAspectRatio(width, height); // Get aspect ratio
        System.out.println("The aspect ratio is: " + aspectRatio);
    }
    
}
