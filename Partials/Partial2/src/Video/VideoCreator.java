package Partials.Partial2.src.Video;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import Partials.Partial2.src.MediaReader.MediaFile;


public class VideoCreator {

    private static final int DEFAULT_IMAGE_DURATION = 4;

    public void createVideo(List<MediaFile> mediaFiles, List<File> audioFiles) {
        createVideo(mediaFiles, audioFiles, DEFAULT_IMAGE_DURATION);
    }

    public void createVideo(List<MediaFile> mediaFiles, List<File> audioFiles, int imageDurationSeconds) {
        try {
            // Build one segment per media file (video + its TTS audio)
            List<String> segmentFiles = new ArrayList<>();

            for (int i = 0; i < mediaFiles.size(); i++) {
                MediaFile mf = mediaFiles.get(i);
                System.out.println("Processing: " + mf.getFile().getName());

                File audio = (audioFiles != null && i < audioFiles.size()) ? audioFiles.get(i) : null;
                String segmentOut = "segment_" + i + ".mp4";

                if (mf.getFile().getName().startsWith("segment_quote")) {
                    segmentFiles.add(mf.getFile().getAbsolutePath()); // use as-is
                    continue; // skip re-encoding
                }

                if (mf.isVideo()) {
                    buildVideoSegment(mf.getFile(), audio, segmentOut);
                } else {
                    double duration = (audio != null) ? getAudioDuration(audio.getAbsolutePath())
                                                    : imageDurationSeconds;
                    buildImageSegment(mf.getFile(), audio, duration, segmentOut);
                }

                segmentFiles.add(segmentOut);
            }

            //Concatenate all segments into output.mp4
            concatSegments(segmentFiles, "Partials/Partial2/outputfinal.mp4");

            // Clean up segment files
            for (String seg : segmentFiles) new File(seg).delete();
            new File("input.txt").delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Segment builders
    /**
     * Image + TTS audio = segment.
     * The segment duration matches the audio length (or fallback seconds).
     */
    private void buildImageSegment(File image, File audio, double duration, String output) throws Exception {
        String videoFilter =
            "scale=1080:1920:force_original_aspect_ratio=decrease," +
            "pad=1080:1920:(ow-iw)/2:(oh-ih)/2:black," +
            "setsar=1";

        String audioInput = (audio != null) ? " -i \"" + audio.getAbsolutePath() + "\"" : "";
        String audioMap   = (audio != null) ? " -map 0:v -map 1:a -c:a aac -shortest" : " -an";

        String cmd =
            "ffmpeg -y -loop 1 -t " + duration + " -i \"" + image.getAbsolutePath() + "\""
            + audioInput
            + " -vf \"" + videoFilter + "\""
            + " -fps_mode vfr -c:v libx264 -crf 23 -preset fast"
            + audioMap
            + " \"" + output + "\"";

        // System.out.println("CMD: " + cmd); // Debug
        System.out.println("Building segment for: " + image.getAbsolutePath());
        runCommand(cmd);
        // System.out.println("Segment done: " + output); // Debug
    }

    /**
     * Video (audio stripped) + TTS audio = segment.
     * Duration is clamped to the longer of video/audio so nothing is cut short.
     */
    private void buildVideoSegment(File video, File audio, String output) throws Exception {
        String videoFilter =
            "scale=1080:1920:force_original_aspect_ratio=decrease," +
            "pad=1080:1920:(ow-iw)/2:(oh-ih)/2:black," +
            "setsar=1";

        String cmd;

        if (audio != null) {
            cmd = "ffmpeg -y"
                + " -i \"" + video.getAbsolutePath() + "\""   // input 0: video
                + " -i \"" + audio.getAbsolutePath() + "\""   // input 1: TTS audio
                + " -map 0:v"          // take video stream from input 0
                + " -map 1:a"          // take audio stream from input 1 (original audio ignored)
                + " -vf \"" + videoFilter + "\""
                + " -c:v libx264 -crf 23 -preset fast"
                + " -c:a aac -shortest"
                + " \"" + output + "\"";
        } else {
            cmd = "ffmpeg -y"
                + " -i \"" + video.getAbsolutePath() + "\""
                + " -map 0:v"          // video only, no audio at all
                + " -vf \"" + videoFilter + "\""
                + " -c:v libx264 -crf 23 -preset fast -an"
                + " \"" + output + "\"";
        }

        runCommand(cmd);
    }

    // Concat
    private void concatSegments(List<String> segments, String output) throws Exception {
        File listFile = new File("segments_list.txt");
        try (PrintWriter pw = new PrintWriter(listFile)) {
            for (String seg : segments) {
                pw.println("file '" + new File(seg).getAbsolutePath() + "'");
            }
        }

        String cmd =
            "ffmpeg -y -f concat -safe 0 -i segments_list.txt"
            + " -c:v libx264 -crf 23 -preset fast"
            + " -c:a aac"
            + " \"" + new File("output.mp4").getAbsolutePath() + "\"";

        runCommand(cmd);
        listFile.delete();
    }

    // Helpers :)
    private void runCommand(String cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        new Thread(() -> {
            try { p.getInputStream().transferTo(System.out); }
            catch (Exception ignored) {}
        }).start();
        p.waitFor();
    }

    protected double getVideoDuration(String filePath) throws Exception {
        return probeDuration(filePath);
    }

    protected double getAudioDuration(String filePath) throws Exception {
        return probeDuration(filePath);
    }

    private double probeDuration(String filePath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "ffprobe -v error"
            + " -count_packets"
            + " -show_entries stream=nb_read_packets,duration"
            + " -of default=noprint_wrappers=1:nokey=1"
            + " \"" + filePath + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String out = new String(p.getInputStream().readAllBytes()).trim();
        p.waitFor();

        for (String line : out.split("\\r?\\n")) {
            try {
                double val = Double.parseDouble(line.trim());
                if (val > 0) {
                    System.out.println("Duration probed for " + filePath + ": " + val);
                    return val; // ← returns the real duration if found
                }
            } catch (NumberFormatException ignored) {}
        }

         // System.out.println("Duration fallback for " + filePath); // Debug
        return 3.0; //only reaches here if nothing parsed
    }

    /**
     * Creates a black slide with the quote as white centered text.
     * Duration matches the TTS audio of the quote.
     */
    public File createQuoteSlide(String quote, File audio, String output) throws Exception {
        double duration = (audio != null) ? getAudioDuration(audio.getAbsolutePath()) : 5.0;

        String safeQuote = quote.replace("'", "\u2019").replace("\"", "");

        List<String> lines = splitLines(safeQuote, 30);

        int fontSize = 60;
        int lineSpacing = 20;
        int totalHeight = lines.size() * (fontSize + lineSpacing);
        int startY = (1920 - totalHeight) / 2;

        StringBuilder vf = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) vf.append(",");
            int y = startY + i * (fontSize + lineSpacing);
            vf.append("drawtext=text='").append(lines.get(i)).append("'")
            .append(":fontcolor=white")
            .append(":fontsize=").append(fontSize)
            .append(":font='Arial'")
            .append(":x=(w-text_w)/2")
            .append(":y=").append(y);
        }

        String cmd = "ffmpeg -y"
            + " -f lavfi -i color=c=black:s=1080x1920:r=25"
            + (audio != null ? " -i \"" + audio.getAbsolutePath() + "\"" : "")
            + " -t " + duration
            + " -vf \"" + vf + "\""
            + " -map 0:v"
            + (audio != null ? " -map 1:a -c:a aac" : " -an")
            + " -c:v libx264 -crf 23 -preset fast -shortest"
            + " \"" + new File(output).getAbsolutePath() + "\"";

        System.out.println("Quote slide CMD: " + cmd);
        runCommand(cmd);

        File out = new File(output);
        System.out.println("Quote slide file exists: " + out.exists());
        return out.exists() ? out : null;
    }


    private List<String> splitLines(String text, int maxChars) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxChars && line.length() > 0) {
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (line.length() > 0) lines.add(line.toString().trim());
        return lines;
    }
}