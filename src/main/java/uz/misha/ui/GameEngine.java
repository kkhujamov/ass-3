package uz.misha.ui;

import uz.misha.model.characters.Enemy;
import uz.misha.model.characters.Hero;
import uz.misha.data.Database;
import uz.misha.model.gameplay.Direction;
import uz.misha.model.gameplay.GameLevel;
import uz.misha.model.gameplay.Point;
import uz.misha.model.util.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameEngine extends JPanel {

    private final int SCREEN_WIDTH = 775;
    private final int SCREEN_HEIGHT = 610;
    private final JPanel screen = this;
    private final Database database;

    private boolean paused = false;
    private final Sprite pausedImage = new Sprite(SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 125, 400, 200, new ImageIcon("data/world/paused.png").getImage(), false);

    private int levelNum = 0;
    private GameLevel gameLevel;
    private Hero hero;
    private Enemy enemy;
    private Darkness darkness;
    private JLabel timeLabel;
    private long startTime;
    private Timer timer;
    private long stoppedTime;
    private long elapsedTime;
    private double elapsedTimeInSeconds;

    /**
     * Starts the game, binds a key listener for character movement, starts frame refreshing, starts enemy movement, starts timer, and establishes a connection to the database
     */
    public GameEngine() {
        this.addKeyListener(new CharacterMovement());
        restart(0);
        int FPS = 240;
        Timer newFrameTimer = new Timer(1000 / FPS, new NewFrameListener());
        newFrameTimer.start();
        Timer enemyMovementTimer = new Timer(800, new StartEnemyWalkListener());
        enemyMovementTimer.start();
        database = new Database();
        startTimer();
    }

    /**
     * Starts a timer that updates every 100ms and puts it on the frame
     */
    public void startTimer() {
        timeLabel = new JLabel(" ");
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        startTime = System.currentTimeMillis();
        timer = new Timer(100, e -> {
            elapsedTime = System.currentTimeMillis() - startTime;
            elapsedTimeInSeconds = (double) elapsedTime / 1000;
            timeLabel.setText(elapsedTimeInSeconds + " s");
        });
        timer.start();
    }

    public void restartTimer() {
        startTime = System.currentTimeMillis();
        timer.restart();
    }

    public JLabel getTimer() {
        return timeLabel;
    }

    public Database getDatabase() {
        return database;
    }

    /**
     * Takes level number and generates the appropriate level by call, puts the character to the lower left corner of the level, puts the darkness around the player, and puts the dragon in a random position on the map.
     *
     * @param levelNum Number of the level to be generated.
     */
    public void restart(int levelNum) {
        this.levelNum = levelNum;
        paused = false;
        gameLevel = new GameLevel("data/levels/level" + levelNum + ".txt");
        hero = new Hero();
        darkness = new Darkness();
        darkness.setCoords(hero.getCoordinates());
        enemy = new Enemy(0, 0);
        do {
            int x = (int) Math.round(Math.random() * 16) + 1;
            int y = (int) Math.round(Math.random() * 16) + 1;
            enemy = new Enemy(x * 40, y * 30);
        } while (gameLevel.collides(enemy.getCoordinates()) || enemy.catches(hero));

    }

    /**
     * Checks if the player reached the top right corner of the level.
     *
     * @return a boolean value
     */
    public boolean isOver() {
        Point coords = hero.getCoordinates();
        return coords.x == 18 && coords.y == 1;
    }

    /**
     * Pauses the timer and sets the game state to pause. A second call undoes these.
     */
    public void pause() {
        paused = !paused;
        if (paused) {
            timer.stop();
            stoppedTime = System.currentTimeMillis();
        } else {
            startTime += System.currentTimeMillis() - stoppedTime;
            timer.restart();
        }
    }

    /**
     * Draws all the sprites on the frame
     *
     * @param g Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        gameLevel.draw(g);
        hero.draw(g);
        enemy.draw(g);
        darkness.draw(g);
        if (paused) pausedImage.draw(g);
    }

    /**
     * Used in a Timer object to update the dragon's location every 800ms
     */
    class StartEnemyWalkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!paused) {
                if (gameLevel.collides(enemy.getCoordinates(), enemy.getDirection())) {
                    do {
                        enemy.chooseDirection();
                    } while (gameLevel.collides(enemy.getCoordinates(), enemy.getDirection()));
                }
                enemy.move();
            }
        }
    }

    /**
     * Used in a Timer object to refresh the frame and listens to end of game conditions 240 times a second. Resets the state of the game accordingly.
     */
    class NewFrameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            repaint();
            if (enemy.catches(hero)) {
                timer.stop();
                String name = JOptionPane.showInputDialog(screen, "Enter your name: ", "You couldn't escape...", JOptionPane.INFORMATION_MESSAGE);
                if (name != null) database.putHighScore(name, levelNum, elapsedTimeInSeconds);
                int option = JOptionPane.showConfirmDialog(screen, "Start again?", "Game Over", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    restart(0);
                    restartTimer();
                } else System.exit(0);
            } else if (isOver()) {
                levelNum += 1;
                if (levelNum > 9) {
                    timer.stop();
                    String name = JOptionPane.showInputDialog(screen, "You escaped in " + elapsedTimeInSeconds + " seconds! Enter your name: ", "You won!", JOptionPane.INFORMATION_MESSAGE);
                    if (name != null) database.putHighScore(name, levelNum, elapsedTimeInSeconds);
                    int option = JOptionPane.showConfirmDialog(screen, "Play again?", "Congratulations", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        restart(0);
                        startTime = System.currentTimeMillis();
                        timer.restart();
                    } else System.exit(0);
                } else restart(levelNum);
            }
            repaint();
        }
    }

    /**
     * Listens to the W, A, S, and D keys for player movement. Also repositions the darkness to the player's new position.
     */
    class CharacterMovement extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent key) {
            int kc = key.getKeyCode();
            Point coords = hero.getCoordinates();
            switch (kc) {
                case KeyEvent.VK_A: {
                    if (paused) return;
                    if (!gameLevel.collides(coords, Direction.WEST)) {
                        hero.move(Direction.WEST);
                    }
                    break;
                }
                case KeyEvent.VK_D: {
                    if (paused) return;
                    if (!gameLevel.collides(coords, Direction.EAST)) {
                        hero.move(Direction.EAST);
                    }
                    break;
                }
                case KeyEvent.VK_W: {
                    if (paused) return;
                    if (!gameLevel.collides(coords, Direction.NORTH)) {
                        hero.move(Direction.NORTH);
                    }
                    break;
                }
                case KeyEvent.VK_S: {
                    if (paused) return;
                    if (!gameLevel.collides(coords, Direction.SOUTH)) {
                        hero.move(Direction.SOUTH);
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    pause();
                }
            }
            darkness.setCoords(hero.getCoordinates());
        }
    }
}