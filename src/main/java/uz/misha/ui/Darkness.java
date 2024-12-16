package uz.misha.ui;

import uz.misha.model.gameplay.Point;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Darkness {

    /**
     * Coordinates of the upper left corner of the ellipse
     */
    private int x;
    private int y;

    public void setCoords(Point p) {
        this.x = (p.x - 3) * 40 + 10;
        this.y = (p.y - 3) * 30;
    }

    /**
     * Calculates the area of an ellipse on the player with a radius of 3 tiles and then finds the difference of the whole frame area and the ellipse area and fills it with black.
     *
     * @param g Graphics object
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int SCREEN_WIDTH = 775;
        int SCREEN_HEIGHT = 615;
        Area outer = new Area(new Rectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fill(outer);
        Ellipse2D.Double inner = new Ellipse2D.Double(x, y, (double) SCREEN_WIDTH / 3, (double) SCREEN_HEIGHT / 3);
        outer.subtract(new Area(inner)); // remove the ellipse from the original area
        g2d.setColor(Color.BLACK);
        g2d.fill(outer);
    }

}
