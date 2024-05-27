package org.framework.services.database;

import lombok.Value;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public abstract class CRUDService {

    private static boolean createdTables = false;
    public static void createTables() {
        assert createdTables == false : "createTables() should only be called once";

        String[] createTableSql = {"""
            create table if not exists Players (
                id int auto_increment primary key,
                name varchar(5) not null unique,
                date_created date not null
            )
            """, """
            create table if not exists Levels (
                id int auto_increment primary key,
                number int not null unique,
                song varchar(128) not null,
                note_speed double not null
            )
            """, """
            create table if not exists Scores (
                player_id int not null,
                level_id int not null,
                primary key (player_id, level_id),
                foreign key (player_id) references Players (id),
                foreign key (level_id) references Levels (id),
                value double not null
            )
            """, """
            create table if not exists Notes (
                id int auto_increment primary key,
                direction varchar(8) not null,
                timing double not null,
                level_id int not null,
                foreign key (level_id) references Levels (id)
            )
            """};

        Connection connection = DBConfig.getDBConnection();

        try {
            for (String tableStmt : createTableSql) {
                Statement statement = connection.createStatement();
                statement.execute(tableStmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createdTables = true;
    }

    public static void insertPlayer(String name) {
        String insertPlayerSql = "insert into Players (name, date_created) values (?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            connection.setAutoCommit(false);
            connection.commit();

            PreparedStatement preparedStatement = connection.prepareStatement(insertPlayerSql);

            preparedStatement.setString(1, name.toUpperCase());
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));

            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void insertLevel(int number, String song, double note_speed) {
        String insertLevelSql = "insert into Levels (number, song, note_speed) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            connection.setAutoCommit(false);
            connection.commit();

            PreparedStatement preparedStatement = connection.prepareStatement(insertLevelSql);

            preparedStatement.setInt(1, number);
            preparedStatement.setString(2, song);
            preparedStatement.setDouble(3, note_speed);

            preparedStatement.execute();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void insertScore(double value, String playerName, int levelNumber) throws Exception {
        String insertScoreSql = "insert into Scores (player_id, level_id, value) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            connection.setAutoCommit(false);
            connection.commit();

            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            int playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            int levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(insertScoreSql);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, levelId);
            preparedStatement.setDouble(3, value);

            preparedStatement.execute();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void insertNote(String direction, double timing, int levelNumber) throws Exception {
        String insertNoteSql = "insert into Notes (direction, timing, level_id) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            connection.setAutoCommit(false);
            connection.commit();

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            int levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(insertNoteSql);

            preparedStatement.setString(1, direction);
            preparedStatement.setDouble(2, timing);
            preparedStatement.setInt(3, levelId);

            preparedStatement.execute();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void insertNotes(int levelNumber, String levelMap) throws Exception {
        String[] map = levelMap.split("\n");
        for (int i = 0; i < map.length; i++) {
            String[] timeDir = map[i].split(" -> ");
            insertNote(timeDir[1], Double.parseDouble(timeDir[0]), levelNumber);
        }
    }

    public static Map<String, String> getPlayerByName(String name) {
        String selectPlayerSql = "select * from Players where name = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectPlayerSql);
            preparedStatement.setString(1, name.toUpperCase());

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> playerMap = new HashMap<>();

            if (resultSet.next()) {
                int playerId = resultSet.getInt("id");
                Date playerDateCreated = resultSet.getDate("date_created");

                playerMap.put("id", String.valueOf(playerId));
                playerMap.put("name", name.toUpperCase());
                playerMap.put("date_created", playerDateCreated.toString());

                return playerMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getPlayerById(int id) {
        String selectPlayerSql = "select * from Players where id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectPlayerSql);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> playerMap = new HashMap<>();

            if (resultSet.next()) {
                String playerName = resultSet.getString("name");
                Date playerDateCreated = resultSet.getDate("date_created");

                playerMap.put("id", String.valueOf(id));
                playerMap.put("name", playerName.toUpperCase());
                playerMap.put("date_created", playerDateCreated.toString());

                return playerMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getPlayers() {
        String selectPlayersSql = "select * from Players order by name";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectPlayersSql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> playersList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> playerMap = new HashMap<>();

                int playerId = resultSet.getInt("id");
                String playerName = resultSet.getString("name");
                Date playerDateCreated = resultSet.getDate("date_created");

                playerMap.put("id", String.valueOf(playerId));
                playerMap.put("name", playerName.toUpperCase());
                playerMap.put("date_created", playerDateCreated.toString());

                playersList.add(playerMap);
            }
            return playersList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getLevelByNumber(int number) {
        String selectLevelSql = "select * from Levels where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectLevelSql);
            preparedStatement.setInt(1, number);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> levelMap = new HashMap<>();

            if (resultSet.next()) {
                int levelId = resultSet.getInt("id");
                String levelSong = resultSet.getString("song");
                double noteSpeed = resultSet.getDouble("note_speed");

                levelMap.put("id", String.valueOf(levelId));
                levelMap.put("number", String.valueOf(number));
                levelMap.put("song", levelSong);
                levelMap.put("note_speed", String.valueOf(noteSpeed));

                return levelMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getLevelById(int id) {
        String selectLevelSql = "select * from Levels where id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectLevelSql);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> levelMap = new HashMap<>();

            if (resultSet.next()) {
                int levelNumber = resultSet.getInt("number");
                String levelSong = resultSet.getString("song");
                double noteSpeed = resultSet.getDouble("note_speed");

                levelMap.put("id", String.valueOf(id));
                levelMap.put("number", String.valueOf(levelNumber));
                levelMap.put("song", levelSong);
                levelMap.put("note_speed", String.valueOf(noteSpeed));

                return levelMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getLevels() {
        String selectLevelsSql = "select * from Levels order by number";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectLevelsSql);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Map<String, String>> levelsList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> levelMap = new HashMap<>();

                int levelId = resultSet.getInt("id");
                int levelNumber = resultSet.getInt("number");
                String levelSong = resultSet.getString("song");
                double noteSpeed = resultSet.getDouble("note_speed");

                levelMap.put("id", String.valueOf(levelId));
                levelMap.put("number", String.valueOf(levelNumber));
                levelMap.put("song", levelSong);
                levelMap.put("note_speed", String.valueOf(noteSpeed));

                levelsList.add(levelMap);
            }
            return levelsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getScores() {
        String selectScoresSql = "select * from Scores";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectScoresSql);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, String>> scoresList = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> scoreMap = new HashMap<>();

                int levelId = resultSet.getInt("level_id");
                int playerId = resultSet.getInt("player_id");
                double scoreValue = resultSet.getDouble("value");

                scoreMap.put("player_id", String.valueOf(playerId));
                scoreMap.put("level_id", String.valueOf(levelId));
                scoreMap.put("value", String.valueOf(scoreValue));

                scoresList.add(scoreMap);
            }
            if (scoresList.isEmpty() == false) {
                scoresList.sort((a, b) -> {
                    int levelA = Integer.parseInt(a.get("level_id"));
                    int levelB = Integer.parseInt(b.get("level_id"));

                    Integer levelANumber = Integer.parseInt(getLevelById(levelA).get("number"));
                    Integer levelBNumber = Integer.parseInt(getLevelById(levelB).get("number"));

                    Double scoreA = Double.parseDouble(a.get("value"));
                    Double scoreB = Double.parseDouble(b.get("value"));

                    return levelANumber.compareTo(levelBNumber) * 2 + scoreA.compareTo(scoreB);
                });
            }
            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } 

    public static Map<String, String> getScoreByPlayerAndLevel(String playerName, int levelNumber) throws Exception {
        String selectScoreSql = "select * from Scores where player_id = ? and level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            var playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(selectScoreSql);
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, levelId);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> scoreMap = new HashMap<>();

            if (resultSet.next()) {
                double scoreValue = resultSet.getDouble("value");

                scoreMap.put("player_id", String.valueOf(playerId));
                scoreMap.put("level_id", String.valueOf(levelId));
                scoreMap.put("value", String.valueOf(scoreValue));

                return scoreMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getScoresByPlayer(String playerName) throws Exception {
        String selectScoreSql = "select * from Scores where player_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            var playerId = Integer.parseInt(player.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(selectScoreSql);
            preparedStatement.setInt(1, playerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, String>> scoresList = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> scoreMap = new HashMap<>();

                int levelId = resultSet.getInt("level_id");
                double scoreValue = resultSet.getDouble("value");

                scoreMap.put("player_id", String.valueOf(playerId));
                scoreMap.put("level_id", String.valueOf(levelId));
                scoreMap.put("value", String.valueOf(scoreValue));

                scoresList.add(scoreMap);
            }
            if (scoresList.isEmpty() == false) {
                scoresList.sort((a, b) -> {
                    int levelA = Integer.parseInt(a.get("level_id"));
                    int levelB = Integer.parseInt(b.get("level_id"));

                    Integer levelANumber = Integer.parseInt(getLevelById(levelA).get("number"));
                    Integer levelBNumber = Integer.parseInt(getLevelById(levelB).get("number"));

                    return levelANumber.compareTo(levelBNumber);
                });
            }
            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getScoresByLevel(int levelNumber) throws Exception {
        String selectScoreSql = "select * from Scores where level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(selectScoreSql);
            preparedStatement.setInt(1, levelId);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, String>> scoresList = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, String> scoreMap = new HashMap<>();

                int playerId = resultSet.getInt("player_id");
                double scoreValue = resultSet.getDouble("value");

                scoreMap.put("player_id", String.valueOf(playerId));
                scoreMap.put("level_id", String.valueOf(levelId));
                scoreMap.put("value", String.valueOf(scoreValue));

                scoresList.add(scoreMap);
            }
            if (scoresList.isEmpty() == false) {
                scoresList.sort((a, b) -> {
                    Double scoreA = Double.parseDouble(a.get("value"));
                    Double scoreB = Double.parseDouble(b.get("value"));
                    
                    return scoreA.compareTo(scoreB);
                });
            }
            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getNotes() {
        String selectNotesSql = "select * from Notes";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectNotesSql);

            List<Map<String, String>> notesList = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> noteMap = new HashMap<>();

                String direction = resultSet.getString("direction");
                double timing = resultSet.getDouble("timing");
                int levelId = resultSet.getInt("level_id");
                
                noteMap.put("direction", direction);
                noteMap.put("timing", String.valueOf(timing));
                noteMap.put("level_id", String.valueOf(levelId));

                notesList.add(noteMap);
            }
            if (notesList.isEmpty() == false) {
                notesList.sort((a, b) -> {
                    int levelA = Integer.parseInt(a.get("level_id"));
                    int levelB = Integer.parseInt(b.get("level_id"));

                    Integer levelANumber = Integer.parseInt(getLevelById(levelA).get("number"));
                    Integer levelBNumber = Integer.parseInt(getLevelById(levelB).get("number"));

                    Double noteATiming = Double.parseDouble(a.get("timing"));
                    Double noteBTiming = Double.parseDouble(b.get("timing"));

                    return levelANumber.compareTo(levelBNumber) * 2 + noteATiming.compareTo(noteBTiming);
                });
            }
            return notesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Map<String, String>> getNotesByLevel(int levelNumber) throws Exception {
        String selectNotesSql = "select * from Notes where level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(selectNotesSql);
            preparedStatement.setInt(1, levelId);

            List<Map<String, String>> notesList = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> noteMap = new HashMap<>();

                String direction = resultSet.getString("direction");
                double timing = resultSet.getDouble("timing");

                noteMap.put("direction", direction);
                noteMap.put("timing", String.valueOf(timing));

                notesList.add(noteMap);
            }
            if (notesList.isEmpty() == false) {
                notesList.sort((a, b) -> {
                    Double noteATiming = Double.parseDouble(a.get("timing"));
                    Double noteBTiming = Double.parseDouble(b.get("timing"));

                    return noteATiming.compareTo(noteBTiming);
                });
            }
            return notesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerName(String playerName, String newName) {
        String updatePlayerSql = "update Players set name = ? where name = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updatePlayerSql);
            preparedStatement.setString(1, newName.toUpperCase());
            preparedStatement.setString(2, playerName.toUpperCase());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Player '" + playerName + "' doesn't exist!");
        }
    }

    public static void updateLevelNumber(int levelNumber, int newNumber) {
        String updateLevelSql = "update Levels set number = ? where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelSql);
            preparedStatement.setInt(1, newNumber);
            preparedStatement.setInt(2, levelNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Level with number '" + levelNumber + "' doesn't exist!");
        }
    }

    public static void updateLevelSong(int levelNumber, String song) {
        String updateLevelSongSql = "update Levels set song = ? where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelSongSql);
            preparedStatement.setString(1, song);
            preparedStatement.setInt(2, levelNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Level with number '" + levelNumber + "' doesn't exist!");
        }
    }

    public static void updateLevelNoteSpeed(int levelNumber, double note_speed) {
        String updateLevelNoteSpeedSql = "update Notes set note_speed = ? where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelNoteSpeedSql);
            preparedStatement.setDouble(1, note_speed);
            preparedStatement.setInt(2, levelNumber);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Level with number '" + levelNumber + "' doesn't exist!");
        }
    }

    public static void updatePlayerScoreOnLevel(String playerName, int levelNumber, double newScore) throws Exception {
        String updatePlayerScoreOnLevelSql = "update Scores set value = ? where player_id = ? and level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            var playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(updatePlayerScoreOnLevelSql);
            preparedStatement.setDouble(1, newScore);
            preparedStatement.setInt(2, playerId);
            preparedStatement.setInt(3, levelId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Score for player '" + playerName + "' on level with number '" + levelNumber + "' doesn't exist!");
        }
    }

    public static void updateLevelNotesMap(int levelNumber, String map) throws Exception {
        deleteNotesByLevel(levelNumber);
        insertNotes(levelNumber, map);
    }

    public static Map<String, String> deletePlayer(String playerName) throws Exception {
        String deletePlayerSql = "delete from Players where name = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");

            PreparedStatement preparedStatement = connection.prepareStatement(deletePlayerSql);
            preparedStatement.setString(1, playerName.toUpperCase());

            preparedStatement.execute();
            return player;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Map<String, String>> deletePlayers() throws Exception {
        String deletePlayersSql = "delete from Players";
        Connection connection = DBConfig.getDBConnection();

        try {
            var playersList = getPlayers();
            if (playersList == null)
                throw new Exception("Couldn't find any players.");

            PreparedStatement preparedStatement = connection.prepareStatement(deletePlayersSql);
            preparedStatement.execute();

            return playersList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> deleteLevel(int levelNumber) throws Exception {
        String deleteLevelSql = "delete from Levels where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");

            PreparedStatement preparedStatement = connection.prepareStatement(deleteLevelSql);
            preparedStatement.setInt(1, levelNumber);

            preparedStatement.execute();
            return level;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> deleteLevels() throws Exception {
        String deleteLevelsSql = "delete from Levels";
        Connection connection = DBConfig.getDBConnection();

        try {
            var levelsList = getLevels();
            if (levelsList == null)
                throw new Exception("Couldn't find any levels.");

            PreparedStatement preparedStatement = connection.prepareStatement(deleteLevelsSql);
            preparedStatement.execute();

            return levelsList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> deleteScores() throws Exception {
        String deleteScoresSql = "delete from Scores";
        Connection connection = DBConfig.getDBConnection();

        try {
            var scoresList = getScores();
            if (scoresList == null)
                throw new Exception("Couldn't find any scores.");

            PreparedStatement preparedStatement = connection.prepareStatement(deleteScoresSql);
            preparedStatement.execute();

            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Map<String, String>> deleteScoresByPlayer(String playerName) throws Exception {
        String deleteScoreByPlayerSql = "delete from Scores where player_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var scoresList = getScoresByPlayer(playerName.toUpperCase());
            if (scoresList == null)
                throw new Exception("Couldn't find any scores for the player by name: ");

            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            var playerId = Integer.parseInt(player.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(deleteScoreByPlayerSql);
            preparedStatement.setInt(1, playerId);
            preparedStatement.execute();

            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> deleteScoresByLevel(int levelNumber) throws Exception {
        String deleteScoreByPlayerSql = "delete from Scores where level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            List<Map<String, String>> scoresList = null;
            try {
                scoresList = getScoresByLevel(levelNumber);
            } catch (Exception e) {
                throw new Exception("Couldn't delete score by level number: " + String.format("%d", levelNumber));
            }

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(deleteScoreByPlayerSql);
            preparedStatement.setInt(1, levelId);
            preparedStatement.execute();

            return scoresList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> deleteScore(String playerName, int levelNumber) throws Exception {
        String deleteScoreSql = "delete from Scores where player_id = ? and level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName.toUpperCase());
            if (player == null)
                throw new Exception("No player with name: '" + playerName.toUpperCase() + "'");
            var playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            Map<String, String> score = null;
            try {
                score = getScoreByPlayerAndLevel(playerName.toUpperCase(), levelNumber);
            } catch (Exception e) {
                throw new Exception("Couldn't find score by player name: '" + playerName.toUpperCase() + "' and level number: '" + String.format("%d", levelNumber) + "'");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(deleteScoreSql);
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, levelId);

            preparedStatement.execute();
            return score;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> deleteNotes() throws Exception {
        String deleteScoresSql = "delete from Scores";
        Connection connection = DBConfig.getDBConnection();

        try {
            var notesList = getNotes();
            if (notesList == null)
                throw new Exception("Couldn't find any notes.");

            PreparedStatement preparedStatement = connection.prepareStatement(deleteScoresSql);
            preparedStatement.execute();

            return notesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Map<String, String>> deleteNotesByLevel(int levelNumber) throws Exception {
        String deleteNotesByLevelSql = "delete from Notes where level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            if (level == null)
                throw new Exception("No level with number: '" + String.format("%d", levelNumber) + "'");
            var levelId = Integer.parseInt(level.get("id"));

            List<Map<String, String>> notesList = null;
            try {
                notesList = getNotesByLevel(levelNumber);
            } catch (Exception e) {
                throw new Exception("Couldn't find notes by level number: '" + String.format("%d", levelNumber) + "'");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(deleteNotesByLevelSql);
            preparedStatement.setInt(1, levelId);

            preparedStatement.execute();
            return notesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}