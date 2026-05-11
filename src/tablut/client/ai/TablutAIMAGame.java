package tablut.client.ai;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import tablut.domain.Action;
import tablut.domain.State;

public class TablutAIMAGame implements Game<State, Action, State.Turn> {

    private Heuristic heuristic;

    public TablutAIMAGame(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public State getInitialState() {
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

    /**
     * Get possibole actions from ActionGenerator and orders them in an ArrayList
     */
    @Override
    public List<Action> getActions(State state) {
        List<ActionGenerator.MoveResult> moveResults = ActionGenerator.generateValidMoves(state);
        List<Action> actions = new ArrayList<>();
        for (ActionGenerator.MoveResult mr : moveResults) {
            actions.add(mr.action);
        }
        return actions;
    }

    /**
     * Applys the action to a clone of the state and returns the new state
     */
    @Override
    public State getResult(State state, Action action) {
        try {
            return ActionGenerator.getSimulator().checkMove(state.clone(), action);
        } catch (Exception e) {
            // Should not happen for valid actions returned by getActions
            return state;
        }
    }

    /**
     * Checks if the games is terminated
     */
    @Override
    public boolean isTerminal(State state) {
        return state.getTurn() == State.Turn.WHITEWIN ||
               state.getTurn() == State.Turn.BLACKWIN ||
               state.getTurn() == State.Turn.DRAW;
    }

    /**
     * Calculates the heuristic of the player in a given state
     */
    @Override
    public double getUtility(State state, State.Turn player) {
        return heuristic.evaluate(state);
    }
}
