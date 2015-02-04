import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GorillaOrSeaCucumber {

	private static HashMap<String, Integer> costMatrix;
	private static String[] letters;
	private static int[][] M;
	private static int dashValue;

	public static void main(String[] args) {
		ArrayList<Organism> organisms = readInputFile("HbB_FASTAs.in");
		costMatrix = readMatrixFile();
		dashValue = getDashCost('A');
		
		for (int i = 0; i < organisms.size() - 1; i++) {
			Organism org1 = organisms.get(i);
			int length1 = org1.getProteinSequence().length;
			for (int j = i + 1; j < organisms.size(); j++) {
				Organism org2 = organisms.get(j);
				int length2 = org2.getProteinSequence().length;

				M = new int[length1][length2];
				for (int k = 0; k < length1; k++) {
					for (int l = 0; l < length2; l++) {
						M[k][l] = Integer.MIN_VALUE;
					}
				}
				long start = System.currentTimeMillis();
				int opt = opt(length1 - 1, length2 - 1,
						org1.getProteinSequence(), org2.getProteinSequence());
				long end = System.currentTimeMillis();
				System.out.println((end - start) + " ms");
				System.out.println(org1.getName() + "--" + org2.getName()
						+ ": " + opt);
				System.out.println(getSequenceStrings(org1.getProteinSequence(),
						org2.getProteinSequence()));
			}

		}

	}

	public static String getSequenceStrings(char[] A, char[] B) {
		StringBuilder aligmentA = new StringBuilder();
		StringBuilder aligmentB = new StringBuilder();
		//indexes
		int i = A.length - 1; 
		int j = B.length - 1;

		while (i > 0 || j > 0) {
			if (i > 0 && j > 0 && M[i][j] == M[i - 1][j - 1] + getCost(A[i], B[j])) { //match
				aligmentA.append(A[i--]);
				aligmentB.append(B[j--]);
			} else if (i > 0 && M[i][j] == M[i - 1][j] + dashValue) { //delete
				aligmentA.append(A[i--]);
				aligmentB.append("-");
			} else if (j > 0 && M[i][j] == M[i][j - 1] + dashValue) { //insert
				aligmentA.append("-");
				aligmentB.append(B[j--]);
			} else {
				break;
			}
		}
		aligmentA.append(A[0]);
		aligmentB.append(B[0]);
		return aligmentB.append("\n").append(aligmentA).reverse().toString();
	}

	public static int opt(int n, int m, char[] seq1, char[] seq2) {
		if (n < 0) {
			return (m + 1) * dashValue;
		} else if (m < 0) {
			return (n + 1) * dashValue;
		}

		if (M[n][m] != Integer.MIN_VALUE) {
			return M[n][m];
		}
		// System.out.println(seq1[n]+""+seq2[m]);
		int first = getCost(seq1[n], seq2[m]) + opt(n - 1, m - 1, seq1, seq2);
		int second = getDashCost(seq1[n]) + opt(n - 1, m, seq1, seq2);
		int third = getDashCost(seq2[m]) + opt(n, m - 1, seq1, seq2);
		int max = max(first, second, third);
		M[n][m] = max;
		return max;
	}

	private static Integer getCost(char n, char m) {
		return costMatrix.get(n + "" + m);
	}

	private static Integer getDashCost(char c) {
		return costMatrix.get(c + "" + letters[letters.length - 1]);
	}

	public static int max(int first, int second, int third) {
		return Math.max(first, Math.max(second, third));
	}

	@SuppressWarnings("resource")
	public static HashMap<String, Integer> readMatrixFile() {
		File file = new File("BLOSUM62.txt");
		HashMap<String, Integer> costs = new HashMap<String, Integer>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (Exception e) {
			System.out.println("Please add the filename to the arguments.");
			System.exit(0);
		}
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if (!line.startsWith("#")) {
				letters = line.split("\\s+");
				for (int i = 0; i < letters.length; i++) {
					String[] cost = scanner.nextLine().trim().split("\\s+");
					for (int j = 1; j < cost.length; j++) {
						String combi = letters[i] + letters[j - 1];
						int value = Integer.parseInt(cost[j]);
						costs.put(combi, value);
					}
				}
			}
		}

		return costs;

	}

	@SuppressWarnings("resource")
	private static ArrayList<Organism> readInputFile(String fileName) {
		File file = new File(fileName);
		ArrayList<Organism> organisms = new ArrayList<Organism>();
		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
		} catch (Exception e) {
			System.out.println("Please add the filename to the arguments.");
			System.exit(0);
		}
		try {
			String line = scanner.nextLine().trim();
			while (scanner.hasNext()) {
				String[] splitLine = line.split("\\s+");
				String name = splitLine[0].substring(1);
				String proteinSeq = "";
				while (scanner.hasNext()) {
					line = scanner.nextLine().trim();
					if (line.startsWith(">")) {
						break;
					} else {
						proteinSeq += line;
					}
				}
				organisms.add(new Organism(name, proteinSeq.toCharArray()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// System.out.println("file error: " + fileName);
			e.printStackTrace();
		}
		return organisms;
	}
}

class Organism {
	private String name;
	private char[] proteinSequence;

	public String getName() {
		return name;
	}

	public char[] getProteinSequence() {
		return proteinSequence;
	}

	public Organism(String name, char[] proteinSequnce) {
		this.name = name;
		this.proteinSequence = proteinSequnce;
	}
}
