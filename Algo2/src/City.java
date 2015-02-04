
import java.util.ArrayList;


public class City {

	/*
	 * HashMap is used to store the distance between the city and the other cities
	 * The key corresponds to the other city name and the value is the distance
	 * 
	 */
	
	private String name;
	private ArrayList<Edge> destinations;
	
	public City(String name){
		this.name = name;	
		destinations = new ArrayList<Edge>();
	}
	
	public ArrayList<Edge> getDestinations(){
		return destinations;
	}
	
	public void addEdge(Edge edge){
		destinations.add(edge);
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
	
	
	
}
