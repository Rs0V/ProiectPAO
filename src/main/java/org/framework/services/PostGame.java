package org.framework.services;

import lombok.Getter;
import lombok.Setter;
import org.framework.level.Level;
import org.framework.services.database.CRUDService;

import java.util.Map;
import java.util.Scanner;

public class PostGame implements Runnable {
    protected boolean readName = false;
    @Getter @Setter
    protected Map<String, String> levelPlayed = null;

    @Override
    public void run() {
        System.out.printf("%n%nScore: %dpts%n%n", ChartEditor.getScore());

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name (5 letters): ");

        while(readName == false) {
			if (scanner.hasNextLine()) {
				String playerName = scanner.nextLine().trim().toUpperCase();
                if (playerName.length() > 5) {
                    System.out.println("Name has more than 5 letters! Try again.");
                    continue;
                }

                int levelNumber = (levelPlayed == null) ? 1 : Integer.parseInt(levelPlayed.get("number"));
				CRUDService.insertPlayer(playerName);
                try {
                    var score = CRUDService.getScoreByPlayerAndLevel(playerName, levelNumber);
                    double scoreValue = Double.parseDouble(score.get("value"));
                    if (scoreValue < ChartEditor.getScore()) {
                        CRUDService.updatePlayerScoreOnLevel(playerName, levelNumber, ChartEditor.getScore());
                    }
                } catch (Exception e) {
                    try {
                        CRUDService.insertScore(ChartEditor.getScore(), playerName, levelNumber);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

				readName = true;
			}
		}
		scanner.close();
    }
}
