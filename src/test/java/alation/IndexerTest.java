package alation;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IndexerTest {

    String csvFileName = "data.csv";
    List<Pair> pairs = new ArrayList<>(Arrays.asList(
            new Pair("June_Sales",          80),
            new Pair("Sales",               250),
            new Pair("June_Revenue",        30),
            new Pair("Sep_Salary",          120),
            new Pair("Revenue",             100),
            new Pair("April_Sales",         50),
            new Pair("April_Revenue",       60),
            new Pair("Salary_Net",          200),
            new Pair("May_Revenue",         70),
            new Pair("Aug_Sep_Revenue_Net", 110),
            new Pair("May_Sales",           90),
            new Pair("May_Sales",           300)
    ));

    private void writePairsToTextFile(String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(writer);
            for (Pair p : pairs) out.write(p.toString() + "\n");
            out.close();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testIndexerAndNamesTrieAreNotNull() throws Exception {
        writePairsToTextFile(csvFileName);
        Indexer indexer = new Indexer(csvFileName);
        assertNotNull(indexer);
        assertNotNull(indexer.namesTrie);
    }

    @Test
    public void testSerializationDeserializationWork() throws Exception {
        writePairsToTextFile(csvFileName);
        Indexer indexer = new Indexer(csvFileName);
        String trie2JsonString = indexer.toJson();
        assertNotNull(trie2JsonString);
        indexer.toJson("test.json");
        File jsonOutput = new File("test.json");
        assertTrue(jsonOutput.exists());
        Indexer deserializedTrie = new Indexer("test.json");
        String deserializedTrie2JsonString = deserializedTrie.toJson();
        assertEquals(trie2JsonString, deserializedTrie2JsonString);
        jsonOutput.delete();
    }

}