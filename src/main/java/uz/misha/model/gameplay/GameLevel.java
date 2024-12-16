package uz.misha.model.gameplay;

import uz.misha.model.util.Sprite;
import uz.misha.util.FileUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class GameLevel {
    Sprite[][] world;

    public GameLevel(String levelPath) {
        loadLevel(levelPath);
    }

    public void loadLevel(String levelPath) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(Objects.requireNonNull(FileUtil.getFileFromResource(levelPath))));
            world = new Sprite[19][19];
            int y = 0;
            String line;
            while ((line = br.readLine()) != null) {
                int x = 0;
                for (char spriteType : line.toCharArray()) {
                    int TILE_WIDTH = 40;
                    int TILE_HEIGHT = 30;
                    if (spriteType == '0') {
                        Image image = FileUtil.getImage("data/world/wall.png");
                        world[y][x] = new Sprite(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, image, true);
                    } else {
                        Image image = FileUtil.getImage("data/world/floor.png");
                        world[y][x] = new Sprite(x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT, image, false);
                    }
                    x++;
                }
                y++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + levelPath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + levelPath);
        }

    }

    public void draw(Graphics g) {
        for (Sprite[] row : world) {
            for (Sprite sprite : row) {
                sprite.draw(g);
            }
        }
    }

    public boolean collides(Point coords, Direction d) {
        if (coords.x + d.x > 18) return true;
        Sprite object = world[coords.y + d.y][coords.x + d.x];
        return object.isWall;
    }

    public boolean collides(Point coords) {
        Sprite object = world[coords.y][coords.x];
        return object.isWall;
    }
}
