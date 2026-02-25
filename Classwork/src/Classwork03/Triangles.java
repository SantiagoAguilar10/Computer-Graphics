package src.Classwork03;

import java.io.File; // Abstract representation of file and directory pathnames.
import java.io.FileWriter; // Class used for writing character files to disk.
import java.io.IOException; // Exception class for signaling I/O failures.

public class Triangles {
    public static void main(String[] args) {

        String instructionsSVG = """
        <svg width="400" height="300" viewBox="0 0 400 300" xmlns="http://www.w3.org/2000/svg">
        \r
            <polygon points="0,0 0,300 400,300" fill="#0d00ff" />\r
        \r
            <polygon points="0,0 400,0 400,300" fill="#ff0000" />\r
        </svg>""";

        File outputFile = new File("src/Classwork03/Triangles.svg"); 

        try (FileWriter draw = new FileWriter(outputFile)){
            draw.write(instructionsSVG);
            System.out.println("\nThe image was succesfully created."); 
        } catch (IOException e) {
            System.out.println("\nError creating the file due to " + e.getMessage());
        }
        
    }
    
    
}
