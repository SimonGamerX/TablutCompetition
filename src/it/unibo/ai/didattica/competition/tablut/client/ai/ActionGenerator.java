package it.unibo.ai.didattica.competition.tablut.client.ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class ActionGenerator {

    private static GameAshtonTablut simulator;

    static {
        // Create a single instance to validate moves and generate next states.
        // We set cache size to 0 because we handle draws externally or ignore for shallow search.
        simulator = new GameAshtonTablut(0, -1, "logs", "ActionGenerator", "ActionGenerator");
        // Disable logging to prevent writing millions of lines during search
        Logger.getLogger("GameLog").setLevel(Level.OFF);
    }

    public static GameAshtonTablut getSimulator() {
        return simulator;
    }

    public static class MoveResult {
        public Action action;
        public State resultingState;

        public MoveResult(Action action, State resultingState) {
            this.action = action;
            this.resultingState = resultingState;
        }
    }

    /**
     * Generates all valid moves for the current player in the given state.
     */
    public static List<MoveResult> generateValidMoves(State state) {
        List<MoveResult> results = new ArrayList<>();
        Pawn[][] board = state.getBoard();
        State.Turn turn = state.getTurn();

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                Pawn p = board[r][c];
                if ((turn == State.Turn.WHITE && (p == Pawn.WHITE || p == Pawn.KING)) ||
                    (turn == State.Turn.BLACK && p == Pawn.BLACK)) {
                    
                    String from = state.getBox(r, c);
                    // Check all 4 directions
                    checkDirection(state, turn, r, c, from, -1, 0, results); // Up
                    checkDirection(state, turn, r, c, from, 1, 0, results);  // Down
                    checkDirection(state, turn, r, c, from, 0, -1, results); // Left
                    checkDirection(state, turn, r, c, from, 0, 1, results);  // Right
                }
            }
        }
        return results;
    }

    private static void checkDirection(State state, State.Turn turn, int r, int c, String from, int dr, int dc, List<MoveResult> results) {
        int nr = r + dr;
        int nc = c + dc;
        Pawn[][] board = state.getBoard();

        while (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
            if (board[nr][nc] != Pawn.EMPTY) {
                break; // blocked by another pawn
            }

            String to = state.getBox(nr, nc);
            try {
                Action a = new Action(from, to, turn);
                State clone = state.clone();
                // GameAshtonTablut.checkMove throws an exception if the move is invalid
                State newState = simulator.checkMove(clone, a);
                results.add(new MoveResult(a, newState));
            } catch (Exception e) {
                // The move was invalid (e.g. crossing a citadel)
                // If it's invalid due to rules like citadel, we shouldn't continue in this direction
                // if it's climbing. But checkMove actually checks everything.
                // We'll just ignore and continue generating, though strictly we might break early
                // if it's an obstacle. But checkMove handles it.
            }

            nr += dr;
            nc += dc;
        }
    }
}
