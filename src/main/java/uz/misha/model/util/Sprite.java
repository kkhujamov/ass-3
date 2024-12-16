package uz.misha.model.util;

import uz.misha.model.gameplay.Point;

import java.awt.*;


public class Sprite {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image image;
    public boolean isWall;

    public Sprite(int x, int y, int width, int height, Image image, boolean isWall) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.isWall = isWall;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
    }

    public Point getCoordinates() {
        return new Point(x / 40, y / 30);
    }
}
