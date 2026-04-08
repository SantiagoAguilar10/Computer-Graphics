package Partials.Partial2.src;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class VideoCreator {

    public void createVideo(List<MediaFile> mediaFiles) {
        try {

            // Create temporal file with list of media files and durations for ffmpeg
            File temp = new File("input.txt");

            PrintWriter writer = new PrintWriter(temp);

            for (MediaFile mf : mediaFiles) {
                writer.println("file '" + mf.getFile().getAbsolutePath() + "'");
                writer.println("duration 2");
            }

            writer.close();

            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "ffmpeg -f concat -safe 0 -i input.txt -vsync vfr output.mp4"
            );

            pb.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}