package alation;

public class Pair implements Comparable<Pair> {
    String name;
    int score;

    public Pair(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return this.name + "," + this.score;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) return false;
        Pair p = (Pair) obj;
        return this.name.equals(p.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.name.hashCode();
        hash = 37 * hash + this.score;
        return hash;
    }

    @Override
    public int compareTo(Pair p) {
        return this.score - p.score;
    }
}
