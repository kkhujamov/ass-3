package uz.misha.ui;

import uz.misha.data.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class LabyrinthGUI {
    private final GameEngine gameArea;
    private final Database database;
    private JFrame highScoresFrame;

    public LabyrinthGUI() {
        JFrame frame = new JFrame("Labyrinth");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameArea = new GameEngine();
        database = gameArea.getDatabase();
        updateHighScoresFrame();

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menu = getHighScoresMenu();
        menuBar.add(menu);

        gameArea.setFocusable(true);
        frame.getContentPane().add(gameArea, BorderLayout.CENTER);
        frame.getContentPane().add(gameArea.getTimer(), BorderLayout.SOUTH);

        frame.setPreferredSize(new Dimension(775, 650));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenu getHighScoresMenu() {
        JMenu menu = new JMenu("Menu");

        JMenuItem highScores = new JMenuItem("Highscores");
        highScores.addActionListener(e -> {
            gameArea.pause();
            updateHighScoresFrame();
            highScoresFrame.setVisible(true);
        });
        menu.add(highScores);
        JMenuItem pause = new JMenuItem("Pause");
        pause.addActionListener(e -> gameArea.pause());
        menu.add(pause);
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(e -> {
            gameArea.restart(0);
            gameArea.restartTimer();
        });
        menu.add(restart);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);
        return menu;
    }

    /**
     * Creates a new frame which contains a table of the top 10 high scores.
     */
    private void updateHighScoresFrame() {
        highScoresFrame = new JFrame("Highscores");
        JTable table = new JTable(database.getDataMatrix(), database.getColumnNamesArray());
        table.setEnabled(false);
        table.setRowHeight(50);
        JScrollPane sp = new JScrollPane(table);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(230);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(200);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        columnModel.getColumn(0).setCellRenderer(cellRenderer);
        columnModel.getColumn(1).setCellRenderer(cellRenderer);
        columnModel.getColumn(2).setCellRenderer(cellRenderer);
        columnModel.getColumn(3).setCellRenderer(cellRenderer);
        highScoresFrame.add(sp);
        highScoresFrame.setSize(new Dimension(600, 400));
        highScoresFrame.setLocationRelativeTo(null);
    }
}