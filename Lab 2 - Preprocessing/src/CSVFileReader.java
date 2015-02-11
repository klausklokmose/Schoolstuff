import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The CSVFileReader class is used to load a csv file
 * 
 * @author andershh and hjab
 *
 */
public class CSVFileReader {
	public final static String normalized = "NORMALIZED";
	public final static String height = "Height";
	public final static String topicAlg = "Which topics would you prefer to learn in this course? [Use off-the-shelf data mining tools]";
	public final static String videoGames = "How often do you play video games?";
	public final static String tallerThanHector = "How tall are you compared to Héctor?";
	public final static String whyCourse = "Why are you taking this course?";
	
	/*
	 * Transformation table of how much time, people use on video games every week
		Never				:= 0
		< 5 hours a week	:= 1
		< 10 hours a week	:= 2
		< 20 hours a week	:= 3
		> 20 hours a week	:= 4
	*/
	public final static String[] videoGamesUsageList = { "Never",
		"< 5 hours a week",
		"< 10 hours a week",
		"< 20 hours a week",
		"> 20 hours a week" };
	
	/*
	 * Transformation table of how much time, people use on video games every week
		Héctor is taller than me		:= 0
		I am exactly as tall as Héctor	:= 1
		I am taller than Héctor		:= 2
	*/
	public final static String[] tallerThanHectorList = { "Héctor is taller than me",
		"I am exactly as tall as Héctor",
		"I am exactly as tall as Héctor",
		"I am taller than Héctor" };

	public final static String[] interestedList = { "Meh",
		"Not interested",
		"Sounds interesting",
		"Very interested" };
	/*
	  Transformation table (String to "integer")
	  
	  	NO ANSWER or written answer			:= -1
	   	I am interested in the subject 		:= 0 
	  	This was a mandatory course for me 	:= 1 
	  	It may help me to find a job		:= 2
	 */
	public final static String[] ps = { "I am interested in the subject",
			"This was a mandatory course for me",
			"It may help me to find a job" };
	
	/**
	 * The read method reads in a csv file as a two dimensional string array.
	 * This method is utilizes the string.split method for splitting each line
	 * of the data file. String tokenizer bug fix provided by Martin Marcher.
	 * 
	 * @param csvFile
	 *            File to load
	 * @param seperationChar
	 *            Character used to seperate entries
	 * @param nullValue
	 *            What to insert in case of missing values
	 * @return Data file content as a 2D string array
	 * @throws IOException
	 */
	public static String[][] readDataFile(String csvFile,
			String seperationChar, String nullValue,
			boolean checkForQuoteMarkRange) throws IOException {
		List<String[]> lines = new ArrayList<String[]>();
		BufferedReader bufRdr = new BufferedReader(new FileReader(new File(
				csvFile)));

		// read the header
		String line = bufRdr.readLine();

		while ((line = bufRdr.readLine()) != null) {
			String[] arr = line.split(seperationChar);

			for (int i = 0; i < arr.length; i++) {
				if (arr[i].equals("")) {
					arr[i] = nullValue;
				}
			}

			if (checkForQuoteMarkRange) {
				arr = CheckForQuoteMarkRange(arr);
			}

			lines.add(arr);
		}

		String[][] ret = new String[lines.size()][];
		bufRdr.close();
		return lines.toArray(ret);
	}

	/**
	 * This method checks a line of date from the data to ensure that no piece
	 * of the data has been split up due to how the questionnaire data set
	 * handles decimal values and lists of data - eg. "181,5" and
	 * "Fifa 2014, Grand theft auto V". This is a problem since we use the comma
	 * to split read-in lines of data into attributes of data, and since because
	 * of this attribute data may erroneously be split up e.g. "181,5" is split
	 * up into two attributes - 181 and 5.
	 * 
	 * @param line
	 *            Line of data to check.
	 * @return Line of data corrected if needed.
	 */
	private static String[] CheckForQuoteMarkRange(String[] line) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> keeper = new ArrayList<String>();
		boolean addToKeeper = false;

		for (String part : line) {
			if (part.contains("\"")) {
				if (addToKeeper) {
					addToKeeper = false;
					keeper.add(part);
					String toAdd = keeper.toString();
					result.add(toAdd.substring(1, toAdd.length() - 1));// Done
																		// to
																		// remove
																		// brackets
																		// from
																		// the
																		// string.
					keeper.clear();
				} else {
					addToKeeper = true;
					keeper.add(part);
				}
			} else {
				if (addToKeeper) {
					keeper.add(part);
				} else {
					result.add(part);
				}
			}
		}
		String[] res = new String[result.size()];

