package it.unibo.ai.didattica.competition.tablut.client.ai;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface Heuristic {
    /**
     * Evaluates the given state.
     * @param state The state to evaluate
     * @return A double representing the value of the state for the current player.
     *         Higher values mean a better state.
     */
    double evaluate(State state);
}
