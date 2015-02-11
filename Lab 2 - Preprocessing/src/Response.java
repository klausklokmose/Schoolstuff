import java.util.HashMap;

public class Response {

	private final HashMap<String, String> result;
	
	
	public Response(String[] input) {
		result = new HashMap<>();
		
		for (int i = 0; i < input.length; i++) {
			String attr = CSVFileReader.attrs.get(i);
			String answer = input[i];
			if (attr.equals(CSVFileReader.height)) {
				answer = cleanHeight(answer);
				int value = Integer.parseInt(answer);
				if(value > 0){
					if(value > CSVFileReader.heightMax){
						CSVFileReader.heightMax = value;
					}
					if(value < CSVFileReader.heightMin){
						CSVFileReader.heightMin = value;
					}
				}
				
			} else if(attr.equals(CSVFileReader.topicAlg)){
				answer = getIndexString(answer, CSVFileReader.interestedList);
				
			}else if (attr.equals(CSVFileReader.tallerThanHector)) {
				answer = getIndexString(answer, CSVFileReader.tallerThanHectorList);
				
			} else if (attr.equals(CSVFileReader.whyCourse)) {
				answer = cleanWhyTakingCourse(answer);
				
			} else if(attr.equals(CSVFileReader.videoGames)){
				answer = getIndexString(answer, CSVFileReader.videoGamesUsageList);
			}
			// System.out.println(attr+" "+input[i]);
			result.put(attr, answer);
		}
		// result = parseListWithoutQuoteCommas(input);
	}

	private String getIndexString(String answer, String[] ps) {
		boolean found = false;
		for (int j = 0; j < ps.length; j++) {
			if(answer.equals(ps[j])){
				 answer = ""+j;
				 found = true;
				 break;
			}
		}
		if(found)
			return answer;
		else
			return "-1";
	}

//	private String cleanTallerThanHector(String answer) {
//		return answer.replaceAll("Héctor", "Hector");
//	}

	private String cleanWhyTakingCourse(String answer) {
		
		String[] checks = answer.replaceAll("\"", "").split(",");
		answer = "";
		if (checks.length > 0) {
			for (String c : checks) {
				c = c.trim();
				if(!c.isEmpty()){
					if (c.trim().equals(CSVFileReader.ps[0])) {
						answer += "0,";
					} else if (c.trim().equals(CSVFileReader.ps[1])) {
						answer += "1,";
					} else if (c.trim().equals(CSVFileReader.ps[2])) {
						answer += "2,";
					}
				}
			}
			answer = answer.length() > 0 ? answer.substring(0, answer.length() - 1): "-1";
		}else {
			answer = "-1";
		}
		return answer;
	}

	private String cleanHeight(String answer) {
		while (true) {
			try {
				if (answer.equals("0")) {
					answer = "-1";
				}
				Integer.parseInt(answer);// test if is integer
				break;
			} catch (NumberFormatException e) {
				answer = answer.replaceAll("\"", "").substring(0,
						answer.indexOf(","));
			}
		}
		return answer;
	}

	// private HashMap<String, String> parseListWithoutQuoteCommas(String input)
	// {
	// HashMap<String, String> result = new HashMap<>();
	// int start = 0;
	// boolean inQuotes = false;
	// for (int current = 0; current < input.length(); current++) {
	// if (input.charAt(current) == '\"')
	// inQuotes = !inQuotes; // toggle state
	// boolean atLastChar = (current == input.length() - 1);
	// if (atLastChar)
	// result.put("name", input.substring(start));
	// else if (input.charAt(current) == ',' && !inQuotes) {
	// result.put("name", input.substring(start, current));
	// start = current + 1;
	// }
	// }
	// return result;
	// }

	public HashMap<String, String> getResult() {
		return result;
	}

}
