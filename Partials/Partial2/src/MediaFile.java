package Partials.Partial2.src;
import java.io.File;

public class MediaFile {
    private File file;
    private String dateTaken;

    public MediaFile(File file, String dateTaken) {
        this.file = file;
        this.dateTaken = dateTaken;
    }

    public File getFile() { return file; }
    public String getDateTaken() { return dateTaken; }
}