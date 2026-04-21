package Partials.Partial2.src.GeminiRequest;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class DescriptionStore {

    /**
     * Guarda descripciones en formato JSON
     */
    public void saveAsJson(Map<String, String> descriptions, String path) throws IOException {

        FileWriter writer = new FileWriter(path);

        writer.write("{\n");

        int count = 0;
        int size = descriptions.size();

        for (Map.Entry<String, String> entry : descriptions.entrySet()) {

            String file = escapeJson(entry.getKey());
            String description = escapeJson(entry.getValue());

            writer.write("  \"" + file + "\": \"" + description + "\"");

            count++;
            if (count < size) {
                writer.write(",");
            }

            writer.write("\n");
        }

        writer.write("}");

        writer.close();
    }

    /**
     * Escapa caracteres peligrosos para JSON
     */
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}