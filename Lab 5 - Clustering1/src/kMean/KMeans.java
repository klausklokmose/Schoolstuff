package kMean;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import data.Iris;


public class KMeans {

	@SuppressWarnings("unchecked")
	public static ArrayList<KMeanCluster> KMeansPartition(int k, ArrayList<Iris> data)
	{
		Set<Integer> rndIndex = getRandomIndex(k, data);
		
		System.out.println("k="+k+" Random indexes: "+rndIndex);
		ArrayList<Iris> randomSamples = new ArrayList<>();
		for (int integer : rndIndex) {
			randomSamples.add(data.get(integer));
		}
		ArrayList<KMeanCluster> clusters = new ArrayList<KMeanCluster>();
		for (int i = 0; i < k; i++) {
			KMeanCluster c = new KMeanCluster();
			c.addInitialMean(randomSamples.get(i));
			clusters.add(c);
		}
		ArrayList<KMeanCluster> tmpClusters = (ArrayList<KMeanCluster>) clusters.clone();
		
		int barrier;
		int count = 0;
		do {
			count++;
//			System.out.print(". ");
			clusters = (ArrayList<KMeanCluster>) tmpClusters.clone();
//			System.out.println("iteration");
			for (int i = 0; i < k; i++) {
				tmpClusters.get(i).ClusterMembers.clear();
				if(tmpClusters.get(i).ClusterMembers.size()!=0){
					System.out.println("ERROR");
				}
			}
			for (int i = 0; i < data.size(); i++) {
				float d = Float.MAX_VALUE;
				Iris irisI = data.get(i);
				int clusterToPut = 0;
				for (int j = 0; j < k; j++) {
					Iris irisJ = clusters.get(j).mean;

					float dist = dist(irisI, irisJ);
					if(dist <= d){
						d = dist;
						clusterToPut = j;
					}
				}
//				System.out.println(clusterToPut);
				tmpClusters.get(clusterToPut).
									ClusterMembers.add(irisI);
			}
			//update cluster means
			barrier = 0; //if run is k, then stop
			for (int i = 0; i < k; i++) {
				Iris oldMean = clusters.get(i).mean;
				Iris newMean = tmpClusters.get(i).calculateNewMean();

				if(oldMean.equals(newMean)){
					barrier++;
				}
			}
			
		}while(barrier != k);

		System.out.println("rounds = "+count);
		
		return clusters;
		
	}
	
	private static float dist(Iris i, Iris j){
		return (float)Math.sqrt(
					  sq(i.Petal_Length-j.Petal_Length) 
					+ sq(i.Petal_Width-j.Petal_Width)
					+ sq(i.Sepal_Length-j.Sepal_Length) 
					+ sq(i.Sepal_Width-j.Sepal_Width));
	}
	
	private static double sq(double i){
		return i*i;
	}
	private static Set<Integer> getRandomIndex(int k, ArrayList<Iris> data) {
		Set<Integer> generated = new LinkedHashSet<Integer>();
		Random rng = new Random(); // Ideally just create one instance globally
		// Note: use LinkedHashSet to maintain insertion order
		while (generated.size() < k)
		{
		    Integer next = rng.nextInt(data.size()-1);
		    // As we're adding to a set, this will automatically do a containment check
		    generated.add(next);
		}
		return generated;
	}

}
