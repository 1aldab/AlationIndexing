package alation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexer {

    private final static Logger LOGGER = Logger.getLogger(Indexer.class.getName());
    SortedMap<String, List<Pair>> namesTrie;

    public Indexer(String pathToFile) {
        String extension = pathToFile.substring(pathToFile.lastIndexOf(".") + 1);
        if (extension.equals("json")) {
            LOGGER.log(Level.INFO, "input file \"" + pathToFile + ": attempting to read JSON");
            deserializeFromJson(pathToFile);
        } else {
            LOGGER.log(Level.INFO, "input file \"" + pathToFile + ": attempting to read CSV");
            deserializeFromText(pathToFile);
        }
    }

    private void deserializeFromText(String pathToTextFile) {
        namesTrie = new TreeMap<>();
        try {
            LOGGER.log(Level.INFO, "building namesTrie from csv file");
            FileReader reader = new FileReader(pathToTextFile);
            BufferedReader in = new BufferedReader(reader);
            String nextPair;
            while ((nextPair = in.readLine()) != null) {
                LOGGER.log(Level.FINEST, "pair " + nextPair + " read from the file");
                String[] parts = nextPair.split(",");
                Pair p = new Pair(parts[0], Integer.parseInt(parts[1]));
                LOGGER.log(Level.FINEST, "adding pair " + p + " to the namesTrie");
                for (String token : parts[0].toLowerCase().trim().split("_")) {
                    if (!namesTrie.containsKey(token))
                        namesTrie.put(token, new ArrayList<Pair>());
                    namesTrie.get(token).add(p);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "exception occurred reading file " + pathToTextFile);
            ex.printStackTrace();
        }
    }

    private void deserializeFromJson(String pathToJsonFile) {
        Gson gson = new Gson();
        Type pairType = new TypeToken<TreeMap<String, List<Pair>>>() {}.getType();
        try {
            BufferedReader jsonReader = new BufferedReader(new FileReader(pathToJsonFile));
            LOGGER.log(Level.INFO, "reconstructing namesTrie from JSON file");
            namesTrie = gson.fromJson(jsonReader, pairType);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "could not find JSON file " + pathToJsonFile);
            ex.printStackTrace();
        }
    }

    public String toJson() {
        LOGGER.log(Level.INFO, "inside toJson(): converting namesTrie to a JSON string");
        Gson gson = new Gson();
        return gson.toJson(namesTrie);
    }

    public void toJson(String pathToJsonFile) {
        LOGGER.log(Level.INFO, "inside toJson(): writing namesTrie to external file: " + pathToJsonFile);
        Gson gson = new Gson();
        try {
            Writer jsonWriter = new FileWriter(pathToJsonFile);
            gson.toJson(namesTrie, jsonWriter);
            LOGGER.log(Level.FINE, "wrote namesTrie to file" + pathToJsonFile);
            jsonWriter.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "could not write to file " + pathToJsonFile);
            ex.printStackTrace();
        }
    }
}
