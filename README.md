# PDARun
Simple interpreter for PDA programs (Language for designing and testing pushdown automatas)

PDARun lets you build pushdown automatas (also supposrts DFAs and NFAs) with simple and flexible code. Once the interpreter
has parsed your code, it will ask you for a string to test and run it through the automata you've built. You can
also type *inputs n* (n = positive integer) and the interpreter will show you all the strings that are accepted and rejected
that have a length <= n over the alphabet you declared in your program.

The syntax of the program should be as followed:

    //Name of the starting state
    start:s;

    //PDA's alphabet
    alpha{a,b,c};

    //The states of the PDA
    states{s,q1,q2,F};

    //Set of accept states
    accept{F,q2};

    //Transition function
    //The following means if the machine is in state s, on input 'a' with a top
    //stack symbol of ~ (~ is the notation for epsilon, or the empty string), go to 
    //state q1 and push $ onto the stack.
    delta{

    s(a,~) goto q1 push $;
    //rule2
    //rule3
    //etc...
    }

The order of the declarations is interchangeable. The language is case-sensitive and whitespaces are only allowed
in between the declarations or inside the delta brackets.

Run the install script on Linux to use the interpreter from CLI anytime for a .pda file as follows:
$ pdarun file.pda

