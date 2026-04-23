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

    /**
     * Sends a media file to Gemini for description, with up to 3 retries if an error is detected in the response.
     * @param file
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String describeMedia(File file) throws IOException, InterruptedException {

        int attempts = 0;

        while (attempts < 3) {
            String json = sendRequest(file);
            //System.out.println("RAW for " + file.getName() + ": " + json); // Debug

            if (!json.contains("\"error\": \"")) {
                return extractText(json);
            }

            System.out.println("Retrying... attempt " + (attempts + 1));
            Thread.sleep(2500); // Stay polite and patient with Gemini
            attempts++;
        }

        return "Failed to get a valid response after 3 attempts.";

    }

    /**
     * Builds and executes curl with ProcessBuilder, returns raw JSON response as String
     * @param file
     * @return
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
        // System.out.println("RAW Gemini Response: "+ result);  //Debug
        return result;
    }

    /**
     * Detects the MIME type based on file extension
     * MIME: Multipurpose Internet Mail Extensions, standard way to indicate file types on the web.
     * @param file
     * @return
     */
    private String getMimeType(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".mp4")) return "video/mp4";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";

        return "application/octet-stream";
    }

    /**
     * Encodes a file to Base64 format
     * @param file
     * @return
     * @throws IOException
     */
    private String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    /**
     * Extracts the "text" field from Gemini's JSON response, handling escaped characters
     * @param json
     * @return
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

    /**
     * Generates a summary image based on the provided descriptions using Gemini's image generation capabilities. 
     * Returns the saved image file or null if generation failed.
     * @param descriptions
     * @param outputPath
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public File generateSummaryImage(Map<String, String> descriptions, String outputPath) 

        throws IOException, InterruptedException {

            String prompt = buildSummaryPrompt(descriptions);

            String payload = "{"
                + "\"contents\":[{"
                + "\"parts\":["
                + "{ \"text\": \"" + prompt + "\" }"
                + "]"
                + "}],"
                + "\"generationConfig\":{"
                + "\"responseModalities\":[\"TEXT\",\"IMAGE\"]"  // ← required for image output
                + "}"
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

            // Debug
            /* 
            System.out.println("IMAGE RESPONSE:");
            System.out.println(response.toString());
            */

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
    
    

        /**
         * Builds a prompt for Gemini's image generation based on the provided scene descriptions. 
         * The prompt instructs Gemini to create a single, cohesive image that captures the essence of all scenes, while ignoring any failed or empty descriptions.
         * @param descriptions
         * @return
         */
    private String buildSummaryPrompt(Map<String, String> descriptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Based on the following descriptions, create a single image that represents the essence of all scenes:\n\n");

        for (String desc : descriptions.values()) {
            // Skip failed or empty descriptions
            if (desc == null 
                || desc.startsWith("Failed") 
                || desc.equals("No description found")) {
                continue;
            }
            sb.append("- ").append(desc).append("\n");
        }

        sb.append("\nThe image should be symbolic, visually appealing, and cohesive.");
        return escapeJson(sb.toString());
    }

    /**
     * Generates a short inspirational quote based on the provided scene descriptions using Gemini.
     * @param descriptions
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String generateQuote(Map<String, String> descriptions) 
        throws IOException, InterruptedException {

            StringBuilder prompt = new StringBuilder();
            prompt.append("Based on these scene descriptions from a video, generate one short, ");
            prompt.append("inspirational or motivational quote that captures the spirit of the journey. ");
            prompt.append("Return ONLY the quote itself, no author, no quotation marks, no explanation.\\n\\n");

            for (String desc : descriptions.values()) {
                if (desc == null
                    || desc.startsWith("Failed")
                    || desc.equals("No description found")) continue;
                prompt.append("- ").append(desc).append("\\n");
            }

            String payload = "{"
                + "\"contents\":[{"
                + "\"parts\":["
                + "{ \"text\": \"" + escapeJson(prompt.toString()) + "\" }"
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
            while ((line = reader.readLine()) != null) response.append(line);
            process.waitFor();
            System.out.println("Quote exit code: " + process.exitValue());
            System.out.println("Quote RAW: " + response.toString());
            tempJson.delete();

            String json = response.toString();
            System.out.println("\n \n \nQUOTE RESPONSE:");
            // System.out.println("Quote RAW: " + json); // Debug

            if (json.contains("\"error\"")) {
                System.out.println("Quote generation failed.");
                return null;
            }

            return extractText(json);
        }

    private String extractImageBase64(String json) {

        if (json.contains("\"error\"")) {
            /*
            System.out.println("Image API ERROR: " + json);
            */
            return null;
        }

        String key = "\"data\": \"";
        int start = json.indexOf(key);
        if (start == -1) {
            /*/
            System.out.println("No image in response. RAW: " + json); // Debug
            */
            return null;
        }

        start = json.indexOf(key, start);
            if (start == -1) {
                // System.out.println("No data field in inlineData. RAW: " + json); // Debug
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
    /**
     * Saves a Base64-encoded image string to a file. 
     * Validates that the decoded data is a PNG or JPEG image before saving. 
     * Returns the saved File object or null if the data is invalid.
     * @param base64
     * @param outputPath
     * @return
     * @throws IOException
     */
    private File saveImage(String base64, String outputPath) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64);

        boolean isPng  = imageBytes[0] == (byte)0x89 && imageBytes[1] == (byte)0x50;
        boolean isJpeg = imageBytes[0] == (byte)0xFF && imageBytes[1] == (byte)0xD8;

        if (!isPng && !isJpeg) {
            System.out.println("Invalid image data received:");
            System.out.println(new String(imageBytes, 0, Math.min(200, imageBytes.length)));
            return null;
        }

        // Use correct extension
        if (isJpeg && outputPath.endsWith(".png")) {
            outputPath = outputPath.replace(".png", ".jpg");
        }

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