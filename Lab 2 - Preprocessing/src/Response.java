import java.util.HashMap;

public class Response {

	private HashMap<String, String> result;

	public Response(String[] input) {
		// String input = "foo,bar,c;qual=\"baz,blurb\",d;junk=\"quux,syzygy\"";
		result = new HashMap<>();
		for (int i = 0; i < input.length; i++) {
			if(input[i]==null){
				input[i]="";
			}
			result.put(CSVFileReader.attrs.get(i), 
					input[i]);
		}
		// result = parseListWithoutQuoteCommas(input);
	}

//	private HashMap<String, String> parseListWithoutQuoteCommas(String input) {
//		HashMap<String, String> result = new HashMap<>();
//		int start = 0;
//		boolean inQuotes = false;
//		for (int current = 0; current < input.length(); current++) {
//			if (input.charAt(current) == '\"')
//				inQuotes = !inQuotes; // toggle state
//			boolean atLastChar = (current == input.length() - 1);
//			if (atLastChar)
//				result.put("name", input.substring(start));
//			else if (input.charAt(current) == ',' && !inQuotes) {
//				result.put("name", input.substring(start, current));
//				start = current + 1;
//			}
//		}
//		return result;
//	}

	public HashMap<String, String> getR() {
		return result;
	}

}
