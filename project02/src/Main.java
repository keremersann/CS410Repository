import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
	
	private String input;
	private int lineCount;
	private String foundEpselon = "";

	// map variable with production ( variable -> production)
	private Map<String, List<String>> mapVariableProduction = new LinkedHashMap<>();

	public static void main(String args[]) throws FileNotFoundException {
		String textFile = "G2.txt";
		String CFGrules = Utils.getCFGRules(textFile);
		int line_count = Utils.getRuleCount(textFile);
		Main c = new Main();
		c.setInputandLineCount(CFGrules, line_count);
		c.convertCFGtoCNF();
		c.printTheCNF();
	}

	public static class Utils {
		public static String getCFGRules(String fileName) throws FileNotFoundException {
			String pRules = "";
			File inputFile = new File(fileName);
			Scanner scanner = new Scanner(inputFile);
			boolean isRule = false;
			Map<String, String> map = new HashMap<String, String>();

			while (scanner.hasNextLine()) {
				String scannedLine = scanner.nextLine();

				if (scannedLine.equals("RULES")) {
					isRule = true;
					continue;
				}
				if (scannedLine.equals("START")) {
					break;
				}
				if (isRule) {
					String firstLetter = Character.toString(scannedLine.charAt(0));
					if (map.keySet().contains(firstLetter)) {
						String newValue = map.get(firstLetter) + "|" + scannedLine.substring(2);
						map.put(firstLetter, newValue);
					} else {
						String newValue = "->" + scannedLine.substring(2);
						map.put(firstLetter, newValue);
					}
				}
			}

			for (String name : map.keySet()) {
				pRules += name + map.get(name) + "\n";
			}
			pRules = pRules.substring(0, pRules.length() - 1);
			return pRules;
		}

		public static int getRuleCount(String fileName) throws FileNotFoundException {
			String pRules = "";
			File inputFile = new File(fileName);
			Scanner scanner = new Scanner(inputFile);
			boolean isRule = false;
			int count = 0;
			Map<String, String> map = new HashMap<String, String>();

			while (scanner.hasNextLine()) {
				String scannedLine = scanner.nextLine();

				if (scannedLine.equals("RULES")) {
					isRule = true;
					continue;
				}
				if (scannedLine.equals("START")) {
					break;
				}
				if (isRule) {
					String firstLetter = Character.toString(scannedLine.charAt(0));
					if (map.keySet().contains(firstLetter)) {
						String newValue = map.get(firstLetter) + "|" + scannedLine.substring(2);
						map.put(firstLetter, newValue);
					} else {
						String newValue = "->" + scannedLine.substring(2);
						map.put(firstLetter, newValue);
					}
				}
			}

			for (String name : map.keySet()) {
				pRules += name + map.get(name) + "\n";
			}
			pRules = pRules.substring(0, pRules.length() - 1);
			String[] splittedRules = pRules.split("\n");

			return splittedRules.length;
		}
	}
	
	public void printTheCNF() {
		Set nonTerminals = mapVariableProduction.keySet();
		List<String> terminals = new ArrayList<String>();
		for (String key : mapVariableProduction.keySet()) {
			List<String> values = mapVariableProduction.get(key);
			for (String value : values) {
				if(value.length() == 1) {
					if(Character.isLowerCase(value.charAt(0)) || Character.isDigit(value.charAt(0))) {
						terminals.add(value);
					}
				}
			}
		}
		String nonTerminalText = "NON-TERMINAL\n";
		for (Object nonTerminal : nonTerminals) {
			nonTerminalText += nonTerminal.toString() + "\n";
		}
		
		String terminalText = "TERMINAL\n";
		for (String terminal : terminals) {
			terminalText += terminal + "\n";
		}
		String rulesText = "RULES\n";
		for (String key : mapVariableProduction.keySet()) {
			for (String value : mapVariableProduction.get(key)) {
				rulesText += key + ":" + value + "\n";
			}
		}
		String firstElement = "START\n" + (String)nonTerminals.iterator().next();
		System.out.println(nonTerminalText + terminalText + rulesText + firstElement);
		
	}

	public void setInputandLineCount(String input, int lineCount) {
		this.input = input;
		this.lineCount = lineCount;

	}

	public Map<String, List<String>> getMapVariableProduction() {
		return mapVariableProduction;
	}

	public void convertCFGtoCNF() {
		insertNewStartSymbol();
		convertStringtoMap();
		eliminateEpselon();
		removeDuplicateKeyValue();
		eliminateSingleVariable();
		onlyTwoTerminalandOneVariable();
		eliminateThreeTerminal();
	}

	private void eliminateSingleVariable() {

		//System.out.println("Remove Single Variable in Every Production ... ");

		for (int i = 0; i < lineCount; i++) {
			removeSingleVariable();
		}

		//printMap();

	}

	private void eliminateThreeTerminal() {

		//System.out.println("Replace two terminal variable with new variable ... ");

		for (int i = 0; i < lineCount; i++) {
			removeThreeTerminal();
		}

		//printMap();

	}

	private void eliminateEpselon() {

		//System.out.println("\nRemove Epselon....");

		for (int i = 0; i < lineCount; i++) {
			removeEpselon();
		}

		//printMap();

	}

	private String[] splitEnter(String input) {

		String[] tmpArray = new String[lineCount];
		for (int i = 0; i < lineCount; i++) {
			tmpArray = input.split("\\n");
		}
		return tmpArray;
	}

	private void convertStringtoMap() {

		String[] splitedEnterInput = splitEnter(input);

		for (int i = 0; i < splitedEnterInput.length; i++) {

			String[] tempString = splitedEnterInput[i].split("->|\\|");
			String variable = tempString[0].trim();

			String[] production = Arrays.copyOfRange(tempString, 1, tempString.length);
			List<String> productionList = new ArrayList<String>();

			// trim the empty space
			for (int k = 0; k < production.length; k++) {
				production[k] = production[k].trim();
			}

			// import array into ArrayList
			for (int j = 0; j < production.length; j++) {
				productionList.add(production[j]);
			}

			// insert element into map
			mapVariableProduction.put(variable, productionList);
		}
	}

	private void insertNewStartSymbol() {

		String newStart = "S0";
		ArrayList<String> newProduction = new ArrayList<>();
		newProduction.add("S");

		mapVariableProduction.put(newStart, newProduction);
	}

	private void removeEpselon() {

		Iterator itr = mapVariableProduction.entrySet().iterator();
		Iterator itr2 = mapVariableProduction.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

			if (productionRow.contains("e")) {
				if (productionRow.size() > 1) {
					productionRow.remove("e");
					foundEpselon = entry.getKey().toString();

				} else {

					// remove if less than 1
					foundEpselon = entry.getKey().toString();
					mapVariableProduction.remove(foundEpselon);
				}
			}
		}

		// find B and eliminate them
		while (itr2.hasNext()) {

			Map.Entry entry = (Map.Entry) itr2.next();
			ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < productionList.size(); i++) {
				String temp = productionList.get(i);

				for (int j = 0; j < temp.length(); j++) {
					if (foundEpselon.equals(Character.toString(productionList.get(i).charAt(j)))) {

						if (temp.length() == 2) {

							// remove specific character in string
							temp = temp.replace(foundEpselon, "");

							if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
								mapVariableProduction.get(entry.getKey().toString()).add(temp);
							}

						} else if (temp.length() == 3) {

							String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

							if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
								mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
							}

						} else if (temp.length() == 4) {

							String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

							if (!mapVariableProduction.get(entry.getKey().toString()).contains(deletedTemp)) {
								mapVariableProduction.get(entry.getKey().toString()).add(deletedTemp);
							}
						} else {

							if (!mapVariableProduction.get(entry.getKey().toString()).contains("e")) {
								mapVariableProduction.get(entry.getKey().toString()).add("e");
							}
						}
					}
				}
			}
		}
	}

	private void removeDuplicateKeyValue() {

		//System.out.println("Remove Duplicate Key Value ... ");

		Iterator itr3 = mapVariableProduction.entrySet().iterator();

		while (itr3.hasNext()) {
			Map.Entry entry = (Map.Entry) itr3.next();
			ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < productionRow.size(); i++) {
				if (productionRow.get(i).contains(entry.getKey().toString())) {
					productionRow.remove(entry.getKey().toString());
				}
			}
		}

		//printMap();
	}

	private void removeSingleVariable() {

		Iterator itr4 = mapVariableProduction.entrySet().iterator();
		String key = null;

		while (itr4.hasNext()) {

			Map.Entry entry = (Map.Entry) itr4.next();
			Set set = mapVariableProduction.keySet();
			ArrayList<String> keySet = new ArrayList<String>(set);
			ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < productionList.size(); i++) {
				String temp = productionList.get(i);

				for (int j = 0; j < temp.length(); j++) {

					for (int k = 0; k < keySet.size(); k++) {
						if (keySet.get(k).equals(temp)) {

							key = entry.getKey().toString();
							List<String> productionValue = mapVariableProduction.get(temp);
							productionList.remove(temp);
							for (int l = 0; l < productionValue.size(); l++) {
								if (!mapVariableProduction.get(key).contains(productionValue.get(l)))
									mapVariableProduction.get(key).add(productionValue.get(l));
							}
						}
					}
				}
			}
		}
	}

	private Boolean checkDuplicateInProductionList(Map<String, List<String>> map, String key) {

		Boolean notFound = true;

		Iterator itr = map.entrySet().iterator();
		outerloop:

		while (itr.hasNext()) {

			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < productionList.size(); i++) {
				if (productionList.size() < 2) {

					if (productionList.get(i).equals(key)) {
						notFound = false;
						break outerloop;
					} else {
						notFound = true;
					}
				}
			}
		}

		return notFound;
	}

	private void onlyTwoTerminalandOneVariable() {

		//System.out.println("Assign new variable for two non-terminal or one terminal ... ");

		Iterator itr5 = mapVariableProduction.entrySet().iterator();
		String key = null;
		int asciiBegin = 71; // G

		Map<String, List<String>> tempList = new LinkedHashMap<>();

		while (itr5.hasNext()) {

			Map.Entry entry = (Map.Entry) itr5.next();
			Set set = mapVariableProduction.keySet();

			ArrayList<String> keySet = new ArrayList<String>(set);
			ArrayList<String> productionList = (ArrayList<String>) entry.getValue();
			Boolean found1 = false;
			Boolean found2 = false;
			Boolean found = false;

			for (int i = 0; i < productionList.size(); i++) {
				String temp = productionList.get(i);

				for (int j = 0; j < temp.length(); j++) {

					if (temp.length() == 3) {

						String newProduction = temp.substring(1, 3); // SA

						if (checkDuplicateInProductionList(tempList, newProduction)
								&& checkDuplicateInProductionList(mapVariableProduction, newProduction)) {
							found = true;
						} else {
							found = false;
						}

						if (found) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newProduction);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}

					} else if (temp.length() == 2) { // if only two substring

						for (int k = 0; k < keySet.size(); k++) {

							if (!keySet.get(k).equals(Character.toString(productionList.get(i).charAt(j)))) { // if
																												// substring
																												// not
																												// equals
																												// to
																												// keySet
								found = false;

							} else {
								found = true;
								break;
							}

						}

						if (!found) {
							String newProduction = Character.toString(productionList.get(i).charAt(j));

							if (checkDuplicateInProductionList(tempList, newProduction)
									&& checkDuplicateInProductionList(mapVariableProduction, newProduction)) {

								ArrayList<String> newVariable = new ArrayList<>();
								newVariable.add(newProduction);
								key = Character.toString((char) asciiBegin);

								tempList.put(key, newVariable);

								asciiBegin++;

							}
						}
					} else if (temp.length() == 4) {

						String newProduction1 = temp.substring(0, 2); // SA
						String newProduction2 = temp.substring(2, 4); // SA

						if (checkDuplicateInProductionList(tempList, newProduction1)
								&& checkDuplicateInProductionList(mapVariableProduction, newProduction1)) {
							found1 = true;
						} else {
							found1 = false;
						}

						if (checkDuplicateInProductionList(tempList, newProduction2)
								&& checkDuplicateInProductionList(mapVariableProduction, newProduction2)) {
							found2 = true;
						} else {
							found2 = false;
						}

						if (found1) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newProduction1);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}

						if (found2) {
							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newProduction2);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}
					} else if (temp.length() == 1) {
						String newProduction = Character.toString(productionList.get(i).charAt(j));
						if (checkDuplicateInProductionList(tempList, newProduction)
								&& checkDuplicateInProductionList(mapVariableProduction, newProduction)) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newProduction);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);

							asciiBegin++;

						}
					}
				}
			}
		}
		mapVariableProduction.putAll(tempList);
	}

	private void eliminateTwoNonTerminalsToOne() {
		int count = 0;
		ArrayList<String> oneValueKeys = getKeysOneValues();
		
		for (String oneValueKey : oneValueKeys) {
			for (String key : mapVariableProduction.keySet()) {
				for (String value : mapVariableProduction.get(key)) {
					if(value.contains(oneValueKey)) {
						int index = value.indexOf(oneValueKey);
						//mapVariableProduction.
					}
				}
			}
		}
	}
	
	private ArrayList<String> getKeysOneValues() {
		Iterator itr = mapVariableProduction.entrySet().iterator();
		ArrayList<String> keyList = new ArrayList<>();
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

			if (productionRow.size() < 2) {
				keyList.add(entry.getKey().toString());
			}
		}
		return keyList;
	}

	private void removeThreeTerminal() {

		Iterator itr = mapVariableProduction.entrySet().iterator();
		ArrayList<String> keyList = new ArrayList<>();
		Iterator itr2 = mapVariableProduction.entrySet().iterator();

		// obtain key that use to eliminate two terminal and above
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> productionRow = (ArrayList<String>) entry.getValue();

			if (productionRow.size() < 2) {
				keyList.add(entry.getKey().toString());
			}
		}

		// find more than three terminal or combination of variable and terminal to
		// eliminate them
		while (itr2.hasNext()) {

			Map.Entry entry = (Map.Entry) itr2.next();
			ArrayList<String> productionList = (ArrayList<String>) entry.getValue();

			if (productionList.size() > 1) {
				for (int i = 0; i < productionList.size(); i++) {
					String temp = productionList.get(i);

					for (int j = 0; j < temp.length(); j++) {

						if (temp.length() > 2) {
							String stringToBeReplaced1 = temp.substring(j, temp.length());
							String stringToBeReplaced2 = temp.substring(0, temp.length() - j);

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = mapVariableProduction.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								if (stringToBeReplaced1.equals(value)) {

									mapVariableProduction.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(stringToBeReplaced1, key);

									if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
										mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
									}
								} else if (stringToBeReplaced2.equals(value)) {

									mapVariableProduction.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(stringToBeReplaced2, key);

									if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
										mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						} else if (temp.length() == 2) {

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = mapVariableProduction.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								for (int pos = 0; pos < temp.length(); pos++) {
									String tempChar = Character.toString(temp.charAt(pos));

									if (value.equals(tempChar)) {

										mapVariableProduction.get(entry.getKey().toString()).remove(temp);
										temp = temp.replace(tempChar, key);

										if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
											mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
										}
									}
								}
							}
						} else if (temp.length() == 1) {

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = mapVariableProduction.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								if (value.equals(temp)) {

									mapVariableProduction.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(temp, key);

									if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
										mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						}

					}
				}
			} else if (productionList.size() == 1) {

				for (int i = 0; i < productionList.size(); i++) {
					String temp = productionList.get(i);

					if (temp.length() == 2) {

						for (String key : keyList) {

							List<String> keyValues = new ArrayList<>();
							keyValues = mapVariableProduction.get(key);
							String[] values = keyValues.toArray(new String[keyValues.size()]);
							String value = values[0];

							for (int pos = 0; pos < temp.length(); pos++) {
								String tempChar = Character.toString(temp.charAt(pos));

								if (value.equals(tempChar)) {

									mapVariableProduction.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(tempChar, key);

									if (!mapVariableProduction.get(entry.getKey().toString()).contains(temp)) {
										mapVariableProduction.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						}

					}
				}
			}
		}
	}
}
