package Partials.Partial2.src;
import java.io.File;
import java.util.List;

public class FileScanner {

    public List<File> getFiles(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        // The methood listfiles() returns an array of File objects.
        // which are converted to a list in the following line:
        return List.of(files);
    }
}