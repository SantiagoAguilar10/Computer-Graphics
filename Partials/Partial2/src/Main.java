package Partials.Partial2.src;

public class Main {
    public static void main(String[] args) {
        String folderPath = "Computer-Graphics/Partials/Partial2/MediaInput";

        FileScanner scanner = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();
        MediaSorter sorter = new MediaSorter();
        VideoCreator creator = new VideoCreator();

        // Use of "var" to infer types in Java.

        // Ideal Process:
        var files = scanner.getFiles(folderPath);
        var mediaFiles = extractor.extractMetadata(files);
        var sorted = sorter.sortByDate(mediaFiles);
        LocationSummary locations = extractor.extractFirstAndLastLocation(sorted);

        if (locations != null) {
            System.out.println(locations);
        } else {
            System.out.println("No se pudieron obtener ubicaciones.");
        }

        creator.createVideo(sorted);

    }
}