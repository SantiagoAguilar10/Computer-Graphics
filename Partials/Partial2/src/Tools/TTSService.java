package Partials.Partial2.src.Tools;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TTSService {

    // Max chars Google Translate TTS accepts per request
    private static final int MAX_CHUNK = 180;

    /**
     * Generates an MP3 audio file from the given text using Google Translate TTS.
     * Splits long texts into chunks and concatenates them with ffmpeg.
     *
     * @param text     The description text to synthesize
     * @param filename Output filename, e.g. "audio_0.mp3"
     * @return The generated File, or null on failure
     */
    public File generateAudio(String text, String filename) {
        try {
            // Handle failed descriptions gracefully — generate silence instead
            if (text.startsWith("Failed to get a valid response")) {
                return generateSilence(filename, 3);
            }

            List<String> chunks = splitText(text, MAX_CHUNK);

            if (chunks.size() == 1) {
                // Single chunk — downloads directly to the output file
                downloadChunk(chunks.get(0), filename);
            } else {
                // Multiple chunks — downloads each, then concat with ffmpeg
                List<String> chunkFiles = new ArrayList<>();
                for (int i = 0; i < chunks.size(); i++) {
                    String chunkFile = filename + "_chunk" + i + ".mp3";
                    downloadChunk(chunks.get(i), chunkFile);
                    chunkFiles.add(chunkFile);
                }
                concatAudioFiles(chunkFiles, filename);

                // Clean up chunk files
                for (String cf : chunkFiles) new File(cf).delete();
            }

            File out = new File(filename);
            return out.exists() ? out : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Downloads one TTS chunk from Google Translate using curl.
     * @param text The text to synthesize (must be <= MAX_CHUNK)
     * @param outputFile The output MP3 file path
     */
    private void downloadChunk(String text, String outputFile) throws Exception {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String url = "https://translate.google.com/translate_tts"
                + "?ie=UTF-8&q=" + encoded
                + "&tl=en"          // English
                + "&client=gtx"     // Free endpoint
                + "&ttsspeed=0.9";  // Slightly slower = clearer narration

        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "curl -s -A \"Mozilla/5.0\" -o \"" + outputFile + "\" \"" + url + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getInputStream().transferTo(System.out);
        p.waitFor();
    }

    /**
     * Splits text into chunks at word boundaries, respecting MAX_CHUNK.
     * @param text The input text to split
     * @param maxLen The maximum length of each chunk
     */
    private List<String> splitText(String text, int maxLen) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            // +1 for the space
            if (current.length() + word.length() + 1 > maxLen && current.length() > 0) {
                chunks.add(current.toString().trim());
                current = new StringBuilder();
            }
            current.append(word).append(" ");
        }
        if (!current.toString().isBlank()) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }

    /**
     * Concatenates multiple MP3 files into one using ffmpeg concat demuxer.
     * @param files List of input MP3 file paths to concatenate
     * @param output The output MP3 file path
     */
    private void concatAudioFiles(List<String> files, String output) throws Exception {
        // Write a concat list file
        File listFile = new File("audio_concat_list.txt");
        try (PrintWriter pw = new PrintWriter(listFile)) {
            for (String f : files) {
                pw.println("file '" + new File(f).getAbsolutePath() + "'");
            }
        }

        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "ffmpeg -y -f concat -safe 0 -i audio_concat_list.txt"
            + " -c copy \"" + output + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getInputStream().transferTo(System.out);
        p.waitFor();
        listFile.delete();
    }

    /**
     * Generates a silent MP3 of the given duration (fallback for failed descriptions).
     * @param filename The output MP3 file path
     * @param seconds The duration of the silent audio
     * @return The generated File object or null if failed
     */
    private File generateSilence(String filename, int seconds) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "ffmpeg -y -f lavfi -i anullsrc=r=44100:cl=mono"
            + " -t " + seconds
            + " -c:a libmp3lame \"" + filename + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getInputStream().transferTo(System.out);
        p.waitFor();
        File out = new File(filename);
        return out.exists() ? out : null;
    }
}