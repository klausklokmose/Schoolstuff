import data.Class_Label;


public class MushroomClassification implements Comparable<Double>{

	public Mushroom m;
	public double d;
	private Class_Label label;
	
	public MushroomClassification(Mushroom mushroom, double dist) {
		this.m = mushroom;
		this.d = dist;
	}
	
	public MushroomClassification(Mushroom mushroom, Class_Label label) {
		this.m = mushroom;
		this.label = label;
	}

	@Override
	public int compareTo(Double that) {
		if(that > this.d){
			return -1;
		}else if(that < this.d){
			return 1;
		}else{
			return 0;
		}
	}

	public Mushroom getM() {
		return m;
	}

	public double getD() {
		return d;
	}

	public void setClassification(Class_Label label) {
		this.label = label;
	}
	public Class_Label getClassification(){
		return label;
	}
}
