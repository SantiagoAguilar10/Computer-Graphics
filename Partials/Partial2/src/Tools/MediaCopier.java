package Partials.Partial2.src.Tools;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import Partials.Partial2.src.MediaReader.MediaFile;
import java.util.List;
import java.util.ArrayList;


public class MediaCopier {

    public List<File> copyFilesInOrder(List<MediaFile> mediaList, String outputDirPath) throws IOException {
        List<File> copiedFiles = new ArrayList<>();
        File outputDir = new File(outputDirPath);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        int index = 0;

        for (MediaFile media : mediaList) {
            File source = media.getFile();

            String extension = source.getName()
                .substring(source.getName().lastIndexOf("."));

            String newName = String.format("%03d%s", index++, extension);

            File destination = new File(outputDir, newName);

            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

            copiedFiles.add(destination); 
        }

        return copiedFiles;
    }
}