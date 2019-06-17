import java.io.*;
import java.util.*;

public class PDARun {

    static final String symbolRegex = "[!-z~-]";
    static String program, s;
    static ArrayList<String> delta, accept;
    static ArrayList<Character> alpha;
    static StateMap<String, State> states;
    static Stack<String> stack;
    static boolean hasAccepted = false;
    static ArrayList<String> allCombinations = new ArrayList<>();
    static boolean testMode = false;
    static HashMap<String, String> results = new HashMap<>();

    public static void main(String[] args) {

        try {

            if (!args[0].matches("\\w*\\.pda")) {
                throw new RuntimeException("Incorrect file name");
            }

            InputStream in = new FileInputStream(new File(args[0]));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            program = out.toString();   //Prints the string content read from input stream
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            program = "";
        } catch (ArrayIndexOutOfBoundsException a) {
            program = "";
            System.out.println("No/too many files specified");
        } catch (IOException e) {
            e.printStackTrace();
        }

        states = new StateMap<>();
        alpha = new ArrayList<>();
        accept = new ArrayList<>();
        delta = new ArrayList<>();
        stack = new Stack<>();
        s = "";

        //Builds the automata
        parse();

        System.out.println("Automata created successfully, please type the string to test: \n");

        Scanner scan = new Scanner(System.in);

        //Input loop
        while (true) {

            hasAccepted = false;
            stack.clear();
            testMode = false;
            System.out.print("input: ");
            String input = scan.nextLine();

            if (input.matches("\\*inputs \\d*\\*")) {

                System.out.println("Computing...");

                testMode = true;

                int j = Integer.parseInt("" + input.substring(8, input.length() - 1));

                gen(j);

                for (int i = 0; i < allCombinations.size(); i++) {
                    hasAccepted = false;
                    stack.clear();
                    execute(allCombinations.get(i));

                }

                String rejects = "";

                for (Map.Entry<String, String> entry : results.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value.equals("ACCEPT")) {
                        System.out.println(key + " : " + "ACCEPT");
                    } else {
                        rejects += key + " : " + "REJECT\n";
                    }
                }

                System.out.println(rejects);

            } else {
                execute(input);
            }
        }

    }

    /**
     * Used to check for double declarations
     *
     * @param s string to be checked
     * @return true if s is only in the program once, false otherwise
     */
    public static boolean checkDouble(String s) {
        return program.indexOf(s) == program.lastIndexOf(s);
    }

    /**
     * Parses the program
     */
    public static void parse() {

        if (!checkDouble("alpha") || !checkDouble("delta") || !checkDouble("start") || !checkDouble("accept") || !checkDouble("states")) {
            syntaxError("You cannot declare an attribute more than once");
        }


        int index = 0;
        String result = "";
        String syntax = "";

        //parse States
        if (program.contains("states{")) {

            //Checks syntax
            for (int i = program.indexOf("states{"); i < program.length(); i++) {
                if (program.charAt(i) == '}') {
                    syntax += "}";
                    if (!syntax.matches("states\\{(\\w*,(\\s)*)*\\w+\\}")) {
                        syntaxError("States' names may only contain alphanumeric characters and must be separated by ','");
                    }

                    break;
                }

                syntax += program.charAt(i);
            }


            index = program.indexOf("states{") + 7;
            for (int i = index; i < program.length(); i++) {
                if (program.charAt(i) == ',') {
                    states.put(result, new State(result, states));
                    result = "";
                } else if (program.charAt(i) == '}') {
                    states.put(result, new State(result, states));
                    result = "";
                    syntax = "";

                    try {

                        if (program.charAt(i + 1) != ';') {
                            syntaxError("Missing ; after states declaration");
                        }

                    } catch (Error | Exception e) {
                        syntaxError("Missing ; after states declaration");
                    }

                    break;

                } else result += program.charAt(i);

            }

        } else syntaxError("Invalid states declaration");

        //parse Alphabet
        if (program.contains("alpha{")) {

            //Checks syntax
            for (int i = program.indexOf("alpha{"); i < program.length(); i++) {
                if (program.charAt(i) == '}') {
                    syntax += "}";
                    if (!syntax.matches("alpha\\{(" + symbolRegex + ",(\\s)*)*" + symbolRegex + "\\}")) {
                        syntaxError("Illegal alphabet symbols or declaration");
                    }

                    break;
                }

                syntax += program.charAt(i);
            }


            index = program.indexOf("alpha{") + 6;
            for (int i = index; i < program.length(); i++) {
                if (program.charAt(i) == ',') {
                    alpha.add(program.charAt(i - 1));
                    result = "";
                } else if (program.charAt(i) == '}') {
                    alpha.add(program.charAt(i - 1));
                    result = "";
                    syntax = "";

                    try {

                        if (program.charAt(i + 1) != ';') {
                            syntaxError("Missing ; after alpha declaration");
                        }

                    } catch (Error | Exception e) {
                        syntaxError("Missing ; after alpha declaration");
                    }

                    break;
                } else result += program.charAt(i);
            }

        } else syntaxError("Invalid alpha declaration");

        //parse accept
        if (program.contains("accept{")) {

            //Checks syntax
            for (int i = program.indexOf("accept{"); i < program.length(); i++) {
                if (program.charAt(i) == '}') {
                    syntax += "}";
                    if (!syntax.matches("accept\\{(\\w*,(\\s)*)*\\w+\\}")) {
                        syntaxError("Illegal accept declaration");
                    }

                    break;
                }

                syntax += program.charAt(i);
            }


            index = program.indexOf("accept{") + 7;
            for (int i = index; i < program.length(); i++) {
                if (program.charAt(i) == ',') {
                    if (states.containsKey(result)) accept.add(result);
                    else syntaxError("Accept state " + result + " not declared in states");
                    result = "";
                } else if (program.charAt(i) == '}') {
                    if (states.containsKey(result)) accept.add(result);
                    else syntaxError("Accept state " + result + " not declared in states");
                    result = "";
                    syntax = "";

                    try {

                        if (program.charAt(i + 1) != ';') {
                            syntaxError("Missing ; after accept declaration");
                        }

                    } catch (Error | Exception e) {
                        syntaxError("Missing ; after accept declaration");
                    }

                    break;
                } else result += program.charAt(i);
            }

        } else syntaxError("Invalid accept declaration");

        //parse start
        if (program.contains("start:")) {

            //Checks syntax
            for (int i = program.indexOf("start:"); i < program.length(); i++) {
                if (program.charAt(i) == ';') {
                    syntax += ";";
                    if (!syntax.matches("start:\\w*;")) {
                        syntaxError("Illegal accept declaration");
                    }

                    break;
                }

                syntax += program.charAt(i);
            }


            index = program.indexOf("start:") + 6;
            for (int i = index; i < program.length(); i++) {
                if (program.charAt(i) == ';') {
                    if (states.containsKey(result)) s = result;
                    else syntaxError("Start state " + result + " not declared in states");
                    result = "";
                    syntax = "";
                    break;
                } else result += program.charAt(i);
            }

        } else syntaxError("Invalid start declaration");

        //parse delta
        if (program.contains("delta{")) {

            //Checks syntax
            for (int i = program.indexOf("delta{"); i < program.length(); i++) {
                if (program.charAt(i) == '}') {
                    syntax += "}";
                    if (!syntax.matches("delta\\{\\s*(\\w*\\(" + symbolRegex + "," + symbolRegex + "\\) goto \\w* push " + symbolRegex + ";\\s*)+\\}")) {
                        syntaxError("delta must be of the form delta{state(input,topStackSymbol) goto newstate push newstackSymbol;");
                    }

                    break;
                }

                syntax += program.charAt(i);
            }


            index = program.indexOf("delta{") + 6;
            for (int i = index; i < program.length(); i++) {

                String b, c;
                char d;
                char a;

                if (program.charAt(i) == '(') {
                    result = result.replace(" ", "");
                    a = program.charAt(i + 1);
                    b = "" + program.charAt(i + 3);

                    c = program.substring(program.indexOf("goto ", i) + 5, program.indexOf(" push", i));

                    i = program.indexOf("push ", i) + 5;

                    d = program.charAt(i);

                    if (!alpha.contains(a) || !states.containsKey(c)) {
                        if (a == '~') {
                            //ok
                        } else syntaxError("Illegal delta transition");
                    }

                    states.get(result).addRule(new Rule(a, b, d, c));

                    i += 2;

                    result = "";

                } else result += program.charAt(i);

            }

        } else syntaxError("Invalid delta declaration");
    }

    /**
     * Executes the compiled automata with a user defined input
     *
     * @param input input to be tested by the automata
     */
    public static void execute(String input) {

        //Gets the starting state
        State state = states.getN(s);

        /**
         * Checks if empty string (epsilon) is part of the language
         */
        if (input.length() == 0) {
            ArrayList<Output> epsilon = state.transition('~', stack);
            for (int i = 0; i < epsilon.size(); i++) {
                if (accept.contains(epsilon.get(i).getN().id)) {
                    accept(input);
                }
            }

            if (accept.contains(state.id)) {
                accept(input);
            }

        }

        /**
         * Computes the final state with the user defined transition rules and input
         */
        for (int i = 0; i < input.length(); i++) {

            ArrayList<Output> outs = state.transition(input.charAt(i), stack);

            //Rejects if the final state is a 'trash' state
            if (outs.size() == 0) {
                reject(input);
                return;
            }

            //Enables support for non-determinism. If more than one state is reachable upon the same input, branches are created and fully explored with the remaining input before proceeding
            if (outs.size() > 1) {
                for (int j = 1; j < outs.size(); j++) {
                    //The stack is cloned to ensure computing non-deterministic branches don't alter the main branch
                    Computation fork = new Computation((Stack) stack.clone(), states, input.substring(i), outs.get(j).getN(), accept);

                    if (outs.get(j).getPush() != '~') fork.stack.push("" + outs.get(j).getPush());

                    fork.execute();

                    //If at the end of the input, one of the branches led to an accepting state, the input is accepted
                    if (fork.isAccepting()) {
                        accept(input);
                    }
                }
            }

            //Main branch execution resumes
            state = outs.get(0).getN();

            //Pushes a symbol onto the stack, except if the symbol is the empty string
            if (outs.get(0).getPush() != '~') stack.push("" + outs.get(0).getPush());

            //If an epsilon-transition was made, do not increment the charAt index
            if (outs.size() > 0 && outs.get(0).getCharUsed() == '~') {
                i--;
            }

            //Handles the last character of the string
            if ((i + 1) == input.length() && stack.size() == 1) {
                ArrayList<Output> out = state.transition('~', stack);
                if (out.size() > 0 && accept.contains(out.get(0).getN().id)) {
                    accept(input);
                }
            }
        }

        /**
         * If the automate is in an accept state, accept the input
         */
        if (accept.contains(state.id)) {
            accept(input);
        } else {
            reject(input);
        }
    }

    /**
     * Alers the user of a syntax error in the program
     *
     * @param msg alert message
     */
    public static void syntaxError(String msg) {
        throw new RuntimeException("Syntax Error: " + msg);
    }


    /**
     * The automata accepts the input string
     *
     * @param input input stringD
     */
    public static void accept(String input) {
        if (!hasAccepted) {
            if (testMode) {
                results.put(input, "ACCEPT");
            } else System.out.println("ACCEPT");
        }
        hasAccepted = true;
    }

    /**
     * The automata does not accept the input string
     *
     * @param input input string
     */
    public static void reject(String input) {
        if (!hasAccepted) {
            if (testMode) {
                results.put(input, "REJECT");
            } else System.out.println("REJECT");
        }
    }

    /**
     * Generates all possible inputs of length <= j over alphabet
     *
     * @param j max length of input
     */
    private static void gen(int j) {

        char[] ch = new char[alpha.size()];
        for (int i = 0; i < alpha.size(); i++) {
            ch[i] = alpha.get(i);
        }

        for (int i = 0; i <= j; i++) {
            addAllPossibleCombination(ch, i, "");
        }

    }

    private static void addAllPossibleCombination(char[] ch, int k, String prefix) {
        if (prefix.length() == k) {
            allCombinations.add(prefix);
            return;
        } else {
            for (int i = 0; i < ch.length; i++) {
                addAllPossibleCombination(ch, k, prefix + ch[i]);
            }
        }

    }

}
