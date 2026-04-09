package Partials.Partial2.src;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MetadataExtractor {

    public List<MediaFile> extractMetadata(List<File> files) {
        List<MediaFile> result = new ArrayList<>();

        for (File file : files) {
            String date = getDate(file);
            result.add(new MediaFile(file, date));
        }

        return result;
    }

    private String getDate(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c",
                "exiftool -DateTimeOriginal \"" + file.getAbsolutePath() + "\""
            );

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line = reader.readLine();

            if (line != null) {
                return line.split(": ", 2)[1];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "unknown";
    }
}