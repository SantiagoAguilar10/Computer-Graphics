package Partials.Partial2.src.MediaReader;
import java.io.File;
import java.util.List;

public class FileScanner {

    public List<File> getFiles(String path) {
        File folder = new File(path);
        System.out.println("Scanning folder: " + folder.getAbsolutePath());
        File[] files = folder.listFiles();
        System.out.println("Files found: " + (files != null ? files.length : 0));
        // The methood listfiles() returns an array of File objects.
        // which are converted to a list in the following line:
        return List.of(files);
    }
}