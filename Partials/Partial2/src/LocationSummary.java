package Partials.Partial2.src;

public class LocationSummary {
    private GeoLocation firstLocation;
    private GeoLocation lastLocation;

    public LocationSummary(GeoLocation firstLocation, GeoLocation lastLocation) {
        this.firstLocation = firstLocation;
        this.lastLocation = lastLocation;
    }

    public GeoLocation getFirstLocation() {
        return firstLocation;
    }

    public GeoLocation getLastLocation() {
        return lastLocation;
    }

    @Override
    public String toString() {
        return "Primera: " + firstLocation + "\nÚltima: " + lastLocation;
    }
}