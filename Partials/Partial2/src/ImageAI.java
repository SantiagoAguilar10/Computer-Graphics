package Partials.Partial2.src;

// Gemini API call by ProcessBuilder & curl
public class ImageAI {

    public String generateImage(String prompt) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                "curl", "-X", "POST", "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",
                "-H", "Content-Type: application/json",
                "-d", "{\"prompt\": {\"text\": \"" + prompt + "\"}, \"responseFormat\": {\"type\": \"url\"}}"
            );
            Process process = builder.start();
            process.waitFor();

            // Read the output from the process (the generated image URL)
            String output = new String(process.getInputStream().readAllBytes());
            return output; // This will contain the URL of the generated image

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
