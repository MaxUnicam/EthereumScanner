package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import settings.Settings;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class ResultBuilder {

    private Settings settings = Settings.instance();

    private Gson gson = new Gson();


    BigInteger appendToResult(List<String> newContracts) {
        List<String> result = new ArrayList<>();
        List<String> savedContracts = getSavedContracts();
        for (String item : newContracts) {
            if (!savedContracts.contains(item)) {
                result.add(item);
            }
        }

        savedContracts.addAll(result);
        System.out.println("Added " + result.size() + " to the list");
        String newJson = gson.toJson(savedContracts);
        updateResults(newJson);
        return BigInteger.valueOf(savedContracts.size());
    }


    private void updateResults(String newContent) {
        try {
            File fout = new File(settings.outputFile);
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(newContent);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getSavedContracts() {
        if (!(new File(settings.outputFile).exists())) {
            return new ArrayList<>();
        }

        try {
            InputStream is = new FileInputStream(settings.outputFile);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            String fileAsString = sb.toString();
            return gson.fromJson(fileAsString, new TypeToken<List<String>>(){}.getType());

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
