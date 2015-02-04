import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClosestPairsInThePlane {

//	private static ArrayList<String> results = new ArrayList<String>();

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		// find all the test files in the data folder
		Path dir = FileSystems.getDefault().getPath("data");
		List<String> fileNames = new ArrayList<String>();
		fileNames = getFileNames(fileNames, dir);

		// String fileName = "data/ulysses16.tsp";
//		String fileName = "data/pla85900.tsp";
//		 String closestPair = closestPair(fileName);
//		 System.out.println(closestPair);
		for (int i = 0; i < fileNames.size(); i++) {
			System.out.println(closestPair(fileNames.get(i))+"\n");
		}
		long end = System.currentTimeMillis();
		System.out.println(((double)(end - start))/1000/60 + " min");
	}

	private static String closestPair(String fileName) {
		System.out.println("reading file... " + fileName);
		ArrayList<MyPoint> points = readFile(fileName);

		// sort by X
		Mergesort.sort(points, true);

		int splitX = points.size() / 2;
		List<MyPoint> leftHalf = points.subList(0, splitX);
		List<MyPoint> rightHalf = points.subList(splitX + 1, points.size());

		// delta from left half
		Delta deltaLeft = getDelta(leftHalf); // split left
		// delta from right half
		Delta deltaRight = getDelta(rightHalf); // split right
		// find minimum delta --> min(left, right)
		Delta delta = min(deltaLeft, deltaRight);

		// we don't have a line... by x coordinate
		double leftX = leftHalf.get(leftHalf.size() - 1).getX();
		double rightX = rightHalf.get(0).getX();
		double lineX = (leftX + (rightX - leftX) / 2);
		double lowerBound = lineX - delta.getDelta();
		double upperBound = lineX + delta.getDelta();

		// remove all elements further than delta from lineX
		ArrayList<MyPoint> pointsByLine = new ArrayList<MyPoint>();
		for (int i = 0; i < points.size(); i++) {
			MyPoint p = points.get(i);
			if (pointIsWithinBounds(p, lowerBound, upperBound)) {
				pointsByLine.add(p);
			}
		}
		//if there are points to be processed
		if (pointsByLine.size() > 1) {
			// sort list by y-coordinate
			Mergesort.sort(pointsByLine, false);

			double twoDeltaHalf = delta.getDelta();

			final int TO = pointsByLine.size() - 1;// -1 because we don't
													// consider the last element
													// with any other element
			for (int i = 0; i < TO; i++) {
				MyPoint point1 = pointsByLine.get(i);
				for (int j = i + 1; j < TO; j++) {
					MyPoint point2 = pointsByLine.get(j);
					if (Math.abs(point2.getY() - point1.getY()) < twoDeltaHalf) {
						double distance = distanceBetween(point1, point2);
						if (distance < twoDeltaHalf) {
							replaceDelta(delta, point1, point2, distance);
						}
					} else {
						break;
					}
				}
			}
		}
		return ".../data/" + fileName + ": " + points.size() + " "
				+ delta.getDelta();
	}

	private static boolean pointIsWithinBounds(MyPoint p, double lowerBound,
			double upperBound) {
		return p.getX() > lowerBound && p.getX() < upperBound;
	}

	private static void replaceDelta(Delta origin, MyPoint point1,
			MyPoint point2, double distance) {
		origin.setDelta(distance);
		origin.setPair(point1, point2);
	}

	private static Delta min(Delta deltaLeft, Delta deltaRight) {
		if (deltaLeft.isLowerThan(deltaRight)) {
			return deltaLeft;
		} else {
			return deltaRight;
		}
	}

	private static Delta getDelta(List<MyPoint> list) {
		Delta delta = new Delta();
		for (int i = 0; i < list.size(); i++) {
			MyPoint point1 = list.get(i);
			for (int j = 0; j < list.size(); j++) {
				MyPoint point2 = list.get(j);
				if (!point1.equals(point2)) {
					double distance = distanceBetween(point1, point2);
					// System.out.println(distance);
					if (distance < delta.getDelta() || delta.getDelta() == -1) {
						replaceDelta(delta, point1, point2, distance);
					}
				}
			}
		}
		return delta;
	}

	private static double distanceBetween(MyPoint point1, MyPoint point2) {
		double x1 = point1.getX();
		double y1 = point1.getY();
		double x2 = point2.getX();
		double y2 = point2.getY();
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	@SuppressWarnings("resource")
	private static ArrayList<MyPoint> readFile(String fileName) {
		File file = new File(fileName);
		ArrayList<MyPoint> points = new ArrayList<MyPoint>();
		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
		} catch (Exception e) {
			System.out.println("Please add the filename to the arguments.");
			System.exit(0);
		}
		try {
			while (true) {
				String l = scanner.nextLine();
				if (l.trim().equals("NODE_COORD_SECTION")) {
					break;
				}
			}
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (!line.equals("EOF")) {

					String[] splitLine = line.split("\\s+");
					MyPoint point = null;
					point = new MyPoint(splitLine[0], splitLine[1],
							splitLine[2]);
					points.add(point);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("file error: " + fileName);
			e.printStackTrace();
		}
		return points;
	}

	/*
	 * Method found at http://stackoverflow.com/a/24324367
	 */
	private static List<String> getFileNames(List<String> fileNames, Path dir) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
			for (Path path : stream) {
				if (path.toFile().isDirectory())
					getFileNames(fileNames, path);
				else {
					// String fileName = "../"
					// + path.toString().replace("\\", "/");
					String fileName = path.toAbsolutePath().toString();
					fileNames.add(fileName);
					System.out.println(fileName);
				}
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNames;
	}
}

