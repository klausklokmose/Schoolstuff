import java.util.ArrayList;

import kMean.KMeanCluster;
import kMean.KMeans;
import data.DataLoader;
import data.Iris;
import data.IrisClass;
import data.Normalizer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//First step load in iris data
		ArrayList<Iris> irisData = Normalizer.normalizeIrisList(DataLoader.LoadAllIrisData());
		
		//Second step --> do the clustering using k-means!
		ArrayList<KMeanCluster> FoundClusters_KMeans = KMeans.KMeansPartition(3, irisData);
		System.out.println("..................................");
		int h = 1;
		for (KMeanCluster kMeanCluster : FoundClusters_KMeans) {
			System.out.println("Cluster "+h++);
			System.out.printf("setosa       %s", kMeanCluster.ClusterMembers.stream().filter(i -> i.Class == IrisClass.Iris_setosa).count()+"\n");
			System.out.printf("versicolor   %s", kMeanCluster.ClusterMembers.stream().filter(i -> i.Class == IrisClass.Iris_versicolor).count()+"\n");
			System.out.printf("virginica    %s", kMeanCluster.ClusterMembers.stream().filter(i -> i.Class == IrisClass.Iris_virginica).count()+"\n\n");
		}
		
		//Third step --> do the clustering using k-medoids!
//		ArrayList<KMedoidCluster> FoundClusters_KMedoids = KMedoid.KMedoidPartition(3, irisData);
	}

}
