package Partials.Partial2.src;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Partials.Partial2.src.GeminiRequest.DescriptionStore;
import Partials.Partial2.src.GeminiRequest.GeminiService;
import Partials.Partial2.src.Geo.LocationSummary;
import Partials.Partial2.src.Geo.MapService;
import Partials.Partial2.src.MediaReader.FileScanner;
import Partials.Partial2.src.MediaReader.MediaFile;
import Partials.Partial2.src.MediaReader.MediaSorter;
import Partials.Partial2.src.MediaReader.MetadataExtractor;
import Partials.Partial2.src.Tools.AudioNormalizer;
import Partials.Partial2.src.Tools.MediaCopier;
import Partials.Partial2.src.Tools.TTSService;
import Partials.Partial2.src.Video.VideoCreator;

public class Main {
    public static void main(String[] args) throws Exception {

        final String GeminiAPIKey = "GeminiAPIGoesHere";
        final String GeoApifyAPIKey = "GeoApifyKeyGoesHere";

        String folderPath = "Partials/Partial2/MediaInput";

        // Scan, extract metadata, sort
        FileScanner scanner     = new FileScanner();
        MetadataExtractor extractor = new MetadataExtractor();
        MediaSorter sorter      = new MediaSorter();

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

        MediaCopier copier  = new MediaCopier();
        GeminiService gemini = new GeminiService(GeminiAPIKey);
        DescriptionStore store = new DescriptionStore();

        // Copy files in order
        var copiedFiles = copier.copyFilesInOrder(sorted, "Partials/Partial2/ordered_media");

        // Describe each MediaFile with Gemini
        Map<String, String> descriptions = new LinkedHashMap<>();
        for (File file : copiedFiles) {
            String response = gemini.describeMedia(file);
            descriptions.put(file.getName(), response);
            Thread.sleep(2000); // Stay polite with Gemini's rate limits
        }

        store.saveAsJson(descriptions, "Partials/Partial2/descriptions.json");

        // Summary image
        boolean hasErrors = descriptions.containsValue("No description found");
        if (hasErrors) System.out.println("Some descriptions are incomplete - attempting to generate image anyway.");

        File summaryImage = gemini.generateSummaryImage(descriptions, "Partials/Partial2/summary.jpg");
        if (summaryImage != null) {
            System.out.println("Image generated at: " + summaryImage.getAbsolutePath());
        } else {
            System.out.println("Gemini 2.5 was Unable to generate the image.");
        }

        // Generate TTS audio for each description
        TTSService tts = new TTSService();
        List<File> audioFiles = new ArrayList<>();
        List<String> keys = new ArrayList<>(descriptions.keySet());

        for (int i = 0; i < keys.size(); i++) {
            String desc = descriptions.get(keys.get(i));
            File audio = tts.generateAudio(desc, "audio_" + i + ".mp3");
            audioFiles.add(audio);
            Thread.sleep(1200);
        }

        // Generate quote + its audio
        String quote = gemini.generateQuote(descriptions);

        if (quote == null) {
            quote = "Every journey has a story worth remembering.";
            System.out.println("Using fallback quote");
        }

        System.out.println("Quote result: " + quote);
        File quoteAudio = null;

        if (quote != null) {
            System.out.println("Quote: " + quote);
            quoteAudio = tts.generateAudio(quote, "audio_quote.mp3");
        }

        // Build all media and audio lists
        List<MediaFile> allMedia = new ArrayList<>();
        List<File> allAudio     = new ArrayList<>();

        // [0] Summary image (no audio)
        if (summaryImage != null) {
            allMedia.add(new MediaFile(summaryImage));
            allAudio.add(null);
        } else {
            System.out.println("No summary image - video starts directly with media.");
        }

        // Ordered media + TTS audio
        copiedFiles.stream().map(f -> new MediaFile(f)).forEach(allMedia::add);
        allAudio.addAll(audioFiles);

        // Map image (no audio)
        MapService mapService = new MapService(GeoApifyAPIKey);
        if (locations != null) {
            File mapImage = mapService.generateMap(
                locations.getFirstLocation(),
                locations.getLastLocation(),
                "map.jpg"
            );
            if (mapImage != null) {
                allMedia.add(new MediaFile(mapImage));
                allAudio.add(null);
            }
        }

        // Quote slide (audio baked in — handled by segment_quote check in VideoCreator)
        VideoCreator vc = new VideoCreator();
        if (quote != null) {
            File quoteSlide = vc.createQuoteSlide(quote, quoteAudio, "segment_quote.mp4");
            System.out.println("Quote slide exists: " + (quoteSlide != null));
            System.out.println("Quote slide path: " + (quoteSlide != null ? quoteSlide.getAbsolutePath() : "NULL"));
            if (quoteSlide != null) {
                allMedia.add(new MediaFile(quoteSlide));
                allAudio.add(null); // audio already baked into the segment
            }
        }

        // Create the final video
        vc.createVideo(allMedia, allAudio);

        // Check output.mp4 exists before normalization
        File outputVideo = new File("output.mp4");
        System.out.println("output.mp4 exists: " + outputVideo.exists());
        System.out.println("output.mp4 absolute path: " + outputVideo.getAbsolutePath());

        // Normalize
        String outputPath = new File("output.mp4").getAbsolutePath();
        String normalizedPath = new File("output_normalized.mp4").getAbsolutePath();

        AudioNormalizer normalizer = new AudioNormalizer();
        normalizer.normalize(outputPath, normalizedPath);
    }
}