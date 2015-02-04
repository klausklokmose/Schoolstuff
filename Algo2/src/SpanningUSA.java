
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

public class SpanningUSA {

	private static LinkedHashMap<String, City> cities;
	private static String FirstItem;
	private static ArrayList<String> visitedCities;

	public static void main(String[] args) {
		readFile(args[0]);

		int result = primsAlgorithm();
		System.out.println("Is " + result + " the right answer? "
				+ (result == 16598));

	}

	private static int primsAlgorithm() {

		int total = 0;
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
		visitedCities = new ArrayList<String>();

		City first = cities.get(FirstItem);
		visitedCities.add(first.getName());

		for (Edge destination : first.getDestinations()) {
			pq.add(destination);
		}

		while (!pq.isEmpty()) {
			Edge currentEdge = pq.poll();

			// City1 (A) <-----edge-----> City2 (B)
			// If B was not visited before, connect it and add it's possible
			// connections to the queue
			if (hasNotVisitedToCity(currentEdge)) {

				visitedCities.add(currentEdge.getToCityName());
				total += currentEdge.getDistance();
				for (Edge destinationFromSecondCity : currentEdge.getToCity()
						.getDestinations()) {

					// Thank you! Please come again! Don't add the same.
					if (destinationFromSecondCity != currentEdge) {
						pq.add(destinationFromSecondCity);
					}
				}

				// If B have been visited before, check if A was not visited
			} 
			else if (hasNotVisitedFromCity(currentEdge)) {

				visitedCities.add(currentEdge.getFromCityName());
				total += currentEdge.getDistance();
				for (Edge destinationFromFirstCity : currentEdge.getFromCity()
						.getDestinations()) {
					// Don't add the same.
					if (destinationFromFirstCity != currentEdge)
						pq.add(destinationFromFirstCity);
				}
			}
		}

		return total;
	}

	private static boolean hasNotVisitedFromCity(Edge currentEdge) {
		return !visitedCities.contains(currentEdge.getFromCityName());
	}

	private static boolean hasNotVisitedToCity(Edge current) {
		return !visitedCities.contains(current.getToCityName());
	}

	private static void readFile(String fileName) {
		Scanner scanner = null;
		try {
			File file = new File(fileName);
			scanner = new Scanner(file);
			cities = new LinkedHashMap<String, City>();
		} catch (Exception e) {
			System.out.println("Please add the filename to the arguments.");
			System.exit(0);
		}

		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (!line.contains("--")) {

				line = simplifyString(line);
				City current = new City(line);
				if (FirstItem == null) {
					FirstItem = line;
				}
				cities.put(line, current);

			} else {
				String[] distanceSplit = line.split("--");
				String[] toCityAndDistance = distanceSplit[1].trim().split(
						"\\[");

				String fromCity = simplifyString(distanceSplit[0]);
				String toCity = simplifyString(toCityAndDistance[0]);

				int distance = Integer.parseInt(toCityAndDistance[1]
						.replaceAll("\\]", "").trim());

				Edge edge = new Edge(cities.get(fromCity), cities.get(toCity),
						distance);
				cities.get(fromCity).addEdge(edge);
				cities.get(toCity).addEdge(edge);

			}
		}
	}

	private static String simplifyString(String str) {
		return str.replaceAll("\"", "").trim();
	}

}
