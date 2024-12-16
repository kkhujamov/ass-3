package uz.misha.model.gameplay;

public record HighScore(String name, int level, double time) implements Comparable<HighScore> {

    @Override
    public String toString() {
        return name + ": " + level + " level " + time + " seconds";
    }

    @Override
    public int compareTo(HighScore o) {
        int levelDif = -Integer.compare(this.level, o.level);
        int timeDif = Double.compare(this.time, o.time);
        int nameDif = this.name.compareTo(o.name);
        if (levelDif != 0) {
            return levelDif;
        } else if (timeDif != 0) {
            return timeDif;
        } else return nameDif;
    }
}
