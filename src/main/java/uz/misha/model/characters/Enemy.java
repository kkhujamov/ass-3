package uz.misha.model.characters;

import uz.misha.model.gameplay.Direction;
import uz.misha.util.FileUtil;
import uz.misha.model.gameplay.Point;
import uz.misha.model.util.Sprite;

import javax.swing.*;

public class Enemy extends Sprite {

    private Direction direction;

    /**
     * Sets the position and the sprite of the dragon. Also starts an animation for the sprite of the dragon.
     */
    public Enemy(int x, int y) {
        super(x, y, 40, 30, FileUtil.getImage("data/enemy/walkS/enemy_walkA_0000.png"), false);
        chooseDirection();
        Timer animation = new Timer(1000 / 28, e -> {
            int frame = 0;
            frame = (frame + 1) % 14;
            String stringPath = "0" + frame;
            image = FileUtil.getImage("data/enemy/walkS/enemy_walkA_00" + stringPath + ".png");
        });
        animation.start();
    }

    public void move() {
        this.x += direction.x * 40;
        this.y += direction.y * 30;
    }

    /**
     * Randomly chooses a direction
     */
    public void chooseDirection() {
        Direction direction = Direction.SOUTH;
        int randomDirection = (int) Math.round(Math.random() * 3) + 1;
        direction = switch (randomDirection) {
            case 1 -> Direction.SOUTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.NORTH;
            case 4 -> Direction.WEST;
            default -> direction;
        };
        this.direction = direction;
    }

    /**
     * Checks if the coordinates of the neighboring tiles match with the player's position.
     *
     * @param hero The player
     * @return a boolean value
     */
    public boolean catches(Hero hero) {
        Point heroCoords = hero.getCoordinates();
        Point enemyCoords = this.getCoordinates();

        return enemyCoords.equals(heroCoords) ||
               enemyCoords.addDirection(Direction.SOUTH).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.SW).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.WEST).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.NW).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.NORTH).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.NE).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.EAST).equals(heroCoords) ||
               enemyCoords.addDirection(Direction.SE).equals(heroCoords);
    }

    public Direction getDirection() {
        return direction;
    }
}
