package uz.misha.model.gameplay;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point addDirection(Direction d) {
        return new Point(x + d.x, y + d.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Point other)) return false;
        return this.x == other.x && this.y == other.y;
    }
}

