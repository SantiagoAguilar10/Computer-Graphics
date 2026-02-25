package src.Classwork03;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Landscape {

    public static void main(String[] args) {
        
        String instructionsSVG = """
        <svg width="400" height="300" viewBox="0 0 400 300" xmlns="http://www.w3.org/2000/svg">

        // Background Fill
        <rect x="0" y="0" width="400" height="300" fill="#ffffff" />

        // Sun Rays
        <path d="M 15 61.5 L 135 63.5" stroke="#ff6200b3" fill="none" stroke-width="2" />
        <path d="M 74 2.5 L 76 122.5" stroke="#ff6200b3" fill="none" stroke-width="2" />
        <path d="M 32.5 25 L 115 102.5" stroke="#ff6200b3" fill="none" stroke-width="2" />
        <path d="M 32.5 102.5 L 115 25" stroke="#ff6200b3" fill="none" stroke-width="2" />

        // Sun (Circle)
        <circle cx="75" cy="62.5" r="35" fill="#fffb00" />

        // Grass
        <rect x="0" y="237.5" width="400" height="62.5" fill="#00ff00" />

        // Hills (Quadratic Bezier Curves)
        <path d="M 0 240 Q 25 175 50 240 Z" fill="#00ff00" />
        <path d="M 50 240 Q 75 175 100 240 Z" fill="#00ff00" />
        <path d="M 100 240 Q 125 175 150 240 Z" fill="#00ff00" />
        <path d="M 150 240 Q 175 175 200 240 Z" fill="#00ff00" />
        <path d="M 200 240 Q 225 175 250 240 Z" fill="#00ff00" />
        <path d="M 250 240 Q 275 175 300 240 Z" fill="#00ff00" />
        <path d="M 300 240 Q 325 175 350 240 Z" fill="#00ff00" />
        <path d="M 350 240 Q 375 175 400 240 Z" fill="#00ff00" />
        </svg>

        """;

        File outputFile = new File("src/Classwork03/Landscape.svg");
        try (FileWriter draw = new FileWriter(outputFile)){
            draw.write(instructionsSVG);
            System.out.println("\nThe image was succesfully created."); 
        } catch (IOException e) {
            System.out.println("\nError creating the file due to " + e.getMessage());
        }
    }
    
}
