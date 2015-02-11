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
		System.out.println("DataManager loaded "+mushrooms.size() + " mushrooms");
		int trainingSetSize = 1000;
		int testSetSize = mushrooms.size()-trainingSetSize;
		int k = 200;
		
		List<Mushroom> trainingSet = new ArrayList<Mushroom>();
		for (int i = 0; i < mushrooms.size(); i++) {
			trainingSet.add(mushrooms.get(i));
		}
		MushroomClassification di = null;
		List<MushroomClassification> classifications = new ArrayList<>();
		for (int i = testSetSize; i < mushrooms.size(); i++) {
			List<MushroomClassification> distances = new ArrayList<MushroomClassification>();
			getDistancesFromI(mushrooms, trainingSetSize, trainingSet, i,
					distances);
			List<MushroomClassification> kNearest = getKNearest(k, distances);
			int numPoisonous = getNumPoisonous(kNearest);
			int numEdible = Math.abs(kNearest.size()-numPoisonous);
			Class_Label label = (numPoisonous > numEdible) ? Class_Label.poisonous : Class_Label.edible;
			di = new MushroomClassification(mushrooms.get(i), label);
			classifications.add(di);
			System.out.println("-->"+label.equals(mushrooms.get(i).getAttributeValue(Class_Label.class)));
		}
	}

	private static int getNumPoisonous(List<MushroomClassification> kNearest) {
		int num = 0;
		for (MushroomClassification mushroomDist : kNearest) {
			if(mushroomDist.getM().getAttributeValue(Class_Label.class).equals(Class_Label.poisonous)){
				num++;
			}
		}
		return num;
	}

	private static List<MushroomClassification> getKNearest(int k, List<MushroomClassification> distances) {
		Collections.sort(distances, new DistComparator());

		List<MushroomClassification> kNearest = new ArrayList<>();
		for (int j = 0; j < k; j++) {
			kNearest.add(distances.get(j));
//			System.out.print(distances.get(j).getM().getAttributeValue(Class_Label.class)+", ");
		}
		return kNearest;
	}

	private static void getDistancesFromI(ArrayList<Mushroom> mushrooms,
			int trainingSetSize, List<Mushroom> trainingSet, int i,
			List<MushroomClassification> distances) {
		MushroomClassification di;
		for (int j = 0; j < trainingSetSize; j++) {
//				add to priority-queue dist(mushrooms.get(i), trainingSet.get(j));
			di = new MushroomClassification(mushrooms.get(i), dist(mushrooms.get(i), trainingSet.get(j)));
			distances.add(di);
		}
		
	}

	private static double dist(Mushroom x1, Mushroom x2){
		int sum = 0;
		ArrayList<Object> attrList = Mushroom.getAttributeList();
		
		for (int i = 0; i < attrList.size(); i++) {
			sum += x1.getAttributeValue(attrList.get(i)).equals(x2.getAttributeValue(attrList.get(i))) ? 0 : 1;
		}
		return Math.sqrt(sum);
	}
	public static class DistComparator implements Comparator<MushroomClassification>{
		@Override
		public int compare(MushroomClassification thizz, MushroomClassification that) {
			if(that.getD() > thizz.getD()){
				return -1;
			}else if(that.getD() < thizz.getD()){
				return 1;
			}else{
				return 0;
			}
		}
		
	}

}
