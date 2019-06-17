public class Rule {


    private String stack, nid;
    private char in, push;

    /**
     * Transition rules
     *
     * @param in    Input
     * @param stack Top stack value
     * @param push  Value to push onto stack
     * @param nid   Target State id
     */
    public Rule(char in, String stack, char push, String nid) {
        this.in = in;
        this.stack = stack;
        this.push = push;
        this.nid = nid;
    }

    public char getIn() {
        return in;
    }

    public String getStack() {
        return stack;
    }

    public char getPush() {
        return push;
    }

    public String getNid() {
        return nid;
    }

    public String toString() {
        return "On '" + in + "' with a top stack symbol of '" + stack + "' go to '" + nid + "' and push " + push;
    }
}
