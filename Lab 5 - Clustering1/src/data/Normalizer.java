package data;

import java.util.ArrayList;

public class Normalizer {

	public static ArrayList<Iris> normalizeIrisList(ArrayList<Iris> data) {
		// TODO Auto-generated method stub
		float petal_lengthMAX = 0;
		float petal_lengthMIN = Float.MAX_VALUE;
		
		float petal_widthMAX = 0;
		float petal_widthMIN = Float.MAX_VALUE;
		float sepal_lengthMAX = 0;
		float sepal_lengthMIN = Float.MAX_VALUE;
		float sepal_widthMAX = 0;
		float sepal_widthMIN = Float.MAX_VALUE;
		for (int i = 0; i < data.size(); i++) {
			Iris iris = data.get(i);
			if(iris.Petal_Length > petal_lengthMAX){
				petal_lengthMAX = iris.Petal_Length;
			}
			if(iris.Petal_Length < petal_lengthMIN){
				petal_lengthMIN = iris.Petal_Length;
			}
			if(iris.Petal_Width > petal_widthMAX){
				petal_widthMAX = iris.Petal_Width;
			}
			if(iris.Petal_Width < petal_widthMIN){
				petal_widthMIN = iris.Petal_Width;
			}
			if(iris.Sepal_Length > sepal_lengthMAX){
				sepal_lengthMAX = iris.Sepal_Length;
			}
			if(iris.Sepal_Length < sepal_lengthMIN){
				sepal_lengthMIN = iris.Sepal_Length;
			}
			if(iris.Sepal_Width > sepal_widthMAX){
				sepal_widthMAX = iris.Sepal_Width;
			}
			if(iris.Sepal_Width < sepal_widthMIN){
				sepal_widthMIN = iris.Sepal_Width;
			}
		}
		for (Iris iris : data) {
			iris.Petal_Length = (iris.Petal_Length-petal_lengthMIN) / (petal_lengthMAX - petal_lengthMIN);
			iris.Petal_Width = (iris.Petal_Width - petal_widthMIN) / (petal_widthMAX - petal_widthMIN);
			iris.Sepal_Length = (iris.Sepal_Length - sepal_lengthMIN) / (sepal_lengthMAX - sepal_lengthMIN);
			iris.Sepal_Width = (iris.Sepal_Width - sepal_widthMIN) / (sepal_widthMAX - sepal_widthMIN);
		}
		
		return data;
	}
	
	

}
