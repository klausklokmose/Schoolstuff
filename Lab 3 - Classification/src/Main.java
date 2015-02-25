import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import data.Class_Label;

/**
 * Main class to run program from.
 * 
 * @author Anders Hartzen and Hajira Jabeen
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// First step - Load data and convert to Mushroom objects.
		ArrayList<Mushroom> mushrooms = DataManager.LoadData();
		System.out.println("DataManager loaded " + mushrooms.size()
				+ " mushrooms");
		int trainingSetSize = 2000;
		int testSetSize = mushrooms.size() - trainingSetSize;
		int TO = 300;
for (int k = 1; k < TO; k++) {
	
		List<Mushroom> trainingSet = new ArrayList<Mushroom>();
		for (int i = 0; i < trainingSetSize; i++) {
			trainingSet.add(mushrooms.get(i));
		}
		List<MushroomClassification> classifications = new ArrayList<>();
		int numGoodClassifications = 0;

		for (int i = trainingSetSize; i < mushrooms.size(); i++) {
			Mushroom mushr = mushrooms.get(i);
			
			Class_Label label = classifyLabel(k, mushr, trainingSet);
			MushroomClassification di = new MushroomClassification(
					mushrooms.get(i), label);
			classifications.add(di);
			numGoodClassifications += label.equals(mushr
					.getAttributeValue(Class_Label.class)) ? 1 : 0;
		}

		System.out.println(""
				+ (((double) numGoodClassifications / testSetSize) * 100));
}
		
	}

	private static Class_Label classifyLabel(int k, Mushroom mushr,
			List<Mushroom> trainingSet) {
		List<MushroomClassification> distances = getDistancesFromI(
				trainingSet, mushr);
		List<MushroomClassification> kNearest = getKNearest(k, distances);
		int numPoisonous = getNumPoisonous(kNearest);
		int numEdible = Math.abs(kNearest.size() - numPoisonous);
		Class_Label label = (numPoisonous > numEdible) ? Class_Label.poisonous
				: Class_Label.edible;
		return label;
	}

	private static int getNumPoisonous(List<MushroomClassification> kNearest) {
		int num = 0;
		for (MushroomClassification mushroomDist : kNearest) {
			if (mushroomDist.getM().getAttributeValue(Class_Label.class)
					.equals(Class_Label.poisonous)) {
				num++;
			}
		}
		return num;
	}

	private static List<MushroomClassification> getKNearest(int k,
			List<MushroomClassification> distances) {
		Collections.sort(distances, new DistComparator());
		if (k > distances.size()) {
			k = distances.size();
		}
		List<MushroomClassification> kNearest = new ArrayList<>();
		for (int j = 0; j < k; j++) {
			kNearest.add(distances.get(j));
			// System.out.print(distances.get(j).getM().getAttributeValue(Class_Label.class)+", ");
		}
		return kNearest;
	}

	private static List<MushroomClassification> getDistancesFromI(
			List<Mushroom> trainingSet, Mushroom mushroom) {
		List<MushroomClassification> distances = new ArrayList<>();
		for (int i = 0; i < trainingSet.size(); i++) {
			distances.add(new MushroomClassification(trainingSet.get(i),
					hammerDist(mushroom, trainingSet.get(i))));
		}
		return distances;
	}

	private static int hammerDist(Mushroom x1, Mushroom x2) {
		int sum = 0;
		ArrayList<Object> attrList = Mushroom.getAttributeList();

		for (int i = 0; i < attrList.size(); i++) {
			sum += x1.getAttributeValue(attrList.get(i)).equals(
					x2.getAttributeValue(attrList.get(i))) ? 0 : 1;
		}
		return sum;
	}

	public static class DistComparator implements
			Comparator<MushroomClassification> {
		@Override
		public int compare(MushroomClassification thizz,
				MushroomClassification that) {
			if (that.getD() > thizz.getD()) {
				return -1;
			} else if (that.getD() < thizz.getD()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
