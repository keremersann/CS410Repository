import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Util {

	public static String unionOperation(String firstString, String secondString) {
		String unionedString = "";
		String[] firstStringSplit = firstString.split(" ");
		String[] secondStringSplit = secondString.split(" ");

		for (int i = 0; i < firstStringSplit.length; i++) {
			if (!unionedString.contains(firstStringSplit[i])) {
				unionedString = unionedString + " " + firstStringSplit[i];
			}
		}

		for (int i = 0; i < secondStringSplit.length; i++) {
			if (!unionedString.contains(secondStringSplit[i])) {
				unionedString = unionedString + " " + secondStringSplit[i];
			}
		}

		return unionedString.trim();
	}

	public static String getTargetState(String state, String alph,
			HashMap<String, HashMap<String, String>> NFAtransitions) {
		String finalStr = "";
		for (String subState : state.split(" ")) {
			if (NFAtransitions.keySet().contains(subState)) {
				if (NFAtransitions.get(subState).keySet().contains(alph)) {
					finalStr = Util.unionOperation(finalStr, NFAtransitions.get(subState).get(alph));
				} else {
					finalStr = Util.unionOperation(finalStr, "");
				}
			} else {
				finalStr = Util.unionOperation(finalStr, "");
			}
		}
		return finalStr.trim();
	}

}
