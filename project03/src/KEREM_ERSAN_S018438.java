import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class KEREM_ERSAN_S018438 {
    public static void main(String[] args)
	{
        TuringMachine m = new TuringMachine();

        try {
			Scanner scanner = new Scanner(new File("input.txt"));

			m.loadTuringMachine(scanner);
            m.setTape();
			m.initalizeTheMachine(41);
            m.printResult();
		}
		catch (FileNotFoundException | InterruptedException e) {
			System.out.println(e);
			System.exit(0);		
			}
    }
}

class State {
	ArrayList<Transition> trans;

	State(ArrayList<Transition> ts){ 
		trans = ts; 
    }
}

class Transition {
    
	char inputRead;
	char inputWrite;
    char directionOfShift;
    String nextState;
    String currState;
    
    Transition(String s){
        currState = s.substring(0, 2);
        inputRead = s.charAt(3);
        inputWrite = s.charAt(5);
        directionOfShift = s.charAt(7);
        nextState = s.substring(9);
    }

    boolean isTransitionsEqual(Transition t){
        if(t.currState.equals(this.currState) &&
           t.inputRead == this.inputRead &&
           t.inputWrite == this.inputWrite &&
           t.directionOfShift == this.directionOfShift &&
           t.nextState.equals(this.nextState)){
            return true;
           }
           return false;
    }
}

class TuringMachine {
    Scanner lineScanner;

    int inputAlphabetSize; 
    int tapeAlphabetSize;
    int stateCount;
    int transitionCount;
    char blankSymbol;
    String currState; 
    String inputString;
    String loopState = "qL";
    String acceptState;
    String rejectState;
    String reachedStates = "";

    StringBuffer inputTape;

    ArrayList<State> states = new ArrayList<>();

    HashMap<String, Integer> mapStateNumber = new HashMap<>();

    void loadTuringMachine(Scanner lineScanner){
        this.lineScanner = lineScanner;

        inputAlphabetSize = Integer.parseInt(lineScanner.nextLine());
        lineScanner.nextLine();
        tapeAlphabetSize = Integer.parseInt(lineScanner.nextLine());
        lineScanner.nextLine(); 
        blankSymbol = lineScanner.nextLine().charAt(0);
        stateCount = Integer.parseInt(lineScanner.nextLine());
        lineScanner.nextLine(); 
        currState = lineScanner.nextLine();
        acceptState = lineScanner.nextLine();
        rejectState = lineScanner.nextLine();

        transitionCount = (stateCount-2)*(tapeAlphabetSize+1);

        for(int i=0; i<stateCount-2; i++){
            stateAdd(i);
        }

        inputString = lineScanner.nextLine();
    }
    
    void setTape(){
        String defaultTape = "-";

        for(int i=0; i<40; i++){
            defaultTape += blankSymbol;
        }

        defaultTape = defaultTape.concat(inputString);

        for(int i=0; i<40; i++){
            defaultTape += blankSymbol;
        }

        defaultTape += '-';

        inputTape = new StringBuffer(defaultTape);
    }
    
    void printResult(){
        System.out.println("ROUT: " + reachedStates);
        
        if(currState.equals(rejectState)){
            System.out.println("RESULT: rejected");
        }
        else if(currState.equals(acceptState)){
            System.out.println("RESULT: accepted");
        }
        else{
            System.out.println("RESULT: it is in the loop!");
        }
    }

    void stateAdd(int stateId){
        ArrayList<Transition> transitions = new ArrayList<>();

        for(int j=0; j<tapeAlphabetSize+1; j++){
            Transition transition = new Transition(lineScanner.nextLine());
            transitions.add(transition);
        }

        mapStateNumber.put(transitions.get(0).currState, stateId);

        State state = new State(transitions);
        states.add(state);
    }

    

    void initalizeTheMachine(int index) throws InterruptedException{

        ArrayList<Transition> FinalTransitions = new ArrayList<>();

        while (!currState.equals(acceptState) && !currState.equals(rejectState)
                && !currState.equals(loopState)){
            reachedStates = reachedStates + currState + " ";
            
            State state = states.get(mapStateNumber.get(currState));

            for(Transition transition : state.trans){
                if(transition.inputRead == inputTape.charAt(index)){
                    
                    if(FinalTransitions.size() == 3 &&
                       ((transition.isTransitionsEqual(FinalTransitions.get(1)) &&
                       FinalTransitions.get(0).isTransitionsEqual(FinalTransitions.get(2))) ||
                       (transition.isTransitionsEqual(FinalTransitions.get(2))))){
        
                        currState = loopState;
                        break;
                    }
                    
                    if(FinalTransitions.size() == 3){
                        FinalTransitions.set(0, FinalTransitions.get(1));
                        FinalTransitions.set(1, FinalTransitions.get(2));
                        FinalTransitions.set(2, transition);
                    }
                    else{
                        FinalTransitions.add(transition);
                    }

                    inputTape.replace(index, index+1, 
                                String.valueOf(transition.inputWrite));
                    
                    currState = transition.nextState;

                    if(transition.directionOfShift == 'R'){
                        index++;
                    }
                    else if(transition.directionOfShift == 'L'){
                        index--;
                    }
                    else{
                        throw new InterruptedException("ERROR: symbol of directionOfShift is wrong!");
                    }

                    break;

                }
            }
        }
        reachedStates += currState;
    }
}