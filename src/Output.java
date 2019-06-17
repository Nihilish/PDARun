public class Output {

    private State n;
    private char push;
    private char charUsed;

    /**
     * Output is a vector value returned after a delta transition. It contains a State, a character to be pushed on the stack
     * and the character that was used by the transition
     *
     * @param n        state
     * @param push     character to push onto stack
     * @param charUsed character used for transition
     */
    public Output(State n, char push, char charUsed) {
        this.n = n;
        this.push = push;
        this.charUsed = charUsed;
    }

    /**
     * Allows to check if epsilon was used in a transition
     *
     * @return character used in transition
     */
    public char getCharUsed() {
        return charUsed;
    }

    public State getN() {
        return n;
    }

    public char getPush() {
        return push;
    }
}
