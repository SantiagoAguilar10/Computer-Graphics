package Partials.Partial2.src.Tools;

import java.io.*;

public class AudioNormalizer {

    /**
     * Applies EBU R128 loudness normalization to the final video.
     * Targets YouTube standards:
     *   Loudness: -14 LUFS
     *   True Peak: -1 dBTP
     *   LRA:        7 LU
     *
     * Two-pass process:
     *   Analyze the audio
     *   Apply the measured correction
     * @param inputVideo The input video file path
     * @param outputVideo The output video file path (e.g. "final_normalized
     */
    public File normalize(String inputVideo, String outputVideo)
            throws IOException, InterruptedException {

        // Pass 1: measure loudness, store results in a temp file
        File statsFile = new File("loudnorm_stats.txt");

        String pass1 = "ffmpeg -y -i \"" + inputVideo + "\""
            + " -af loudnorm=I=-14:TP=-1:LRA=7:print_format=json"
            + " -f null -";

        System.out.println("Loudnorm pass 1: analyzing...");
        String stats = runCommandAndCapture(pass1);
        //System.out.println("Pass 1 output:\n" + stats); // Debug

        // Extract measured values from pass 1 JSON output
        String measuredI    = extractJson(stats, "input_i");
        String measuredTP   = extractJson(stats, "input_tp");
        String measuredLRA  = extractJson(stats, "input_lra");
        String measuredThresh = extractJson(stats, "input_thresh");
        String offset       = extractJson(stats, "target_offset");

        if (measuredI == null) {
            System.out.println("Pass 1 failed to extract stats. Skipping normalization.");
            return new File(inputVideo);
        }

        System.out.println("Measured loudness: " + measuredI + " LUFS");

        // Pass 2: apply correction using measured values
        String audioFilter = "loudnorm=I=-14:TP=-1:LRA=7"
            + ":measured_I="      + measuredI
            + ":measured_TP="     + measuredTP
            + ":measured_LRA="    + measuredLRA
            + ":measured_thresh=" + measuredThresh
            + ":offset="          + offset
            + ":linear=true"      // linear mode = higher quality
            + ":print_format=summary";

        String pass2 = "ffmpeg -y -i \"" + inputVideo + "\""
            + " -af \"" + audioFilter + "\""
            + " -c:v copy"   // video stream untouched
            + " -c:a aac"
            + " \"" + outputVideo + "\"";

        System.out.println("Loudnorm pass 2: applying...");
        runCommand(pass2);

        File out = new File(outputVideo);
        if (out.exists() && out.length() > 0) {
            System.out.println("Normalized video saved: " + outputVideo);
            statsFile.delete();
            return out;
        }

        System.out.println("Normalization failed: returning original.");
        return new File(inputVideo);
    }

    // Runs a command and returns its full output as a String
    /**
     * Runs a command and captures its full output as a String. Waits for the process to finish.
     * @param cmd
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String runCommandAndCapture(String cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes());
        p.waitFor();
        return output;
    }

    /**
     * Runs a command without capturing output. Waits for the process to finish.
     * @param cmd
     * @throws IOException
     * @throws InterruptedException
     */
    private void runCommand(String cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        new Thread(() -> {
            try { p.getInputStream().transferTo(System.out); }
            catch (Exception ignored) {}
        }).start();
        p.waitFor();
    }

    // Extracts a value from ffmpeg's JSON-like loudnorm output
    /**
     * Extracts a value from ffmpeg's JSON-like loudnorm output based on the provided key.
     * @param output
     * @param key
     * @return
     */
    private String extractJson(String output, String key) {
        String search = "\"" + key + "\" : \"";
        int start = output.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = output.indexOf("\"", start);
        if (end == -1) return null;
        return output.substring(start, end).trim();
    }
}