import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Set;

public class FileManager {

    public LinkedHashMap<String, String> readFile(String path) throws IOException {
        FileInputStream inputStream = null;

        Scanner sc = null;
        String line;
        LinkedHashMap<String, String> set = new LinkedHashMap<String, String>();
        String term = "";
        String definition = "";
        boolean useDefinition = false;

        try {
            inputStream = new FileInputStream(path);

            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                term = "";
                definition = "";
                useDefinition = false;

                line = sc.nextLine();

                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c != ':' && !useDefinition) {
                        term = term + c;
                    } else if (c == ':') {
                        useDefinition = true;
                    } else {
                        definition = definition + c;
                    }
                }

                set.put(term, definition);

            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }

            return set;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
            return new LinkedHashMap<String, String>();

        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedHashMap<String, String>();

        } finally {

            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    public void writeFile(LinkedHashMap<String, String> set, String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            Set<String> keys = set.keySet();

            for (String key : keys) {
                out.write(key + ":" + set.get(key) + "\n");
            }

            out.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void createFile(String path) {
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.close();
        } catch (IOException e) {
            System.out.println("io exception in FileManager.createFile");
        }
    }

    public void deleteFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception in FileManager:deleteFile");
        }
    }

    public boolean fileExists(String path) {
        File file = new File(path);
        return file.exists() && !file.isDirectory();
    }
}