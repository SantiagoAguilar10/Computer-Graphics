package Partials.Partial2.src.MediaReader;
import java.io.File;
import java.util.Set;

public class MediaFile {
    private File file;
    private String dateTaken;

    public MediaFile(File file, String dateTaken) {
        this.file = file;
        this.dateTaken = dateTaken;
    }

    public MediaFile(File file) {
        this.file = file;
        this.dateTaken = "Unknown";
    }

    public File getFile() { return file; }
    public String getDateTaken() { return dateTaken; }


    public boolean isVideo() {
        String fileName = file.getName().toLowerCase();

        // Obtain Extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return false; // Doesn't have an extension
        }

        String extension = fileName.substring(dotIndex + 1);

        // Lista de extensiones de video
        Set<String> videoExtensions = Set.of("mp4", "mov", "avi", "mkv");

        return videoExtensions.contains(extension);
    }
}