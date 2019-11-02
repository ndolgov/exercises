package net.ndolgov.imcgame.rps.cli;

import java.util.Scanner;

import net.ndolgov.imcgame.domain.GamePlayer;
import net.ndolgov.imcgame.domain.PlayerGameStatsCollector;
import net.ndolgov.imcgame.domain.PlayerMove;
import net.ndolgov.imcgame.domain.TwoPlayerGame;
import net.ndolgov.imcgame.rps.RpsGames;
import net.ndolgov.imcgame.rps.RpsItem;

/**
 * RPS game CLI
 * Ask for a player's name and the number of round to play and then start the game.
 */
public final class Main {
    public static void main(String[] args) {
        try (final Scanner scanner = new Scanner(System.in)) {
            System.out.println("Player id: ");
            final String playerId = scanner.next();
            final String humanId = playerId.isEmpty() ? "Human" : playerId;

            System.out.println("Rounds to play: ");
            final int nRounds = scanner.nextInt();

            final GamePlayer<RpsItem> human = new GamePlayer<>(humanId, (history) -> { throw new IllegalStateException("Not in CLI mode"); });
            final GamePlayer<RpsItem> computer = new GamePlayer<>("Computer", RpsGames.createRandomStrategy());
            final TwoPlayerGame<RpsItem> game = RpsGames.createNewGame(nRounds, human, computer);

            while (!game.isOver()) {
                game.onNextMove(new PlayerMove<>(human, readNextHumanMove(scanner)));
                game.onNextMove(computer.nextMove(game.history()));
            }

            new PlayerGameStatsCollector<RpsItem>().stats(game).forEachRemaining(ps -> {
                if (ps.playerId.equals(humanId)) {
                    System.out.println("You won " + ps.won + " games and lost " + ps.lost);
                }
            });
        }
    }

    private static RpsItem readNextHumanMove(Scanner scanner) {
        while(true) {
            System.out.println("Your move (r/p/s/q): ");
            final char ch = scanner.next().charAt(0);

            if (ch == 'r') {
                return RpsItem.ROCK;
            }

            if (ch == 'p') {
                return RpsItem.PAPER;
            }

            if (ch == 's') {
                return RpsItem.SCISSORS;
            }

            if (ch == 'q') {
                System.exit(1);
            }
        }
    }

    private Main() {
    }
}

