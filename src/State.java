import java.util.ArrayList;
import java.util.Stack;

public class State {

    String id;
    StateMap<String, State> states;
    Stack<String> stack;
    ArrayList<Rule> rules;

    /**
     * State objects represent the states of the diagram
     *
     * @param id     the name of the state, used to fetch a given state in a hashmap
     * @param states an array containing all the states of the automata
     * @throws RuntimeException if a state has already been declared
     */
    public State(String id, StateMap<String, State> states) throws RuntimeException {

        if (!states.containsKey(id)) {
            this.id = id;
            this.states = states;
            this.rules = new ArrayList<>();
        } else throw new RuntimeException("Syntax error: State " + id + " declared twice");
    }

    /**
     * Adds a transition rule to the automata's delta
     *
     * @param r rule to be added
     */
    public void addRule(Rule r) {
        this.rules.add(r);
    }

    /**
     * The core of the automata's execution
     *
     * @param input single character input. Used along with the transition rules to determine the next state
     * @param stack the pushdown automata's stack, used for context free languages
     * @return One of more states (non-determinism) available for a given input
     */
    public ArrayList<Output> transition(char input, Stack<String> stack) {

        this.stack = stack;

        ArrayList<Output> results = new ArrayList<>();

        //This indicated whether the transition popped a stack value
        boolean popped = false;

        //Iterates through the state's transition rules
        for (int i = 0; i < rules.size(); i++) {
            Rule r = rules.get(i);

            //If the stack was already popped during the transition, break
            if (popped) return results;

            //Top stack symbol
            String topStack = (stack.empty() || stack.peek().equals("~")) ? null : stack.peek();

            if (r.getIn() == '~' && ((r.getStack().equals("~")) || r.getStack().equals(topStack))) {
                if (!stack.isEmpty() && !r.getStack().equals("~")) {
                    stack.pop();
                    popped = true;
                }
                results.add(new Output(states.getN(r.getNid()), r.getPush(), r.getIn()));
            } else if (r.getIn() == input && ((r.getStack().equals("~")) || r.getStack().equals(topStack))) {
                if (!stack.isEmpty() && !r.getStack().equals("~")) {
                    stack.pop();
                    popped = true;
                }
                results.add(new Output(states.getN(r.getNid()), r.getPush(), r.getIn()));
            }

        }

        return results;
    }

    /**
     * Used for debugging. Prints all the rules of a given state
     */
    public void printRules() {
        for (int i = 0; i < rules.size(); i++) {
            System.out.println(rules.get(i));
        }
    }
}
