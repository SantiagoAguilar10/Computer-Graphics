package Partials.Partial2.src.GeminiRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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


    public String describeMedia(File file) throws IOException, InterruptedException {

        int attempts = 0;

        while (attempts < 3) {
            String json = sendRequest(file);

            if (!json.contains("\"error\": \"")) {
                return extractText(json);
            }

            System.out.println("Retrying... attempt " + (attempts + 1));
            Thread.sleep(2000); // Stay polite and patient with Gemini
            attempts++;
        }

        return "Failed to get a valid response after 3 attempts.";

    }

    /**
     * Builds and executes curl with ProcessBuilder, returns raw JSON response as String
     */
    private String sendRequest(File file) throws IOException, InterruptedException {

        String mimeType = getMimeType(file);
        String base64 = encodeFileToBase64(file);

        String payload = "{"
                + "\"contents\":[{"
                + "\"parts\":["
                + "{\"text\":\"Describe this media in 1 short sentence\"},"
                + "{"
                + "\"inline_data\":{"
                + "\"mime_type\":\"" + mimeType + "\","
                + "\"data\":\"" + base64 + "\""
                + "}"
                + "}"
                + "]"
                + "}]"
                + "}";

        //Create Temporal JSON file for curl
        File tempJson = File.createTempFile("request", ".json");

        try (FileWriter writer = new FileWriter(tempJson)) {
            writer.write(payload);
        }

        //Usar archivo en curl
        ProcessBuilder pb = new ProcessBuilder(
                "curl",
                "-X", "POST",
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                "-H", "Content-Type: application/json",
                "-d", "@" + tempJson.getAbsolutePath()
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
        tempJson.delete();

        String result = response.toString();
        System.out.println("RAW Gemini Response: "+ result);
        return result;
    }

    /**
     * Detects the MIME type based on file extension
     * MIME: Multipurpose Internet Mail Extensions, standard way to indicate file types on the web.
     */
    private String getMimeType(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".mp4")) return "video/mp4";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }

    /**
     * Base 64
     */
    private String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * Extracts the "text" field from Gemini's JSON response, handling escaped characters
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

            // Detects the closing quote that is not escaped
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

            File tempJson = File.createTempFile("request", ".json");
            try (FileWriter writer = new FileWriter(tempJson)) {
                writer.write(payload);
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "curl",
                    "-X", "POST",
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey,
                    "-H", "Content-Type: application/json",
                    "-d", "@" + tempJson.getAbsolutePath()
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
            tempJson.delete();

            String jsonResponse = response.toString();
            System.out.println("IMAGE RESPONSE:");
            System.out.println(response.toString());

            if (jsonResponse.contains("\"error\": \"")) {
                System.out.println("Error generating image");
                return null;
            }

            String base64Image = extractImageBase64(response.toString());

            if (base64Image == null) {
                System.out.println("No image data found in response");
                return null;
            }
            
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

        if (json.contains("\"error\"")) {
            System.out.println("Image API ERROR: " + json);
            return null;
        }

        String key = "\"data\": \"";
        int start = json.indexOf(key);

        if (start == -1) {
            System.out.println("No image in response. RAW: " + json);
            return null;
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