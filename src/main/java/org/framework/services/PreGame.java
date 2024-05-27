package org.framework.services;

import lombok.Getter;
import org.framework.services.database.CRUDService;

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
                String[] line = scanner.nextLine().trim().split(" ");
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
                    }
                    else if (line.length == 3 && line[1].equals("-notes")) {
                        List<Map<String, String>> notes = null;
                        try {
                            notes = CRUDService.getScoresByLevel(Integer.parseInt(line[2]));
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
                    }
                    else {
                        System.out.println("Wrong parameters in 'get' command!");
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
                    }
                    else if (line.length == 1) {
                        running = false;
                    }
                    else {
                        System.out.println("Wrong parameters in 'play' command!");
                    }
                }
                else if (line.length == 1 && (line[0].equals("exit") || line[0].equals("quit"))) {
                    running = false;
                    ret = "quit";
                }
                else {
                    System.out.println("Command not recognized!");
                }
            }
            System.out.println();
        }
    }
}
