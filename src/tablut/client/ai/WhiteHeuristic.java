package tablut.client.ai;

import tablut.domain.State;
import tablut.domain.State.Pawn;

public class WhiteHeuristic extends Heuristic {

    @Override
    public double evaluate(State state) {
        if (state.getTurn() == State.Turn.WHITEWIN) {
            return 10000.0;
        } else if (state.getTurn() == State.Turn.BLACKWIN) {
            return -10000.0;
        } else if (state.getTurn() == State.Turn.DRAW) {
            return 0.0;
        }

        double score = 0.0;
        Pawn[][] board = state.getBoard();
        int kingRow = -1;
        int kingCol = -1;
        int whiteCount = 0;
        int blackCount = 0;

        /**
         * Counts the number of black and white pawns and king coordinates
         */
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == Pawn.KING) {
                    kingRow = r;
                    kingCol = c;
                } else if (board[r][c] == Pawn.WHITE) {
                    whiteCount++;
                } else if (board[r][c] == Pawn.BLACK) {
                    blackCount++;
                }
            }
        }

        /**
         * King absent, game lost
         */
        if (kingRow == -1) {
            return -10000.0;
        }

        /**
         * +10 for each white, -5 for each black
         */
        score += whiteCount * 10.0;
        score -= blackCount * 5.0;

        /**
         * Count black pawns around the king and modify the total score accordingly
         */
        int enemiesNearKing = 0;
        if (kingRow > 0 && board[kingRow - 1][kingCol] == Pawn.BLACK) enemiesNearKing++;
        if (kingRow < 8 && board[kingRow + 1][kingCol] == Pawn.BLACK) enemiesNearKing++;
        if (kingCol > 0 && board[kingRow][kingCol - 1] == Pawn.BLACK) enemiesNearKing++;
        if (kingCol < 8 && board[kingRow][kingCol + 1] == Pawn.BLACK) enemiesNearKing++;
        score -= enemiesNearKing * 15.0;
        
        /**
         * Checks if king is near the edge, if so raises the score
         */
        int distToEdge = Math.min(Math.min(kingRow, 8 - kingRow), Math.min(kingCol, 8 - kingCol));
        score += (4 - distToEdge) * 20.0;

        /**
         * Checks if king has a free path towards the edge, if so raises the score 
         */
        int freePaths = 0;
        if (super.isPathClear(board, kingRow, kingCol, -1, 0)) freePaths++; // up
        if (super.isPathClear(board, kingRow, kingCol, 1, 0)) freePaths++; // down
        if (super.isPathClear(board, kingRow, kingCol, 0, -1)) freePaths++; // left
        if (super.isPathClear(board, kingRow, kingCol, 0, 1)) freePaths++; // right
        if (freePaths > 0) {
            score += freePaths * 100.0; // high priority if a path is open
        }

        return score;
    }
}