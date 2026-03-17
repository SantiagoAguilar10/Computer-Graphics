package src.Classwork05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class App {

    // https://developers.openai.com/

    public static void main(String[] args) {

        try {

            // Leer archivo text.txt
            BufferedReader fileReader = new BufferedReader(new FileReader("./text.txt"));
            StringBuilder textBuilder = new StringBuilder();

            String line;
            while ((line = fileReader.readLine()) != null) {
                textBuilder.append(line).append("\n");
            }

            fileReader.close();
            String text = textBuilder.toString();

            // Escapar comillas para JSON
            // Si el texto contiene comillas, se deben sustituir para que el JSON sea válido.
            // text = text.replace("\"", "\\\"");


            // Leer archivo .env
            String token = null;

            BufferedReader envReader = new BufferedReader(new FileReader(".env"));

            /*
             * Reglas del archivo .env para que funcione con el código:
             *
             * 1. Debe estar en la carpeta raíz (no dentro de src).
             * 2. La Api Key debe estar en formato:
             *      VARIABLE=valor
             * 3. No debe haber espacios alrededor del '='
             * 4. Ejemplo:
             *      OpenAIToken=sk-abcd....89
             */

            while ((line = envReader.readLine()) != null) {

                if (line.startsWith("OpenAIToken=")) {
                    token = line.substring("OpenAIToken=".length());
                    break;
                }

            }

            envReader.close();

            if (token == null) {
                System.out.println("ERROR: OpenAIToken not found in .env");
                return;
            }


            // Crear JSON para OpenAI
            String jsonData = "{"
                    + "\"model\":\"gpt-4.1-mini\","
                    + "\"input\":\"Translate the following text to English if it is Spanish, or to Spanish if it is English:\\n"
                    + text + "\""
                    + "}";


            // Comando curl
            String[] command = new String[]{
                    "curl",
                    "https://api.openai.com/v1/responses",
                    "-H", "Content-Type: application/json",
                    "-H", "Authorization: Bearer " + token,
                    "-d", jsonData
            };


            // Ejecutar proceso
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();


            // Leer respuesta
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor(); // Esperar a que el proceso termine antes de destruirlo 
            process.destroy(); // Terminar el proceso.

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}