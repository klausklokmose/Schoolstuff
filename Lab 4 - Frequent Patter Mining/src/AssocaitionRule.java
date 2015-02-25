import java.util.Arrays;


public class AssocaitionRule {
	private int[] a;
	private int[] b;
	private int[] AunionB;
	private double confidence;
	private double support;
	
	public AssocaitionRule(int[] a, int[] b, int[] AunionB, double support, double confidence){
		this.a = a;
		this.b = b;
		this.support = support;
		this.confidence = confidence;
	}

	public int[] getA() {
		return a;
	}

	public int[] getAunionB(){
		return AunionB;
	}
	public double getSupport(){
		return support;
	}
	
	public double getConfidence() {
		return confidence;
	}
	
	public String toString(){
		return Arrays.toString(a)+" => "+Arrays.toString(b) + "\tSupport = "+(int)support +" %\tConfidence = "+(int)confidence+" %";
	}
}
