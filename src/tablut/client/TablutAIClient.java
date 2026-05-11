package tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

import tablut.client.ai.BlackHeuristic;
import tablut.client.ai.Heuristic;
import tablut.client.ai.TablutAIMAGame;
import tablut.client.ai.WhiteHeuristic;
import tablut.domain.Action;
import tablut.domain.State;
import tablut.domain.StateTablut;

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;

public class TablutAIClient extends TablutClient {

    private int timeout;
    private Heuristic heuristic;
    private TablutAIMAGame aimaGame;

    public TablutAIClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);
        this.timeout = timeout;

        if (this.getPlayer() == State.Turn.WHITE) {
            this.heuristic = new WhiteHeuristic();
        } else {
            this.heuristic = new BlackHeuristic();
        }
        this.aimaGame = new TablutAIMAGame(this.heuristic);
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            
            State state = this.getCurrentState();
            System.out.println("Turn: " + state.getTurn().toString());

            if (state.getTurn().equalsTurn(this.getPlayer().toString())) {
                System.out.println("Thinking...");

                IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn> search =
                        IterativeDeepeningAlphaBetaSearch.createFor(aimaGame, -10000.0, 10000.0, this.timeout - 2);

                Action action = search.makeDecision(state);
                
                if (action != null) {
                    System.out.println("Chosen move: " + action.toString());
                    try {
                        this.write(action);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No valid move found!");
                    System.exit(1);
                }
            } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                if (this.getPlayer() == State.Turn.WHITE) {
                    System.out.println("YOU WIN!");
                } else {
                    System.out.println("YOU LOSE!");
                }
                System.exit(0);
            } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                if (this.getPlayer() == State.Turn.BLACK) {
                    System.out.println("YOU WIN!");
                } else {
                    System.out.println("YOU LOSE!");
                }
                System.exit(0);
            } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                System.out.println("DRAW!");
                System.exit(0);
            } else {
                System.out.println("Waiting for your opponent move... ");
            }
        }
    }
}
