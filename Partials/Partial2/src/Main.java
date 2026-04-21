package Partials.Partial2.src;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import Partials.Partial2.src.GeminiRequest.DescriptionStore;
import Partials.Partial2.src.GeminiRequest.GeminiService;
import Partials.Partial2.src.Geo.LocationSummary;
import Partials.Partial2.src.MediaReader.FileScanner;
import Partials.Partial2.src.MediaReader.MediaSorter;
import Partials.Partial2.src.MediaReader.MetadataExtractor;
import Partials.Partial2.src.Tools.MediaCopier;
import Partials.Partial2.src.Video.VideoCreator;

public class Main {
    public static void main(String[] args) throws Exception {


        int n = 4; // seconds each image is shown in the slideshow
        String folderPath = "Partials/Partial2/MediaInput";

        // Scan, extract metadata, sort 
        FileScanner scanner   = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();
        MediaSorter sorter    = new MediaSorter();
        VideoCreator creator  = new VideoCreator();

        System.out.println("No errors in path");

        var files = scanner.getFiles(folderPath);
        System.out.println("Files found: " + files.size());

        var mediaFiles = extractor.extractMetadata(files);
        System.out.println("Metadata extracted");

        var sorted = sorter.sortByDate(mediaFiles);
        System.out.println("Media sorted by date");

        LocationSummary locations = extractor.extractFirstAndLastLocation(sorted);
        if (locations != null) {
            System.out.println(locations);
        } else {
            System.out.println("Could not extract locations.");
        }

        // Create the overall video 
        creator.createVideo(sorted, n);
        String overallVideo = "output.mp4";
        System.out.println("Overall video created: " + overallVideo);

        MediaCopier copier = new MediaCopier();
        GeminiService gemini = new GeminiService("TU_API_KEY");
        DescriptionStore store = new DescriptionStore();

        //Copiar archivos en orden
        var copiedFiles = copier.copyFilesInOrder(sorted, "ordered_media");

        //Procesar con Gemini
        Map<String, String> descriptions = new LinkedHashMap<>();

        for (File file : copiedFiles) {
            String response = gemini.describeMedia(file);

            descriptions.put(file.getName(), response);

            Thread.sleep(1000); // evitar rate limit
        }

        //Guardar resultados
        store.saveAsJson(descriptions, "descriptions.json");

        File summaryImage = gemini.generateSummaryImage(descriptions, "summary.png");

        System.out.println("Summary image created: " + summaryImage.getAbsolutePath());
    }

}