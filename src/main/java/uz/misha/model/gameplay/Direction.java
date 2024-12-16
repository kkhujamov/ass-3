package uz.misha.model.gameplay;

public enum Direction {
    SOUTH(0, 1), SW(-1, 1), WEST(-1, 0), NW(-1, -1), NORTH(0, -1), NE(1, -1), EAST(1, 0), SE(1, 1);

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int x, y;
}
