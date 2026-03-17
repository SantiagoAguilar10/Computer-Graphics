package src.Classwork05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// Este código, a diferencia del App.java, obtiene la API Key desde una variable de entorno en lugar de un archivo .env.
// En caso de que no funcione App.java, es más recomensable usar este código.

public class App2 {

    public static void main(String[] args) {

        try {

            //Leer archivo text.txt
            BufferedReader fileReader = new BufferedReader(new FileReader("./text.txt"));
            StringBuilder textBuilder = new StringBuilder();

            String line;
            while ((line = fileReader.readLine()) != null) {
                textBuilder.append(line).append("\n");
            }

            fileReader.close();
            String text = textBuilder.toString();

            // Escapar comillas para JSON
            text = text.replace("\"", "\\\"");

            //Obtener API Key desde variable de entorno
            String token = System.getenv("OpenAIToken");

            if (token == null) {
                System.out.println("ERROR: Environment variable OpenAIToken not found.");
                return;
            }


            // Curl se divide en partes para evitar problemas con las comillas y el formato del JSON.
            
            //Crear JSON
            String jsonData = "{"
                    + "\"model\":\"gpt-4.1-mini\","
                    + "\"input\":\"Translate the following text to English if it is in Spanish, or to Spanish if it is in English:\\n"
                    + text + "\""
                    + "}";

            //Comando curl
            String[] command = new String[]{
                    "curl",
                    "https://api.openai.com/v1/responses",
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + token,
                    "-d", jsonData
            };

            //Ejecutar proceso
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();

            //Leer respuesta
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor(); // Esperar a que el proceso termine antes de destruirlo
            process.destroy();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}