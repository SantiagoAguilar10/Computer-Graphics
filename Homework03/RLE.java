public class RLE {

    // Value-Count Pair: This class represents a single run of pixels in the RLE compression scheme. 
    // It contains two fields: 'value', which holds the pixel value (in this case, the grayscale value), and 'count', which indicates how many consecutive pixels have that value. 
    // The constructor initializes these fields when a new RLE object is created.
    public int value;
    public int count;

    public RLE(int value, int count) {
        this.value = value;
        this.count = count;
    }
}