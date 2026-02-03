package Homework02.AspectRatio;

// Program that returns aspect ratio of given width and height
public class Ratio {

    public String getAspectRatio(int width, int height) {
        int gcd = computeGCD(width, height);
        int aspectWidth = width / gcd;
        int aspectHeight = height / gcd;
        return aspectWidth + ":" + aspectHeight;
    }

    // Recursive method to compute GCD
    private int computeGCD(int a, int b) {
        if (b == 0) {
            return a;
        }
        return computeGCD(b, a % b);
    }
    
}
