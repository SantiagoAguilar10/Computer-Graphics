package Partials.Partial2.src;

public class Main {
    public static void main(String[] args) {
        String folderPath = "Partials/Partial2/MediaInput";
        System.out.println("No errors in path");

        FileScanner scanner = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();
        MediaSorter sorter = new MediaSorter();
        VideoCreator creator = new VideoCreator();

        // Use of "var" to infer types in Java.

        // Ideal Process:
        var files = scanner.getFiles(folderPath);
        System.out.println("Files found : " + files.size());

        var mediaFiles = extractor.extractMetadata(files);
        System.out.println("Metadata Extracted");

        var sorted = sorter.sortByDate(mediaFiles);
        System.out.println("Media sorted by Dates");

        LocationSummary locations = extractor.extractFirstAndLastLocation(sorted);

        if (locations != null) {
            System.out.println(locations);
        } else {
            System.out.println("Could not extract locations.");
        }

        creator.createVideo(sorted);

    }
}