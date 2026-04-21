package Partials.Partial2.src.GeminiRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

public class GeminiService {

    private final String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Método principal que:
     * 1. Envía el archivo a Gemini
     * 2. Obtiene el JSON
     * 3. Extrae el texto de la respuesta
     */
    public String describeMedia(File file) throws IOException, InterruptedException {
        String jsonResponse = sendRequest(file);
        return extractText(jsonResponse);
    }

    /**
     * Construye y ejecuta la petición con curl usando ProcessBuilder
     */
    private String sendRequest(File file) throws IOException, InterruptedException {

        String mimeType = getMimeType(file);
        String base64 = encodeFileToBase64(file);

        String payload = "{"
                + "\"contents\":[{"
                + "\"parts\":["
                + "{\"text\":\"Describe briefly this media\"},"
                + "{"
                + "\"inline_data\":{"
                + "\"mime_type\":\"" + mimeType + "\","
                + "\"data\":\"" + base64 + "\""
                + "}"
                + "}"
                + "]"
                + "}]"
                + "}";

        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-X", "POST",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                "-H", "Content-Type: application/json",
                "-d", payload
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        process.waitFor();

        return response.toString();
    }

    /**
     * Detecta el MIME type básico
     */
    private String getMimeType(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".mp4")) return "video/mp4";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }

    /**
     * Convierte archivo a Base64
     */
    private String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * Extrae el texto desde el JSON de Gemini SIN usar librerías externas
     */
    private String extractText(String json) {

        String key = "\"text\": \"";
        int start = json.indexOf(key);

        if (start == -1) {
            return "No description found";
        }

        start += key.length();

        StringBuilder result = new StringBuilder();

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            // Detecta cierre de string (ignorando comillas escapadas)
            if (c == '"' && json.charAt(i - 1) != '\\') {
                break;
            }

            result.append(c);
        }

        return result.toString()
                .replace("\\n", "\n")
                .replace("\\\"", "\"");
    }

    public File generateSummaryImage(Map<String, String> descriptions, String outputPath) 

        throws IOException, InterruptedException {

            String prompt = buildSummaryPrompt(descriptions);

            String payload = "{"
                    + "\"contents\":[{"
                    + "\"parts\":["
                    + "{ \"text\": \"" + prompt + "\" }"
                    + "]"
                    + "}]"
                    + "}";

            ProcessBuilder pb = new ProcessBuilder(
                    "curl",
                    "-X", "POST",
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                    "-H", "Content-Type: application/json",
                    "-d", payload
            );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            process.waitFor();

            String base64Image = extractImageBase64(response.toString());

            return saveImage(base64Image, outputPath);
        }
    
    private String buildSummaryPrompt(Map<String, String> descriptions) {

        StringBuilder sb = new StringBuilder();

        sb.append("Based on the following descriptions, create a single image that represents the essence of all scenes:\n\n");

        for (String desc : descriptions.values()) {
            sb.append("- ").append(desc).append("\n");
        }

        sb.append("\nThe image should be symbolic, visually appealing, and cohesive.");

        return escapeJson(sb.toString());
    }

    private String extractImageBase64(String json) {

        String key = "\"data\": \"";
        int start = json.indexOf(key);

        if (start == -1) {
            throw new RuntimeException("No image found in response");
        }

        start += key.length();

        StringBuilder result = new StringBuilder();

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && json.charAt(i - 1) != '\\') {
                break;
            }

            result.append(c);
        }

        return result.toString();
    }

    private File saveImage(String base64, String outputPath) throws IOException {

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        File outputFile = new File(outputPath);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(imageBytes);
        }

        return outputFile;
    }


    private String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "");
    }
    
}