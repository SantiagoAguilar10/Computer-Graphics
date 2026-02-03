package Homework02.AspectRatio;

public class Ratio {

    public String getAspectRatio(int width, int height) {
        int gcd = computeGCD(width, height); // Aspect ratio is calculated using GCD
        int aspectWidth = width / gcd; // Simplify width
        int aspectHeight = height / gcd; // Simplify height
        return aspectWidth + ":" + aspectHeight; // Return as "width:height" format
    }

    // Recursive method to compute GCD
    private int computeGCD(int a, int b) { 
        if (b == 0) { // Base case
            return a; // Return GCD
        }
        return computeGCD(b, a % b); // Recursive call with new values
    }
    
}
