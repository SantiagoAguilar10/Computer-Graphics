package Homework02.Coordinates;

public class Convertor {

    public double[] polarToCartesian(double r, double thetaDegrees) { // Convert degrees to radians
        double thetaRad = Math.toRadians(thetaDegrees); // Conversion formula
        return new double[]{ // Return x and y as an array
            r * Math.cos(thetaRad),
            r * Math.sin(thetaRad)
        };
    }

    public double[] cartesianToPolar(double x, double y) { // Calculate r and theta
        return new double[]{ // Return r and theta as an array
            Math.sqrt(x * x + y * y), // r calculation
            Math.toDegrees(Math.atan2(y, x)) // theta calculation in degrees
        };
    }
}
