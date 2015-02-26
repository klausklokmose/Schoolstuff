package kMean;
import java.util.ArrayList;

import data.Iris;

//ToDo: Compute cluster mean based on cluster members.
public class KMeanCluster {

	public ArrayList<Iris> ClusterMembers;
	public Iris mean;
	
	
	public KMeanCluster()
	{
		this.ClusterMembers = new ArrayList<Iris>();
	}
	
	public void addInitialMean(Iris iris){
//		ClusterMembers.add(iris);
		mean = iris;
	}
	
	public Iris calculateNewMean(){
		//TODO
		float sum_petal_length = 0;
		float sum_petal_width = 0;
		float sum_sepal_length = 0;
		float sum_sepal_width = 0;
		int size = ClusterMembers.size();
		if(size == 0){
			System.out.println("SIZE of members is zero");
			size = 1;
		}
		
		for (Iris iris : ClusterMembers) {
			sum_petal_length += iris.Petal_Length;
			sum_petal_width += iris.Petal_Width;
			sum_sepal_length += iris.Sepal_Length;
			sum_sepal_width += iris.Sepal_Width;
		}
		
		mean = new Iris(sum_petal_length/size, sum_petal_width/size, sum_sepal_length/size, sum_sepal_width/size, "");
//		System.out.println(mean);
		return mean;
	}
	
	@Override
	public String toString() {
		String toPrintString = "-----------------------------------CLUSTER START------------------------------------------" + System.getProperty("line.separator");
		
		for(Iris i : this.ClusterMembers)
		{
			toPrintString += i.toString() + System.getProperty("line.separator");
		}
		toPrintString += "-----------------------------------CLUSTER END-------------------------------------------" + System.getProperty("line.separator");
		
		return toPrintString;
	}

}