class Delta {
	private double delta = -1;
	private MyPoint[] pair = new MyPoint[2];

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	public void setPair(MyPoint point1, MyPoint point2) {
		pair[0] = point1;
		pair[1] = point2;
	}

	public MyPoint[] getPair() {
		return pair;
	}

	public boolean isLowerThan(Delta d) {
		// if no one of the two has not found a delta because of empty sets
		if (delta == -1 || d.getDelta() == -1) {
			return true;
		}
		return delta < d.getDelta();
	}

}

class MyPoint {
	private final double x, y;
	private final String name;

	public MyPoint(String name, String x, String y) {
		this.name = name;
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getName() {
		return name;
	}

	public void print() {
		System.out.println(name + " " + x + ", " + y);
	}

	// TODO maybe not needed!
	@Override
	public boolean equals(Object obj) {
		double oX = (double) ((MyPoint) obj).getX();
		double oY = (double) ((MyPoint) obj).getY();
		String oName = ((MyPoint) obj).getName();
		return this.x == oX && this.y == oY && this.name == oName;
	}
}

/*
 * implemented from
 * http://www.vogella.com/tutorials/JavaAlgorithmsMergesort/article.html
 */
class Mergesort {
	private static boolean sortByX = true;
	private static MyPoint[] numbers;
	private static MyPoint[] helper;

	public static void sort(ArrayList<MyPoint> values, boolean shouldSortByX) {
		sortByX = shouldSortByX;
		int capacity = values.size();
		numbers = new MyPoint[capacity];
		for (int i = 0; i < values.size(); i++) {
			numbers[i] = values.get(i);
		}
		helper = new MyPoint[capacity];
		mergesort(0, capacity - 1);

		for (int i = 0; i < numbers.length; i++) {
			values.set(i, numbers[i]);
		}
	}

	private static void mergesort(int low, int high) {
		// check if low is smaller then high, if not then the array is sorted
		if (low < high) {
			// Get the index of the element which is in the middle
			int middle = low + (high - low) / 2;
			// Sort the left side of the array
			mergesort(low, middle);
			// Sort the right side of the array
			mergesort(middle + 1, high);
			// Combine them both
			merge(low, middle, high);
		}
	}

	private static void merge(int low, int middle, int high) {
		// Copy both parts into the helper array
		for (int i = low; i <= high; i++) {
			helper[i] = numbers[i];
		}
		int i = low;
		int j = middle + 1;
		int k = low;
		// Copy the smallest values from either the left or the right side back
		// to the original array
		while (i <= middle && j <= high) {
			double first = helper[i].getX();
			double second = helper[j].getX();
			if (!sortByX) {
				first = helper[i].getY();
				second = helper[j].getY();
			}
			if (first <= second) {
				// if (helper[i].getX() <= helper[j].getX()) {
				numbers[k] = helper[i];
				i++;
			} else {
				numbers[k] = helper[j];
				j++;
			}
			k++;
		}
		// Copy the rest of the left side of the array into the target array
		while (i <= middle) {
			numbers[k] = helper[i];
			k++;
			i++;
		}

	}
}