package data;


public class Iris {

	public float Sepal_Length;
	public float Sepal_Width;
	public float Petal_Length;
	public float Petal_Width;
	
	public IrisClass Class;
	
	public Iris(float sepal_length, float sepal_width, float petal_length, float petal_width, String iris_class)
	{
		this(sepal_length,sepal_width,petal_length,petal_width,ResolveIrisClass(iris_class));
	}
	
	public Iris(float sepal_length, float sepal_width, float petal_length, float petal_width, IrisClass iris_class)
	{
		this.Sepal_Length = sepal_length;
		this.Sepal_Width = sepal_width;
		this.Petal_Length = petal_length;
		this.Petal_Width = petal_width;
		this.Class = iris_class;
	}
	
	private static IrisClass ResolveIrisClass(String iris_class)
	{
		if(iris_class.equals("Iris-setosa"))
		{
			return IrisClass.Iris_setosa;
		}
		else if(iris_class.equals("Iris-versicolor"))
		{
			return IrisClass.Iris_versicolor;
		}
		else if(iris_class.equals("Iris-virginica"))
		{
			return IrisClass.Iris_virginica;
		}
		
		return null;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Class == null) ? 0 : Class.hashCode());
		result = prime * result + Float.floatToIntBits(Petal_Length);
		result = prime * result + Float.floatToIntBits(Petal_Width);
		result = prime * result + Float.floatToIntBits(Sepal_Length);
		result = prime * result + Float.floatToIntBits(Sepal_Width);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		double epsilon = 0.0000001f;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Iris other = (Iris) obj;
		if (Class != other.Class)
			return false;
		if (Math.abs(this.Petal_Length - other.Petal_Length) > epsilon)
			return false;
		if (Math.abs(this.Petal_Width - other.Petal_Width) > epsilon)
			return false;
		if (Math.abs(this.Sepal_Length - other.Sepal_Length) > epsilon)
			return false;
		if (Math.abs(this.Sepal_Width - other.Sepal_Width ) > epsilon)
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "Iris Object --> | Sepal_Length = "+this.Sepal_Length+" | Sepal_Width = "+this.Sepal_Width+" | Petal_Length = "+this.Petal_Length + " | Petal_Width = "+this.Petal_Width + " | Class = "+this.Class;
		
		return result;
	}
	

}
