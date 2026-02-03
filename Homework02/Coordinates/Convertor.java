package Homework02.Coordinates;

public class Convertor {

    public double[] polarToCartesian(double r, double thetaDegrees) {
        double thetaRad = Math.toRadians(thetaDegrees);
        return new double[]{
            r * Math.cos(thetaRad),
            r * Math.sin(thetaRad)
        };
    }

    public double[] cartesianToPolar(double x, double y) {
        return new double[]{
            Math.sqrt(x * x + y * y),
            Math.toDegrees(Math.atan2(y, x))
        };
    }
}
