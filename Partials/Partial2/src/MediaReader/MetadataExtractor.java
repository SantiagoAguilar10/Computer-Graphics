package Partials.Partial2.src.MediaReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Partials.Partial2.src.Geo.GeoLocation;
import Partials.Partial2.src.Geo.LocationSummary;

public class MetadataExtractor {

    public List<MediaFile> extractMetadata(List<File> files) {
        List<MediaFile> result = new ArrayList<>();

        for (File file : files) {
            String date = getDate(file);
            result.add(new MediaFile(file, date));
        }

        return result;
    }

    private String getDate(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "exiftool -DateTimeOriginal \"" + file.getAbsolutePath() + "\""
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line = reader.readLine();

            if (line != null) {
                return line.split(": ", 2)[1];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "unknown";
    }

    public LocationSummary extractFirstAndLastLocation(List<MediaFile> sortedMedia) {

        if (sortedMedia == null || sortedMedia.isEmpty()) {
            return null;
        }

        MediaFile first = sortedMedia.get(0);
        MediaFile last = sortedMedia.get(sortedMedia.size() - 1);

        GeoLocation firstLoc = getGeoLocation(first.getFile());
        GeoLocation lastLoc = getGeoLocation(last.getFile());

        return new LocationSummary(firstLoc, lastLoc);
    }

    private GeoLocation getGeoLocation(File file) {

        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "exiftool -n -GPSLatitude -GPSLongitude \"" + file.getAbsolutePath() + "\""
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line;
            Double lat = null;
            Double lon = null;

            while ((line = reader.readLine()) != null) {

                if (line.contains("GPS Latitude")) {
                    lat = parseDoubleValue(line);
                }

                if (line.contains("GPS Longitude")) {
                    lon = parseDoubleValue(line);
                }
            }

            if (lat != null && lon != null) {
                return new GeoLocation(lat, lon);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // no hay GPS
    }

    private double parseDoubleValue(String line) {
        try {
            return Double.parseDouble(line.split(":", 2)[1].trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}