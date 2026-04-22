package Partials.Partial2.src;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Partials.Partial2.src.GeminiRequest.DescriptionStore;
import Partials.Partial2.src.GeminiRequest.GeminiService;
import Partials.Partial2.src.Geo.LocationSummary;
import Partials.Partial2.src.MediaReader.FileScanner;
import Partials.Partial2.src.MediaReader.MediaFile;
import Partials.Partial2.src.MediaReader.MediaSorter;
import Partials.Partial2.src.MediaReader.MetadataExtractor;
import Partials.Partial2.src.Tools.MediaCopier;
import Partials.Partial2.src.Video.VideoCreator;

public class Main {
    public static void main(String[] args) throws Exception {

        final String GeminiAPIKey = "";

        String folderPath = "Partials/Partial2/MediaInput";

        // Scan, extract metadata, sort 
        FileScanner scanner   = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();
        MediaSorter sorter    = new MediaSorter();

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

        MediaCopier copier = new MediaCopier();
        GeminiService gemini = new GeminiService(GeminiAPIKey);
        DescriptionStore store = new DescriptionStore();

        //Copiar archivos en orden
        var copiedFiles = copier.copyFilesInOrder(sorted, "Partials/Partial2/ordered_media");

        //Procesar con Gemini
        Map<String, String> descriptions = new LinkedHashMap<>();

        for (File file : copiedFiles) {
            String response = gemini.describeMedia(file);

            descriptions.put(file.getName(), response);

            Thread.sleep(2000); // evitar rate limit
        }

        //Guardar resultados
        store.saveAsJson(descriptions, "Partials/Partial2/descriptions.json");

        boolean hasErrors = descriptions.containsValue("No description found");

        if (hasErrors) {
            System.out.println("Some descriptions failed. Image won't be generated.");
        } else {
            File summaryImage = gemini.generateSummaryImage(descriptions, "summary.png");

            if (summaryImage != null) {
                System.out.println("Image generated at: " + summaryImage.getAbsolutePath());
            } else {
                System.out.println("Unable to generate image.");
            }
        }

        TTSService tts = new TTSService(); // No API key needed

        List<File> audioFiles = new ArrayList<>();
        List<String> keys = new ArrayList<>(descriptions.keySet());

        for (int i = 0; i < keys.size(); i++) {
            String desc = descriptions.get(keys.get(i));

            File audio = tts.generateAudio(desc, "audio_" + i + ".mp3");
            audioFiles.add(audio); // may be null if something went wrong — VideoCreator handles it

            Thread.sleep(1200); // stay polite with Google's free endpoint
        }

        // Create the final video
        VideoCreator vc = new VideoCreator();
        vc.createVideo(copiedFiles.stream()
            .map(f -> new MediaFile(f))  
            .collect(Collectors.toList()),
            audioFiles
        );

    }

}