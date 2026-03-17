package src.Classwork05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Example {

    void main() {
        // Example:
        //String[] command = new String[]{"curl", "-X", "POST", "https://postman-echo.com/post", "--data", "foo=bar"};
        String[] command = new String[]{"cmd.exe", "/c", "dir"};

        final ProcessBuilder builder = new ProcessBuilder();
        try {
            final Process process = builder.command(command).start();
            InputStream inputStream = process.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);


            String line;

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            process.destroy();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

