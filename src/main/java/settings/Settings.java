package settings;

import com.google.gson.Gson;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;


public class Settings {

    private static Settings instance;

    private boolean initialized;


    public String ethereumNodeUrl;

    public BigInteger startBlock;

    public String outputFile;

    public BigInteger neededContracts;


    private Settings() { }


    public static Settings instance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        initialized = true;
        String fileContent = new String(getFileData());
        Gson gson = new Gson();
        Settings temporary = gson.fromJson(fileContent, Settings.class);
        this.ethereumNodeUrl = temporary.ethereumNodeUrl;
        this.outputFile = temporary.outputFile;
        this.startBlock = temporary.startBlock;
        this.neededContracts = temporary.neededContracts;
    }

    public boolean isInitialized() {
        return this.initialized;
    }


    private byte[] getFileData() {
        try {
            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            String path = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
            File file = new File(path + "/appsettings.json");

            // Loaded from properties during development and from the same directory of the jar in production
            InputStream inputStream;
            if (file.exists())
                inputStream = new FileInputStream(file);
            else
                inputStream = this.getClass().getResourceAsStream("/settings/appsettings.json");

            if (inputStream != null) {
                byte[] data = toByteArray(inputStream);
                inputStream.close();
                return data;
            }

            System.out.println("Appsettings.json not found");
            return new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }


}
