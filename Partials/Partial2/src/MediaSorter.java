package Partials.Partial2.src;
import java.util.Comparator;
import java.util.List;

public class MediaSorter {

    public List<MediaFile> sortByDate(List<MediaFile> list) {
        list.sort(Comparator.comparing(MediaFile::getDateTaken));
        return list;
    }
}