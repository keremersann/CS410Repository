import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NFA {
	public ArrayList<String> alphabetSymbolsList;
	public ArrayList<String> statesList;
	public String startState;
	public ArrayList<String> endstatesList;
	public HashMap<String, HashMap<String, String>> NFAtransitions;
	public HashMap<String, HashMap<String, String>> DFAtransitions;
	private static String scannedLine;

	public NFA() {
		alphabetSymbolsList = new ArrayList<String>();
		statesList = new ArrayList<String>();
		startState = null;
		endstatesList = new ArrayList<String>();
		NFAtransitions = new HashMap<>();
		DFAtransitions = new HashMap<>();
		scannedLine = null;
	}

	public ArrayList<String> getAlphabetSymbolsList() {
		return alphabetSymbolsList;
	}

	public void setAlphabetSymbolsList(ArrayList<String> alphabetSymbolsList) {
		this.alphabetSymbolsList = alphabetSymbolsList;
	}

	public ArrayList<String> getStatesList() {
		return statesList;
	}

	public void setStatesList(ArrayList<String> statesList) {
		this.statesList = statesList;
	}

	public String getStartState() {
		return startState;
	}

	public void setStartState(String startState) {
		this.startState = startState;
	}

	public ArrayList<String> getEndstatesList() {
		return endstatesList;
	}

	public void setEndstatesList(ArrayList<String> endstatesList) {
		this.endstatesList = endstatesList;
	}

	public HashMap<String, HashMap<String, String>> getNFAtransitions() {
		return NFAtransitions;
	}

	public void setNFAtransitions(HashMap<String, HashMap<String, String>> nFAtransitions) {
		NFAtransitions = nFAtransitions;
	}

	public HashMap<String, HashMap<String, String>> getDFAtransitions() {
		return DFAtransitions;
	}

	public void setDFAtransitions(HashMap<String, HashMap<String, String>> dFAtransitions) {
		DFAtransitions = dFAtransitions;
	}

	public void loadAndSetNFA(String fileName) throws FileNotFoundException {
		File inputFile = new File(fileName);
		Scanner scanner = new Scanner(inputFile);
		scanner.nextLine();

		while (scanner.hasNextLine()) {
			scannedLine = scanner.nextLine();
			if (scannedLine.equals("STATES")) {
				break;
			}
			alphabetSymbolsList.add(scannedLine);
		}
		while (scanner.hasNextLine()) {
			scannedLine = scanner.nextLine();
			if (scannedLine.equals("START")) {
				break;
			}
			statesList.add(scannedLine);
		}
		startState = scanner.nextLine();
		scanner.nextLine();
		while (scanner.hasNextLine()) {
			scannedLine = scanner.nextLine();
			if (scannedLine.equals("TRANSITIONS")) {
				break;
			}
			endstatesList.add(scannedLine);
		}
		while (scanner.hasNextLine()) {
			scannedLine = scanner.nextLine();
			if (scannedLine.equals("END")) {
				break;
			}
			String[] connect = scannedLine.split(" ");
			if (NFAtransitions.containsKey(connect[0])) {
				if (NFAtransitions.get(connect[0]).keySet().contains(connect[1])) {
					NFAtransitions.get(connect[0]).put(connect[1],
							NFAtransitions.get(connect[0]).get(connect[1]) + " " + connect[2]);
				} else {
					NFAtransitions.get(connect[0]).put(connect[1], connect[2]);
				}
			} else {
				NFAtransitions.put(connect[0], new HashMap<String, String>());
				NFAtransitions.get(connect[0]).put(connect[1], connect[2]);
			}
		}
	}

	public void convertNFAtoDFA(String startState) {
		DFAtransitions.put(startState, new HashMap<String, String>());
		for (String alph : alphabetSymbolsList) {
			String targetState = Util.getTargetState(startState, alph, NFAtransitions);
			DFAtransitions.get(startState).put(alph, targetState);
			boolean isRepeated = false;
			for (String subState : DFAtransitions.keySet()) {
				if (subState.equals(targetState)) {
					isRepeated = true;
					break;
				}
			}
			if (!isRepeated) {
				convertNFAtoDFA(targetState);
			}
		}
	}

	public void printDFA() {
		System.out.println("ALPHABET");
		for (String symbol : alphabetSymbolsList) {
			System.out.println(symbol);
		}
		System.out.println("STATES");
		for (String state : DFAtransitions.keySet()) {
			if (state == "") {
				System.out.println("SINK-STATE");
			} else {
				System.out.println(state.replaceAll("\\s", ""));
			}
		}
		System.out.println("START");
		System.out.println(startState);
		System.out.println("FINAL");
		for (String state : DFAtransitions.keySet()) {
			for (String endState : endstatesList) {
				if (state.contains(endState)) {
					System.out.println(state.replaceAll("\\s", ""));
				}
			}
		}
		System.out.println("TRANSITIONS");
		for (String state : DFAtransitions.keySet()) {
			for (String symbol : alphabetSymbolsList) {
				if (state == "") {
					System.out.println("SINK-STATE " + symbol + " SINK-STATE");
				} else {
					System.out.println(state.replaceAll("\\s", "") + " " + symbol + " "
							+ DFAtransitions.get(state).get(symbol).replaceAll("\\s", ""));
				}
			}
		}
	}

}
