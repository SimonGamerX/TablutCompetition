package tablut.client.ai;

import tablut.domain.State;
import tablut.domain.State.Pawn;
public abstract class Heuristic {
    /**
     * Evaluates the given state.
     * @param state The state to evaluate
     * @return A double representing the value of the state for the current player.
     *         Higher values mean a better state.
     */
    public abstract double evaluate(State state);

    protected boolean isPathClear(Pawn[][] board, int startRow, int startCol, int offsetRow, int offsetCol) {
        int row = startRow + offsetRow;
        int col = startCol + offsetCol;
        while (row >= 0 && row < board.length && col >= 0 && col < board[0].length) {
            /**
             * Checks for empty square and throne square
             */
            if (board[row][col] != Pawn.EMPTY && board[row][col] != Pawn.THRONE) {
                return false;
            }

            /**
             * Checks for black camps
             */
            if ((row == 0 && (col == 3 || col == 4 || col == 5)) ||  // 3 black camps in row 0 (top side)
                (row == 1 && col == 4) ||                            // 1 black camp in row 1 (top side)
                (row == 8 && (col == 3 || col == 4 || col == 5)) ||  // 3 black camps in row 8 (bottom side)
                (row == 7 && col == 4) ||                            // 1 black camp in row 7 (bottom side)
                (col == 0 && (row == 3 || row == 4 || row == 5)) ||  // 3 black camps in col 0 (left side)
                (col == 1 && row == 4) ||                            // 1 black camp in col 2 (left side)
                (col == 8 && (row == 3 || row == 4 || row == 5)) ||  // 3 black camps in col 8 (right side)
                (col == 7 && row == 4)) {                            // 1 black camp  in col 7 (right side)
                return false; // Citadel
            }

            row += offsetRow;
            col += offsetCol;
        }
        return true;
    }
}