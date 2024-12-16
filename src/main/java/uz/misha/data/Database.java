package uz.misha.data;

import uz.misha.model.gameplay.HighScore;
import uz.misha.util.FileUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database {

    private final int maxScores = 10;
    private Connection connection;

    private Statement statement;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement selectStatement;

    public Database() {
        String dbUrl = FileUtil.getProperty("database.url");
        String dbUser = FileUtil.getProperty("database.username");
        String dbPassword = FileUtil.getProperty("database.password");
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            statement = connection.createStatement();
            boolean exists = tableExistsSQL(connection);
            if (!exists) {
                createDatabaseTable();
            }
            prepareStatements();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }

    private void createDatabaseTable() {
        try {
            String CREATE_TABLE = "CREATE TABLE HIGHSCORES(NAME VARCHAR(20), LEVEL INT, TIME VARCHAR)";
            statement.execute(CREATE_TABLE);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    private void prepareStatements() {
        try {
            String INSERT_SCORE = "INSERT INTO HIGHSCORES (NAME, LEVEL, TIME) VALUES (?, ?, ?)";
            insertStatement = connection.prepareStatement(INSERT_SCORE);
            String DELETE_SCORE = "DELETE FROM HIGHSCORES WHERE LEVEL = ? AND TIME = ?";
            deleteStatement = connection.prepareStatement(DELETE_SCORE);
            String SELECT_ALL = "SELECT * FROM HIGHSCORES";
            selectStatement = connection.prepareStatement(SELECT_ALL);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }

    public List<HighScore> getHighScores() {
        List<HighScore> highScores = new ArrayList<>();
        ResultSet results;
        try {
            results = selectStatement.executeQuery();
            while (results.next()) {
                highScores.add(new HighScore(results.getString("NAME"), results.getInt("LEVEL"), results.getDouble("TIME")));
            }
            Collections.sort(highScores);
            return highScores;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return highScores;
    }

    public String[] getColumnNamesArray() {
        return new String[]{"#", "Name", "Levels completed", "Time"};
    }

    public String[][] getDataMatrix() {
        List<HighScore> highScores = getHighScores();
        String[][] dataMatrix = new String[Math.min(highScores.size(), maxScores)][4];
        for (int i = 0; i < dataMatrix.length; i++) {
            HighScore hs = highScores.get(i);
            dataMatrix[i][0] = String.valueOf(i + 1);
            dataMatrix[i][1] = hs.name();
            dataMatrix[i][2] = String.valueOf(hs.level());
            dataMatrix[i][3] = hs.time() + " s";
        }
        return dataMatrix;
    }

    public void putHighScore(String name, int level, double time) {
        List<HighScore> highScores = getHighScores();
        if (highScores.size() < maxScores) {
            insertScore(name, level, time);
        } else {
            int lowestScoreIndex = findLowestScoreIndex(highScores, level, time);
            if (lowestScoreIndex != -1) {
                deleteScore(highScores.get(lowestScoreIndex));
                insertScore(name, level, time);
            }
        }
    }

    private int findLowestScoreIndex(List<HighScore> highScores, int level, double time) {
        int lowestScoreIndex = -1;
        for (int i = highScores.size() - 1; i >= 0; i--) {
            HighScore hs = highScores.get(i);
            if (level > hs.level() || (level == hs.level() && time < hs.time())) {
                lowestScoreIndex = i;
                break;
            }
        }
        return lowestScoreIndex;
    }

    private void insertScore(String name, int level, double time) {
        try {
            insertStatement.setString(1, name);
            insertStatement.setInt(2, level);
            insertStatement.setDouble(3, time);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

    }

    private void deleteScore(HighScore highScore) {
        try {
            deleteStatement.setInt(1, highScore.level());
            deleteStatement.setDouble(2, highScore.time());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }

    }

    private boolean tableExistsSQL(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT count(*)\s
                FROM information_schema.tables\s
                WHERE table_name = ?
                LIMIT 1;""");
        preparedStatement.setString(1, "highscores");

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) != 0;
    }
}