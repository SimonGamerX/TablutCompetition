package it.unibo.ai.didattica.competition.tablut.client.ai;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class BlackHeuristic implements Heuristic {

    @Override
    public double evaluate(State state) {
        if (state.getTurn() == State.Turn.BLACKWIN) {
            return 10000.0;
        } else if (state.getTurn() == State.Turn.WHITEWIN) {
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

        if (kingRow == -1) {
            return 10000.0; // King captured
        }

        // 1. Maximize black pawns, minimize white pawns
        score += blackCount * 10.0;
        score -= whiteCount * 10.0; // Black needs to capture whites to reach the king

        // 2. Surround the king
        int enemiesNearKing = 0;
        if (kingRow > 0 && board[kingRow - 1][kingCol] == Pawn.BLACK) enemiesNearKing++;
        if (kingRow < 8 && board[kingRow + 1][kingCol] == Pawn.BLACK) enemiesNearKing++;
        if (kingCol > 0 && board[kingRow][kingCol - 1] == Pawn.BLACK) enemiesNearKing++;
        if (kingCol < 8 && board[kingRow][kingCol + 1] == Pawn.BLACK) enemiesNearKing++;
        
        score += enemiesNearKing * 20.0;

        // 3. Penalize king being close to edge
        int distToEdge = Math.min(Math.min(kingRow, 8 - kingRow), Math.min(kingCol, 8 - kingCol));
        score -= (4 - distToEdge) * 30.0;

        // 4. Block free paths to the edge for the King
        int freePaths = 0;
        if (isPathClear(board, kingRow, kingCol, -1, 0)) freePaths++; // up
        if (isPathClear(board, kingRow, kingCol, 1, 0)) freePaths++;  // down
        if (isPathClear(board, kingRow, kingCol, 0, -1)) freePaths++; // left
        if (isPathClear(board, kingRow, kingCol, 0, 1)) freePaths++;  // right
        
        if (freePaths > 0) {
            score -= freePaths * 150.0; // high penalty if a path is open
        }

        return score;
    }

    private boolean isPathClear(Pawn[][] board, int startRow, int startCol, int dRow, int dCol) {
        int r = startRow + dRow;
        int c = startCol + dCol;
        while (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
            if (board[r][c] != Pawn.EMPTY && board[r][c] != Pawn.THRONE) {
                return false;
            }
            if ((r == 0 && (c == 3 || c == 4 || c == 5)) ||
                (r == 1 && c == 4) ||
                (r == 8 && (c == 3 || c == 4 || c == 5)) ||
                (r == 7 && c == 4) ||
                (c == 0 && (r == 3 || r == 4 || r == 5)) ||
                (c == 1 && r == 4) ||
                (c == 8 && (r == 3 || r == 4 || r == 5)) ||
                (c == 7 && r == 4)) {
                return false; // Citadel
            }
            r += dRow;
            c += dCol;
        }
        return true;
    }
}
