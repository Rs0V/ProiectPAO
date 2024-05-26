package org.framework.services.database;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CRUDService {

    private static boolean createdTables = false;
    public static void createTables() {
        assert createdTables == false : "createTables() should only be called once";

        String[] createTableSql = {"""
            create table if not exists Players (
                id int auto_increment primary key,
                name varchar(5) not null,
                date_created date not null
            )
            """, """
            create table if not exists Levels (
                id int auto_increment primary key,
                number int not null,
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
            PreparedStatement preparedStatement = connection.prepareStatement(insertPlayerSql);
            preparedStatement.setString(1, name);
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertLevel(int number, String song, double note_speed) {
        String insertLevelSql = "insert into Levels (number, song, note_speed) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertLevelSql);
            preparedStatement.setInt(1, number);
            preparedStatement.setString(2, song);
            preparedStatement.setDouble(3, note_speed);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertScore(double value, String playerName, int levelNumber) {
        String insertScoreSql = "insert into Scores (player_id, level_id, value) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName);
            assert player != null : "Player not found";
            int playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            int levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(insertScoreSql);
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, levelId);
            preparedStatement.setDouble(3, value);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertNote(String direction, double timing, int levelNumber) {
        String insertNoteSql = "insert into Notes (direction, timing, level_id) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            int levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(insertNoteSql);
            preparedStatement.setString(1, direction);
            preparedStatement.setDouble(2, timing);
            preparedStatement.setInt(3, levelId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertNotes(int levelNumber, String levelMap) {
        String insertNoteSql = "insert into Notes (direction, timing, level_id) values (?, ?, ?)";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            int levelId = Integer.parseInt(level.get("id"));

            String[] map = levelMap.split("\n");
            for (int i = 0; i < map.length; i++) {
                String[] timeDir = map[i].split(" -> ");
                PreparedStatement preparedStatement = connection.prepareStatement(insertNoteSql);

                preparedStatement.setString(1, timeDir[1]);
                preparedStatement.setDouble(2, Double.parseDouble(timeDir[0]));
                preparedStatement.setInt(3, levelId);

                preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getPlayerByName(String name) {
        String selectPlayerSql = "select * from Players where name = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectPlayerSql);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> playerMap = new HashMap<>();

            while (resultSet.next()) {
                int playerId = resultSet.getInt("id");
                String playerName = resultSet.getString("name");
                Date playerDateCreated = resultSet.getDate("date_created");

                playerMap.put("id", String.valueOf(playerId));
                playerMap.put("name", playerName);
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
                playerMap.put("name", playerName);
                playerMap.put("date_created", playerDateCreated.toString());

                return playerMap;
            }
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

            int levelId = 0; String levelSong = ""; double noteSpeed = 0;
            if (resultSet.next()) {
                levelId = resultSet.getInt("id");
                levelSong = resultSet.getString("song");
                noteSpeed = resultSet.getDouble("note_speed");
            }

            levelMap.put("id", String.valueOf(levelId));
            levelMap.put("number", String.valueOf(number));
            levelMap.put("song", levelSong);
            levelMap.put("note_speed", String.valueOf(noteSpeed));

            return levelMap;
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

            int levelNumber = 0; String levelSong = ""; double noteSpeed = 0;
            if (resultSet.next()) {
                levelNumber = resultSet.getInt("number");
                levelSong = resultSet.getString("song");
                noteSpeed = resultSet.getDouble("note_speed");
            }

            levelMap.put("id", String.valueOf(id));
            levelMap.put("number", String.valueOf(levelNumber));
            levelMap.put("song", levelSong);
            levelMap.put("note_speed", String.valueOf(noteSpeed));

            return levelMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getScoreByPlayerAndLevel(String playerName, int levelNumber) {
        String selectScoreSql = "select * from Scores where player_id = ? and level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName);
            assert player != null : "Player not found";
            var playerId = Integer.parseInt(player.get("id"));

            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(selectScoreSql);
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, levelId);

            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, String> scoreMap = new HashMap<>();

            double scoreValue = 0;
            if (resultSet.next()) {
                scoreValue = resultSet.getDouble("value");
            }

            scoreMap.put("player_id", String.valueOf(playerId));
            scoreMap.put("level_id", String.valueOf(levelId));
            scoreMap.put("value", String.valueOf(scoreValue));

            return scoreMap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, String>> getNotesByLevel(int levelNumber) {
        String selectNotesSql = "select * from Notes where level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
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
            return notesList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateLevelNumber(int levelNumber, int newNumber) {
        String updateLevelSql = "update Levels set number = ? where id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelSql);
            preparedStatement.setInt(1, newNumber);
            preparedStatement.setInt(2, levelId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLevelSong(int levelNumber, String song) {
        String updateLevelSongSql = "update Levels set song = ? where id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelSongSql);
            preparedStatement.setString(1, song);
            preparedStatement.setInt(2, levelId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateLevelNoteSpeed(int levelNumber, double note_speed) {
        String updateLevelNoteSpeedSql = "update Notes set note_speed = ? where id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelNoteSpeedSql);
            preparedStatement.setDouble(1, note_speed);
            preparedStatement.setInt(2, levelId);

            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> deletePlayer(String playerName) {
        String deletePlayerSql = "delete from Players where name = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var player = getPlayerByName(playerName);
            assert player != null : "Player not found";
            var playerId = Integer.parseInt(player.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement("select * from Scores where player_id = ?");
            preparedStatement.setInt(1, playerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int levelId = Integer.parseInt(resultSet.getString("level_id"));
                var level = getLevelById(levelId);
                assert level != null : "Level not found";
                deleteScore(playerName, Integer.parseInt(level.get("number")));
            }

            preparedStatement = connection.prepareStatement(deletePlayerSql);
            preparedStatement.setString(1, playerName);

            preparedStatement.execute();
            return player;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> deleteLevel(int levelNumber) {
        String deleteLevelSql = "delete from Levels where number = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            PreparedStatement preparedStatement = connection.prepareStatement("select * from Scores where level_id = ?");
            preparedStatement.setInt(1, levelId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int player_id = Integer.parseInt(resultSet.getString("player_id"));
                var player = getPlayerById(player_id);
                assert player != null : "Player not found";
                deleteScore(player.get("name"), levelNumber);
            }

            preparedStatement = connection.prepareStatement(deleteLevelSql);
            preparedStatement.setInt(1, levelNumber);

            preparedStatement.execute();
            return level;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> deleteScore(String playerName, int levelNumber) {
        String deleteScoreSql = "delete from Scores where player_id = ? and level_id = ?";
        Connection connection = DBConfig.getDBConnection();

        try {
            var level = getLevelByNumber(levelNumber);
            assert level != null : "Level not found";
            var levelId = Integer.parseInt(level.get("id"));

            var player = getPlayerByName(playerName);
            assert player != null : "Player not found";
            var playerId = Integer.parseInt(player.get("id"));

            var score = getScoreByPlayerAndLevel(playerName, levelNumber);
            assert score != null : "Score not found";

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

//    public static void updateLevelNotesMap(int levelNumber, String map) {
//        String updateLevelNotesMapSql = "update Notes set direction = ?, timing = ? where id = ?";
//        Connection connection = DBConfig.getDBConnection();
//
//        try {
//            var level = getLevelByNumber(levelNumber);
//            assert level != null : "Level not found";
//            var levelId = Integer.parseInt(level.get("id"));
//
//            PreparedStatement preparedStatement = connection.prepareStatement(updateLevelNotesMapSql);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}