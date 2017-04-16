package alation;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryServer {

    private final static Logger LOGGER = Logger.getLogger(QueryServer.class.getName());
    private Indexer indexer;

    public QueryServer(Indexer indexer) {
        LOGGER.log(Level.INFO, "inside the constructor");
        this.indexer = indexer;
    }

    private Set<Pair> getMatchingQueries(String prefix) {
        prefix = prefix.toLowerCase().trim();
        Collection<Queue<Pair>> allMatching = indexer.namesTrie.subMap(prefix, prefix + Character.MAX_VALUE).values();
        LOGGER.log(Level.FINE, "list of all matching queries (may include duplicates) for keyword \"" + prefix + "\": " + allMatching);
        Set<Pair> uniqueMatching = new HashSet<>();
        for (Queue queue : allMatching) uniqueMatching.addAll(queue);
        LOGGER.log(Level.INFO, "list of all unique matching queries for keyword \"" + prefix + "\": " + uniqueMatching);
        return uniqueMatching;
    }

    public String[] getTopMatches(String prefix, int count) {
        Set<Pair> matchingQueries = getMatchingQueries(prefix);
        Queue<Pair> pq = new PriorityQueue<>();
        for (Pair next : matchingQueries) {
            LOGGER.log(Level.FINEST, "pair " + next + " offered to the priority queue");
            pq.add(next);
            if (pq.size() > count) pq.remove();
        }
        LOGGER.log(Level.INFO, "top " + count + " matching names for query \"" + prefix + "\": " + pq);
        Pair[] pairs = new Pair[pq.size()];
        Arrays.sort(pq.toArray(pairs), Collections.reverseOrder());
        String[] names = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) names[i] = pairs[i].name;
        return names;
    }
}
