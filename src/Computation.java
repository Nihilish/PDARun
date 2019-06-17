import java.util.ArrayList;
import java.util.Stack;

public class Computation {

    public Stack<String> stack;
    public StateMap<String, State> states;
    public ArrayList<String> accept;
    public State state;
    public String input;
    public boolean accepting = false;

    /**
     * Computations are created when the automata branches into a non-deterministic tree
     *
     * @param stack  The main execution's stack is CLONED
     * @param states Contains all the states of the automata
     * @param input  The input is typically a substring of the total input
     * @param state  Holds the current state of the execution
     * @param accept Copy of the accept state
     */
    public Computation(Stack<String> stack, StateMap<String, State> states, String input, State state, ArrayList<String> accept) {
        this.states = states;
        this.state = state;
        this.stack = new Stack<>();
        this.stack = (Stack<String>) stack.clone();
        this.input = input;
        this.accept = accept;
    }

    public boolean isAccepting() {
        return accepting;
    }

    /**
     * Works exactly the same as the main execute method, but returns false if the fork doesn't lead to an accepting state
     *
     * @return false if the fork doesn't lead to an accepting state
     */
    public boolean execute() {
        for (int i = 0; i < input.length(); i++) {

            ArrayList<Output> outs = state.transition(input.charAt(i), stack);

            if (outs.size() == 0) {
                accepting = false;
                return false;
            }

            if (outs.size() > 1) {
                for (int j = 1; j < outs.size(); j++) {
                    Computation fork = new Computation((Stack) stack.clone(), states, input.substring(i), outs.get(j).getN(), accept);
                    if (outs.get(j).getPush() != '~') fork.stack.push("" + outs.get(j).getPush());
                    if (!fork.execute()) accepting = false;
                    if (fork.isAccepting()) {
                        accepting = true;
                    }
                }
            }

            state = outs.get(0).getN();
            if (outs.get(0).getPush() != '~') stack.push("" + outs.get(0).getPush());

            if (outs.size() > 0 && outs.get(0).getCharUsed() == '~') {
                i--;
            }

            if (i == input.length() - 1 && accept.contains(state.id)) {
                accepting = true;
                return true;
            }

            if ((i + 1) == input.length() && stack.size() == 1) {
                ArrayList<Output> out = state.transition('~', stack);
                if (out.size() > 0 && accept.contains(out.get(0).getN().id)) {
                    accepting = true;
                }
            }

            continue;
        }

        if (state != null && accept.contains(state.id)) {
            accepting = true;

        }

        return true;

    }


}