		return result.toArray(res);
	}

	private static List<String> readAttributes() {
		List<String> lines = new ArrayList<String>();
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(new File(
					"Attributes.txt")));
			String line = null;
			while ((line = bufRdr.readLine()) != null) {
				if(line.lastIndexOf(",")!=-1){
					line = line.substring(0, line.length() - 1);
				}
				
				lines.add(line);
//				System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufRdr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}

	public static List<String> attrs;
	private static List<Response> responses;

	public static void main(String args[]) {
		attrs = readAttributes();
		responses = new ArrayList<>();
		try {
			String[][] data = readDataFile("DataMining2015Responses.csv", ",",
					"-", true);
			for (String[] line : data) {
//				 System.out.println("Length: " + line.length + " "
//				 + Arrays.toString(line));
				responses.add(new Response(line));
			}
			// System.out.println("Size: " + data.length);
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
		// Response r = new Response(
		// "Length: 49 [28/01/2015 12:25:38, 29, Male, 45, 196, 0, 15, Java; Python; JavaScript; C#; Ruby;, Android, Sounds interesting, Very interested, Very interested, Very interested, Sounds interesting, Meh, Meh, Meh, Sounds interesting, Very interested, Very interested, < 20 hours a week, \"Fifa 2014,  Grand theft auto V,  Last of us\", Walk, Atrium, Canteen, ScrollBar, Analog, Study room, Not at ITU, Not at ITU, Not at ITU, Not at ITU, Not at ITU, Not at ITU, Not at ITU, 3; 7; 2, I am taller than Héctor, 10, 10, 10, 10, 2, 1, 10, 10, 3, 10, SDT-DT, \"I am interested in the subject,  It may help me to find a job\"]");
		// HashMap<String, String> re = r.getR();
		// System.out.println(re.size());
		List<Integer> heights = new ArrayList<>();
		
		for (Response r : responses) {
			HashMap<String, String> result = r.getResult();
			int h = Integer.parseInt(result.get(height));
			if(h != -1){
				heights.add(h);
				result.put(height+normalized, normalize(result.get(height), heightMin, heightMax));
			}
			System.out.println(result.get(height+normalized)+
					"\n\t"+result.get(topicAlg)+
					"\n\t"+result.get(videoGames)+
					"\n\t"+result.get(tallerThanHector)+
					"\n\t"+result.get(whyCourse));
		}
		
		heights.sort(Comparator.naturalOrder());
//		System.out.println(heights);
		int mean = sum(heights)/heights.size();
		
		int middleIndex = heights.size()/2;
		int[] median = new int[2];
		median[0] = heights.get(middleIndex);
		if(heights.size()%2 == 0){
			median[1] = heights.get(middleIndex+1);
		}
		System.out.println("---------HEIGHT----------------HEIGHT------------HEIGHT-------------HEIGHT-----------------");
		System.out.println("MEAN:\t"+mean);
		System.out.println("MEDIAN:\t"+median[0]+" "+median[1]);
		
		final List<Integer> modes = getModes(heights);
	    System.out.println("MODES: "+modes);
	    System.out.println("MIN: "+heightMin);
	    System.out.println("MAX: "+heightMax);
	    System.out.println("-------------------------------------------------------------------------------------------");
	    System.out.println("number of resonses: "+responses.size());
	}

	private static List<Integer> getModes(List<Integer> heights) {
		final List<Integer> modes = new ArrayList<Integer>();
	    final Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();

	    int max = -1;

	    for (final int n : heights) {
	        int count = 0;

	        if (countMap.containsKey(n)) {
	            count = countMap.get(n) + 1;
	        } else {
	            count = 1;
	        }

	        countMap.put(n, count);

	        if (count > max) {
	            max = count;
	        }
	    }

	    for (final Map.Entry<Integer, Integer> tuple : countMap.entrySet()) {
	        if (tuple.getValue() == max) {
	            modes.add(tuple.getKey());
	        }
	    }
		return modes;
	}
	
	private static int sum(List<Integer> heights) {
		int sum = 0;
		for (Integer h : heights) {
			sum += h;
		}
		return sum;
	}

	public static int heightMax = 0;
	public static int heightMin = Integer.MAX_VALUE;
	
	private static String normalize(String s, int min, int max){
		double x = Integer.parseInt(s);
		double i = (x-min)/(max-min);
		return ""+i;
	}
}
