package uz.misha.model.characters;

import uz.misha.model.gameplay.Direction;
import uz.misha.util.FileUtil;
import uz.misha.model.util.Sprite;

import javax.swing.*;

public class Hero extends Sprite {

    public Hero() {
        super(40, 510, 40, 30, new ImageIcon(FileUtil.getFileUrl("data/hero/idleS/hero_idleA_0000.png")).getImage(), false);
        Timer animation = new Timer(1000 / 34, e -> {
            int frame = 0;
            frame = (frame + 1) % 17;
            String stringPath = "0" + frame;
            image = new ImageIcon(FileUtil.getFileUrl("data/hero/idleS/hero_idleA_00" + stringPath + ".png")).getImage();
        });
        animation.start();
    }

    public void move(Direction d) {
        this.x += d.x * 40;
        this.y += d.y * 30;
    }
}
