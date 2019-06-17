import java.util.HashMap;

public class StateMap<String, State> extends HashMap<String, State> {

    /**
     * An extension of a hashmap with (String, State) pairs. The only added feature is to throw a runtime exception
     * if trying to get a State that wasn't defined in the user program
     */
    public StateMap() {
        super();
    }

    public State getN(Object key) throws RuntimeException {
        try {
            return super.get(key);
        } catch (Error | Exception e) {
            throw new RuntimeException("Undefined State " + key.toString());
        }
    }

}
