
public class AssocaitionRule {
	private ItemSet set;
	private double condidence;
	private double support;
	
	public AssocaitionRule(ItemSet set, double support, double condidence){
		this.set = set;
		this.support = support;
		this.condidence = condidence;
	}

	public ItemSet getSet() {
		return set;
	}

	public double getSupport(){
		return support;
	}
	
	public double getCondidence() {
		return condidence;
	}
}
