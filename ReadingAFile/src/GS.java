import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class GS {

	static HashMap<Integer, String> map;
	static ArrayList<int[]> preferences;
	private static ArrayList<int[]> proposals = new ArrayList<>();;
	private static int n;
	private static Scanner scanner;
	private static boolean debug = false;

	public static void main(String[] args) {
		if(!(args.length > 0)){
			System.out.println("please write GS + \"filename\"");
			System.exit(0);
		}
		
		readContentOfFile(args[0]);
		
		if(args.length > 1){
			debug = args[1].equalsIgnoreCase("true");
		}
		printFileContent();

		findStableMatches();

		printLog("-----------------------------------------------------------------");
		ArrayList<String> matchingList = new ArrayList<String>();
		addMatchesToList(matchingList);

		String outFileName = args[0].substring(0, args[0].lastIndexOf(".")) + ".out";
		printLog("outFileName= "+ outFileName);
		
		File outFile = new File(outFileName);
		if(!outFile.exists()){
			System.exit(0);
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(outFile));
			ArrayList<String> comparisonFile = new ArrayList<String>();
			String line = br.readLine();
			while (line != null) {
				comparisonFile.add(line);
				line = br.readLine();
			}
			br.close();
			int correctMatches = 0;
			for (int i = 0; i < matchingList.size(); i++) {
				if (!matchIsCorrect(matchingList, comparisonFile, i)) {
					System.out.println("Match not found for i=" + i + "  "
							+ comparisonFile.get(i));
				} else {
					correctMatches++;
				}
			}
			System.out.println("-----------------------------------------------------------------");
			System.out.println("\n"+correctMatches + "/" + matchingList.size()
					+ " matches verified");
			System.out.println("-----------------------------------------------------------------");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addMatchesToList(ArrayList<String> matchingList) {
		for (int i = 0; i < proposals.size(); i++) {
			String match = getNameFromId(proposals.get(i)[0]) + " -- "
					+ getNameFromId(proposals.get(i)[1]);
			System.out.println(match);
			matchingList.add(match);
			i++;
		}
	}

	private static boolean matchIsCorrect(ArrayList<String> matchingList,
			ArrayList<String> comparisonFile, int i) {
		return matchingList.contains(comparisonFile.get(i));
	}

	private static void findStableMatches() {
		Set<Integer> set = map.keySet();
		for (Integer key : set) {
			proposals.add(new int[] { key, -1 });
			printLog(""+key);
		}
		while (getAvailableMale() != -1) {
			int m = getAvailableMale();
			printLog("chose man " + m);
			if (m != -1) {
				if (!hasManProposedToEveryWoman(m)) {

					printLog("has not proposed to every woman");
					int w = getNextWomen(m);
					printLog("w = " + w);
					if (isFree(w)) {
						engageManAndWoman(m, w);
					} else { // w is currently engaged to m'

						int mp = proposals.get(w - 1)[1];
						int[] womansPrefList = preferences.get(w - 1);
						int indexOfMp = -1;
						int indexOfm = -1;
						for (int i = 0; i < womansPrefList.length; i++) {
							if (womansPrefList[i] == mp) {
								indexOfMp = i;
								printLog("index of mp: " + i);
							} else if (womansPrefList[i] == m) {
								indexOfm = i;
								printLog("index of m: " + i);
							}
						}
						// if w prefers m' to m then m remains free
						boolean prefersCurrentProposal = indexOfMp < indexOfm;
						if (prefersCurrentProposal) {
							setAsProposed(m, w);
						} else {
							// else w prefers m to m', (m,w) become engaged and
							// m' becomes free
							engageManAndWoman(m, w);
							proposals.get(mp - 1)[1] = -1;
						}
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
	}

	private static String getNameFromId(int id) {
		return map.get(id);
	}

	private static void engageManAndWoman(int m, int w) {
		printLog("Engage " + m + " and " + w);
		proposals.get(m - 1)[1] = w;
		proposals.get(w - 1)[1] = m;
		setAsProposed(m, w);
	}

	private static void setAsProposed(int m, int w) {
		int indexOfWomen = -1;
		for (int i = 0; i < preferences.size(); i++) {
			int[] prefsForPerson = preferences.get(i);
			if (prefsForPerson[0] == m) {
				for (int j = 1; j < prefsForPerson.length; j++) {
					if (w == prefsForPerson[j]) {
						indexOfWomen = j;
						printLog("index of woman: " + j);
						break;
					}
				}
			}
		}
		preferences.get(m - 1)[indexOfWomen] = -1;
	}

	private static boolean isFree(int w) {
		return proposals.get(w - 1)[1] == -1;
	}

	private static int getNextWomen(int m) {
		int[] list = preferences.get(m - 1);
		for (int i = 1; i < list.length; i++) {
			if (list[i] != -1) {
				return list[i];
			}
		}
		return -1;
	}

	private static boolean hasManProposedToEveryWoman(int m) {
		for (int i = 0; i < preferences.size(); i++) {
			int[] list = preferences.get(i);
			if (list[0] == m) {
				for (int j = 1; j < list.length; j++) {
					if (list[j] != -1) {
						return false;
					}
				}
				break;
			}
		}
		return true;
	}

	private static int getAvailableMale() {
		for (int i = 0; i < proposals.size(); i++) {
			int[] person = proposals.get(i);
			if (person[0] % 2 != 0) {
				if (person[1] == -1) {
					printLog("return male = " + person[0]);
					return person[0];
				}
			}
		}
		printLog("found no man that is free");
		return -1;
	}

	private static void readContentOfFile(String fileName) {
		File file = new File(fileName);
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		n = -1;
		map = new HashMap<Integer, String>();
		preferences = new ArrayList<int[]>();

		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.startsWith("#")) {
				printLog("Starts with #");
			} else if (line.startsWith("n=")) {
				n = Integer.parseInt(line.substring(2));
				printLog("n=" + n);
				
				for (int i = 0; i < (n * 2); i++) {
					String keyAndName = scanner.nextLine();
					String[] keySplitName = keyAndName.split(" ");
					map.put(Integer.parseInt(keySplitName[0]),
							keySplitName[1].trim());
				}
				scanner.nextLine(); // empty line in text file
				line = scanner.nextLine();
				
				printLog("enter first preference");
				for (int i = 0; i < (n * 2); i++) {
					String[] preferenceLine = line.split(":");
					int personKey = Integer.parseInt(preferenceLine[0]);
					String[] prefs = preferenceLine[1].trim().split(" ");
					int[] prefArr = new int[n + 1];
					prefArr[0] = personKey;
					for (int j = 1; j < prefArr.length; j++) {
						prefArr[j] = Integer.parseInt(prefs[j - 1]);
					}
					preferences.add(prefArr);
					if (scanner.hasNext()) {
						line = scanner.nextLine();
					}
				}
			}
		}
	}

	public static void printFileContent() {
		printLog(map.toString());

		for (int i = 0; i < preferences.size(); i++) {
			int[] p = preferences.get(i);
			for (int list : p) {
				printLog("" + list + " ");
			}
		}
	}

	private static void printLog(String log) {
		if (debug == true) {
			System.out.println(log);
		}
	}

}