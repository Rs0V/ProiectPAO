package org.framework.services;

import lombok.Getter;
import org.framework.services.database.CRUDService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PreGame implements Runnable {
    protected boolean running = true;

    public boolean isRunning() {
        return running;
    }

    @Getter
    protected Object ret = null;


    @Override
    public void run() {
        CRUDService.createTables();

        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.printf("%nEnter a command: ");
            if (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                String[] line = command.split(" ");
                if (line[0].equals("get")) {
                    if (line.length == 3 && line[1].equals("-player")) {
                        Map<String, String> player = null;
                        player = CRUDService.getPlayerByName(line[2]);
                        if (player == null) {
                            System.out.println("Player not found");
                            continue;
                        }

                        for (Map.Entry<String, String> entry : player.entrySet()) {
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                        System.out.println();
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 2 && line[1].equals("-players")) {
                        List<Map<String, String>> players = CRUDService.getPlayers();
                        if (players == null) {
                            System.out.println("Players not found");
                            continue;
                        }

                        System.out.println("[");
                        for (var player : players) {
                            System.out.printf("\t{ ");
                            String tab = "";
                            for (Map.Entry<String, String> entry : player.entrySet()) {
                                System.out.println(tab + entry.getKey() + ": " + entry.getValue());
                                tab = "\t";
                            }
                            System.out.println("\t},");
                        }
                        System.out.println("]");
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 3 && line[1].equals("-level")) {
                        Map<String, String> level = null;
                        try {
                            level = CRUDService.getLevelByNumber(Integer.parseInt(line[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (level == null) {
                            System.out.println("Level not found");
                            continue;
                        }

                        for (Map.Entry<String, String> entry : level.entrySet()) {
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                        System.out.println();
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 2 && line[1].equals("-levels")) {
                        List<Map<String, String>> levels = CRUDService.getLevels();
                        if (levels == null) {
                            System.out.println("Levels not found");
                            continue;
                        }

                        System.out.println("[");
                        for (var level : levels) {
                            System.out.printf("\t{ ");
                            String tab = "";
                            for (Map.Entry<String, String> entry : level.entrySet()) {
                                System.out.println(tab + entry.getKey() + ": " + entry.getValue());
                                tab = "\t";
                            }
                            System.out.println("\t},");
                        }
                        System.out.println("]");
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 4 && line[1].equals("-score")) {
                        Map<String, String> score = null;
                        try {
                            score = CRUDService.getScoreByPlayerAndLevel(line[2], Integer.parseInt(line[3]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (score == null) {
                            System.out.println("Score not found");
                            continue;
                        }

                        for (Map.Entry<String, String> entry : score.entrySet()) {
                            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
                        }
                        System.out.println();
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 3 && line[1].equals("-scores")) {
                        List<Map<String, String>> scores = null;
                        try {
                            scores = CRUDService.getScoresByPlayer(line[2]);
                            if (scores == null) {
                                scores = CRUDService.getScoresByLevel(Integer.parseInt(line[2]));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (scores == null) {
                            System.out.println("Scores not found");
                            continue;
                        }

                        System.out.println("[");
                        for (var score : scores) {
                            System.out.printf("\t{ ");
                            String tab = "";
                            for (Map.Entry<String, String> entry : score.entrySet()) {
                                System.out.println(tab + entry.getKey() + ": " + entry.getValue());
                                tab = "\t";
                            }
                            System.out.println("\t},");
                        }
                        System.out.println("]");
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 3 && line[1].equals("-notes")) {
                        List<Map<String, String>> notes = null;
                        try {
                            notes = CRUDService.getNotesByLevel(Integer.parseInt(line[2]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (notes == null) {
                            System.out.println("Notes not found");
                            continue;
                        }

                        System.out.println("[");
                        for (var score : notes) {
                            System.out.printf("\t{ ");
                            String tab = "";
                            for (Map.Entry<String, String> entry : score.entrySet()) {
                                System.out.println(tab + entry.getKey() + ": " + entry.getValue());
                                tab = "\t";
                            }
                            System.out.println("\t},");
                        }
                        System.out.println("]");
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else {
                        System.out.println("Wrong parameters in 'get' command!");
                    }
                }
                else if (line[0].equals("update")) {
                    if (line[1].equals("-player")) {
                        if (line.length == 5 && line[3].equals("-name")) {
                            CRUDService.updatePlayerName(line[2], line[4]);
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        }
                        else {
                            System.out.println("Wrong parameters in 'update' command!");
                        }
                    }
                    else if (line[1].equals("-level")) {
                        if (line.length == 5 && line[3].equals("-number")) {
                            try {
                                CRUDService.updateLevelNumber(Integer.parseInt(line[2]), Integer.parseInt(line[4]));
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Wrong parameters in 'update' command!");
                            }
                        }
                        else if (line.length == 5 && line[3].equals("-song")) {
                            try {
                                CRUDService.updateLevelSong(Integer.parseInt(line[2]), line[4]);
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Wrong parameters in 'update' command!");
                            }
                        }
                        else if (line.length == 5 && line[3].equals("-notespeed")) {
                            try {
                                CRUDService.updateLevelNoteSpeed(Integer.parseInt(line[2]), Double.parseDouble(line[4]));
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Wrong parameters in 'update' command!");
                            }
                        }
                        else if (line.length == 4 && line[3].equals("-map")) {
                            try {
                                String levelMap = String.join("\n", Files.readAllLines(Paths.get("src/main/java/org/framework/level/levels/level-config.txt"), StandardCharsets.UTF_8));
                                CRUDService.updateLevelNotesMap(Integer.parseInt(line[2]), levelMap);
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Wrong parameters in 'update' command!");
                            }
                        }
                        else {
                            System.out.println("Wrong parameters in 'update' command!");
                        }
                    }
                    else if (line[1].equals("-score")) {
                        if (line.length == 6 && line[4].equals("-value")) {
                            try {
                                CRUDService.updatePlayerScoreOnLevel(line[2], Integer.parseInt(line[3]), Double.parseDouble(line[5]));
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Wrong parameters in 'update' command!");
                            }
                        }
                        else {
                            System.out.println("Wrong parameters in 'update' command!");
                        }
                    }
                    else {
                        System.out.println("Wrong parameters in 'update' command!");
                    }
                }
                else if (line[0].equals("insert")) {
                    if (line.length == 3 && line[1].equals("-player")) {
                        CRUDService.insertPlayer(line[2]);
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 5 && line[1].equals("-level")) {
                        CRUDService.insertLevel(Integer.parseInt(line[2]), line[3], Double.parseDouble(line[4]));

                        try {
                            String levelMap = String.join("\n", Files.readAllLines(Paths.get("src/main/java/org/framework/level/levels/level-config.txt"), StandardCharsets.UTF_8));
                            CRUDService.insertNotes(Integer.parseInt(line[2]), levelMap);
                        } catch (Exception e) {
                            System.out.println("Wrong parameters in 'insert' command!");
                        }

                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 5 && line[1].equals("-score")) {
                        try {
                            CRUDService.insertScore(Double.parseDouble(line[2]), line[3], Integer.parseInt(line[4]));
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("Wrong parameters in 'insert' command!");
                        }
                    }
                    else {
                        System.out.println("Wrong parameters in 'insert' command!");
                    }
                }
                else if (line[0].equals("delete")) {
                    if (line.length == 3 && line[1].equals("-player")) {
                        try {
                            CRUDService.deletePlayer(line[2]);
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("Specified player may not exist.");
                        }
                    }
                    else if (line.length == 2 && line[1].equals("-players")) {
                        try {
                            CRUDService.deletePlayers();
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("There may not be any players recorded.");
                        }
                    }
                    else if (line.length == 3 && line[1].equals("-level")) {
                        try {
                            CRUDService.deleteLevel(Integer.parseInt(line[2]));
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("Specified level may not exist.");
                        }
                    }
                    else if (line.length == 2 && line[1].equals("-levels")) {
                        try {
                            CRUDService.deleteLevels();
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("There may not be any levels recorded.");
                        }
                    }
                    else if (line.length == 4 && line[1].equals("-score")) {
                        try {
                            CRUDService.deleteScore(line[2], Integer.parseInt(line[3]));
                            Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                        } catch (Exception e) {
                            System.out.println("Specified score may not exist.");
                        }
                    }
                    else if (line[1].equals("-scores")) {
                        if (line.length == 4 && line[2].equals("-player")) {
                            try {
                                CRUDService.deleteScoresByPlayer(line[3]);
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Player may not have any scores recorded.");
                            }
                        }
                        else if (line.length == 4 && line[2].equals("-level")) {
                            try {
                                CRUDService.deleteScoresByLevel(Integer.parseInt(line[3]));
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Level may not have any scores recorded.");
                            }
                        }
                        else if (line.length == 2) {
                            try {
                                CRUDService.deleteScores();
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("There may not be any scores recorded.");
                            }
                        }
                        else {
                            System.out.println("Wrong parameters in 'delete' command!");
                        }
                    }
                    else if (line[1].equals("-notes")) {
                        if (line.length == 4 && line[2].equals("-level")) {
                            try {
                                CRUDService.deleteNotesByLevel(Integer.parseInt(line[3]));
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("Level may not have any notes recorded.");
                            }
                        }
                        else if (line.length == 2) {
                            try {
                                CRUDService.deleteNotes();
                                Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                            } catch (Exception e) {
                                System.out.println("There may not be any notes recorded.");
                            }
                        }
                        else {
                            System.out.println("Wrong parameters in 'delete' command!");
                        }
                    }
                    else {
                        System.out.println("Wrong parameters in 'delete' command!");
                    }
                }
                else if (line[0].equals("play")) {
                    if (line.length == 3 && line[1].equals("-level")) {
                        var level = CRUDService.getLevelByNumber(Integer.parseInt(line[2]));
                        if (level == null) {
                            System.out.println("Level not found");
                            continue;
                        }

                        ret = level;
                        running = false;
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else if (line.length == 1) {
                        running = false;
                        Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                    }
                    else {
                        System.out.println("Wrong parameters in 'play' command!");
                    }
                }
                else if (line.length == 1 && (line[0].equals("exit") || line[0].equals("quit"))) {
                    running = false;
                    ret = "quit";
                    Recorder.record("audit", command + ", " + LocalDateTime.now() + "\n\n");
                }
                else {
                    System.out.println("Command not recognized!");
                }
            }
            System.out.println();
        }
    }
}
