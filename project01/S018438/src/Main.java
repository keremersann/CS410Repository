import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("DFA1: \n");
		NFA nfa1 = new NFA();
		nfa1.loadAndSetNFA("NFA1.txt");
		nfa1.convertNFAtoDFA(nfa1.getStartState());
		nfa1.printDFA();
		
		System.out.println("----------------------------------------");
		System.out.println("DFA2: \n");
		NFA nfa2 = new NFA();
		nfa2.loadAndSetNFA("NFA2.txt");
		nfa2.convertNFAtoDFA(nfa2.getStartState());
		nfa2.printDFA();
	}

}