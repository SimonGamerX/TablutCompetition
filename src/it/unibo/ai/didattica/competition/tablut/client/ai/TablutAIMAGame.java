package it.unibo.ai.didattica.competition.tablut.client.ai;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class TablutAIMAGame implements Game<State, Action, State.Turn> {

    private Heuristic heuristic;

    public TablutAIMAGame(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public State getInitialState() {
        // Not used by the search, since we always pass the current state
        return null;
    }

    @Override
    public State.Turn[] getPlayers() {
        return new State.Turn[]{State.Turn.WHITE, State.Turn.BLACK};
    }

    @Override
    public State.Turn getPlayer(State state) {
        return state.getTurn();
    }

    @Override
    public List<Action> getActions(State state) {
        List<ActionGenerator.MoveResult> moveResults = ActionGenerator.generateValidMoves(state);
        List<Action> actions = new ArrayList<>();
        for (ActionGenerator.MoveResult mr : moveResults) {
            actions.add(mr.action);
        }
        return actions;
    }

    @Override
    public State getResult(State state, Action action) {
        try {
            // Apply the action on a clone to get the new state
            return ActionGenerator.getSimulator().checkMove(state.clone(), action);
        } catch (Exception e) {
            // Should not happen for valid actions returned by getActions
            return state;
        }
    }

    @Override
    public boolean isTerminal(State state) {
        return state.getTurn() == State.Turn.WHITEWIN ||
               state.getTurn() == State.Turn.BLACKWIN ||
               state.getTurn() == State.Turn.DRAW;
    }

    @Override
    public double getUtility(State state, State.Turn player) {
        // Our heuristics evaluate from the perspective of the current heuristic owner,
        // but AIMA expects utility from the perspective of the specific player passed as an argument.
        // Wait, if WhiteHeuristic is used, it returns positive for WHITE winning.
        // The IterativeDeepeningAlphaBetaSearch in AIMA uses the utility from the perspective of the player 
        // who is making the choice at the root of the tree.
        // So we can just use our heuristic.evaluate(state) which is already polarized for the 
        // player who owns the heuristic.
        return heuristic.evaluate(state);
    }
}
