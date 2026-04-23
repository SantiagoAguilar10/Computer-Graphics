package Partials.Partial2.src.Geo;
import java.io.*;

public class MapService {

    private final String apiKey;
    private static final int WIDTH  = 1080;
    private static final int HEIGHT = 1920;

    public MapService(String apiKey) {
        this.apiKey = apiKey;
    }

    public File generateMap(GeoLocation first, GeoLocation last, String outputPath)
            throws IOException, InterruptedException {

        String url = buildUrl(first, last);

        System.out.println("Map URL: " + url); // Debug

        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "curl -s -o \"" + outputPath + "\" \"" + url + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getInputStream().transferTo(System.out);
        p.waitFor();

        File out = new File(outputPath);
        if (!out.exists() || out.length() == 0) {
            System.out.println("curl failed: GeoApify returned nothing or empty file.");
            return null;
        }

        byte[] bytes = java.nio.file.Files.readAllBytes(out.toPath());

        boolean isPng  = bytes[0] == (byte)0x89 && bytes[1] == (byte)0x50;
        boolean isJpeg = bytes[0] == (byte)0xFF && bytes[1] == (byte)0xD8;

        if (!isPng && !isJpeg) {
            System.out.println("GeoApify error response: " +
                new String(bytes, 0, Math.min(300, bytes.length)));
            out.delete();
            return null;
        }

        System.out.println("Map saved: " + outputPath);
        return out;
    }

    private String buildUrl(GeoLocation first, GeoLocation last) {
        double lon1 = first.getLongitude();
        double lat1 = first.getLatitude();
        double lon2 = last.getLongitude();
        double lat2 = last.getLatitude();

        double centerLon = (lon1 + lon2) / 2.0;
        double centerLat = (lat1 + lat2) / 2.0;

        int zoom = calculateZoom(lat1, lon1, lat2, lon2);

        return "https://maps.geoapify.com/v1/staticmap"
            + "?style=osm-bright"
            + "&width="   + WIDTH
            + "&height="  + HEIGHT
            + "&center=lonlat:" + centerLon + "," + centerLat
            + "&zoom="    + zoom
            + "&marker=lonlat:" + lon1 + "," + lat1
                + ";color:%23ff0000;size:large;text:Start"
            + "&marker=lonlat:" + lon2 + "," + lat2
                + ";color:%230000ff;size:large;text:End"
            + "&apiKey="  + apiKey;
    }

    /**
     * Estimates an appropriate zoom level based on distance between two points.
     * Uses the Haversine formula to get distance in km, then maps to zoom.
     */
    private int calculateZoom(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double distanceKm = 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        System.out.println("Distance between pins: " + Math.round(distanceKm) + " km");

        if (distanceKm < 0.5)   return 16; // same street
        if (distanceKm < 2)     return 14; // same neighborhood
        if (distanceKm < 10)    return 12; // same city
        if (distanceKm < 50)    return 10; // same region
        if (distanceKm < 300)   return 7;  // same country
        if (distanceKm < 1500)  return 5;  // same continent region
        if (distanceKm < 5000)  return 3;  // intercontinental
        return 2;                           // world scale
    }
}