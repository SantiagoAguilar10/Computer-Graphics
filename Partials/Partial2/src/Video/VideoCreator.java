package Partials.Partial2.src.Video;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import Partials.Partial2.src.MediaReader.MediaFile;

public class VideoCreator {

    private static final int DEFAULT_IMAGE_DURATION = 4; // seconds per image

    public void createVideo(List<MediaFile> mediaFiles) {
        createVideo(mediaFiles, DEFAULT_IMAGE_DURATION);
    }

    
    // Function Overload
    public void createVideo(List<MediaFile> mediaFiles, int imageDurationSeconds) {
        try {
            File temp = new File("input.txt");
            PrintWriter writer = new PrintWriter(temp);

            for (MediaFile mf : mediaFiles) {
                writer.println("file '" + mf.getFile().getAbsolutePath() + "'");

                if (mf.isVideo()) {
                    double duration = getVideoDuration(mf.getFile().getAbsolutePath());
                    writer.println("duration " + duration);
                } else {
                    writer.println("duration " + imageDurationSeconds);
                }
            }

            MediaFile last = mediaFiles.get(mediaFiles.size() - 1);
            writer.println("file '" + last.getFile().getAbsolutePath() + "'");

            writer.close();

            // Portrait mode: scale to 1080x1920, pad black bars if aspect doesn't match,
            // keeping the original image/video orientation intact
            String videoFilter =
                "scale=1080:1920:force_original_aspect_ratio=decrease," +
                "pad=1080:1920:(ow-iw)/2:(oh-ih)/2:black," +
                "setsar=1";

            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "ffmpeg -f concat -safe 0 -i input.txt" +
                " -vf \"" + videoFilter + "\"" +
                " -vsync vfr" +
                " -c:v libx264 -crf 23 -preset fast" +
                " -an" +  // keep audio from any source videos
                " output.mp4"
            );

            pb.redirectErrorStream(true);
            Process p = pb.start();

            // Drain output so process doesn't block
            new Thread(() -> {
                try { p.getInputStream().transferTo(System.out); }
                catch (Exception ignored) {}
            }).start();

            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Uses ffprobe to read the real duration of a video file
    protected double getVideoDuration(String filePath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "cmd.exe", "/c",
            "ffprobe -v error -show_entries format=duration" +
            " -of default=noprint_wrappers=1:nokey=1 \"" + filePath + "\""
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes()).trim();
        p.waitFor();
        return Double.parseDouble(output);
    }
}