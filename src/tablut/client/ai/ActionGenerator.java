package tablut.client.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tablut.domain.Action;
import tablut.domain.GameAshtonTablut;
import tablut.domain.State;
import tablut.domain.State.Pawn;

public class ActionGenerator {

    private static GameAshtonTablut simulator;

    static {
        // Create a single instance to validate moves and generate next states.
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

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                Pawn p = board[row][col];
                if ((turn == State.Turn.WHITE && (p == Pawn.WHITE || p == Pawn.KING)) ||
                    (turn == State.Turn.BLACK && p == Pawn.BLACK)) {
                    
                    String from = state.getBox(row, col);
                    // Check all 4 directions
                    checkDirection(state, turn, row, col, from, -1, 0, results); // Up
                    checkDirection(state, turn, row, col, from, 1, 0, results);  // Down
                    checkDirection(state, turn, row, col, from, 0, -1, results); // Left
                    checkDirection(state, turn, row, col, from, 0, 1, results);  // Right
                }
            }
        }
        return results;
    }

    private static void checkDirection(State state, State.Turn turn, int row, int col, String from, int rowDelta, int colDelta, List<MoveResult> results) {
        int newRow = row + rowDelta;
        int newCol = col + colDelta;
        Pawn[][] board = state.getBoard();

        while (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
            if (board[newRow][newCol] != Pawn.EMPTY) {
                break; // blocked by another pawn
            }

            String to = state.getBox(newRow, newCol);
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

            newRow += rowDelta;
            newCol += colDelta;
        }
    }
}
