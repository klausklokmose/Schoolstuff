package com.example.smartcoffee.test;

import java.util.ArrayList;

import android.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartcoffee.MainActivity;
import com.example.smartcoffee.R;
import com.example.smartcoffee.Recipe;
import com.example.smartcoffee.RecipeActivity;
import com.example.smartcoffee.User;

public class SmartCoffeeTest extends
		ActivityInstrumentationTestCase2<RecipeActivity> {

	private MainActivity mActivity;
	private String name = "";
//	private int sugar;
	private RecipeActivity recipActiv;
	private MenuItem button;
	private ActionBar actionBar;
	private CheckBox foam;
	private CheckBox decaf;
	private CheckBox strMed;
	private CheckBox strMild;
	private CheckBox strStrong;
	private CheckBox grBlonde;
	private CheckBox grDark;
	private TextView sugarText;
	private CheckBox grMed;
	private TextView sweetText;
	private TextView creamText;
	private SeekBar sugar;
	private SeekBar sweet;
	private SeekBar cream;

//	public SmartCoffeeTest() {
//		super("com.example.smartcoffee", MainActivity.class);
//	}
	public SmartCoffeeTest() {
		super("com.example.smartcoffee", RecipeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		mActivity = this.getActivity();
		recipActiv = this.getActivity();
		
		actionBar = recipActiv.getActionBar();
		foam = (CheckBox) recipActiv.findViewById(R.id.foam);
		decaf = (CheckBox) recipActiv.findViewById(R.id.decaf);
		strMild = (CheckBox) recipActiv.findViewById(R.id.strMild);
		strMed = (CheckBox) recipActiv.findViewById(R.id.strMed);
		strStrong = (CheckBox) recipActiv.findViewById(R.id.strStrong);
		grBlonde = (CheckBox) recipActiv.findViewById(R.id.grBlon);
		grMed = (CheckBox) recipActiv.findViewById(R.id.grMed);
		grDark = (CheckBox) recipActiv.findViewById(R.id.grDar);
		sugar = (SeekBar) recipActiv.findViewById(R.id.sugarBar);
		sugarText = (TextView) recipActiv.findViewById(R.id.sugar);
		sweet = (SeekBar)recipActiv.findViewById(R.id.sweetBar);
		sweetText = (TextView) recipActiv.findViewById(R.id.sweet);
		cream = (SeekBar)recipActiv.findViewById(R.id.creamBar);
		creamText = (TextView) recipActiv.findViewById(R.id.cream);
	}

	public void testRecipeActivityName(){
		//name
		assertEquals("New Recipe", actionBar.getTitle().toString());
		assertEquals(false, strStrong.isChecked());
	}
	public void testRecipeActivityStrength(){
		//strength should be checked in strMed
		assertEquals(false, strMild.isChecked());
		assertEquals(true, strMed.isChecked());
	}
	
	public void testRecipeActivityGrind(){
		//grind - should be checked in grMed
		assertEquals(false, grBlonde.isChecked());
		assertEquals(true, grMed.isChecked());
		assertEquals(false, grDark.isChecked());
	}
	
	public void testRecipeActivityDecaf(){
		//decaf should be set false
		assertEquals(false, decaf.isChecked());
	}
	
	public void testRecipeActivityFoam(){
		//foam - should be set false
		assertEquals(false, foam.isChecked());
	}
	
	public void testRecipeActivitySugar(){
		//sugar - should be set to 0
		assertEquals(0, sugar.getProgress());
		assertEquals("Sugar (0)", sugarText.getText());
	}
	
	public void testRecipeActivitySweet(){
		//sweet - should be set to 3
		assertEquals(3, sweet.getProgress());
		assertEquals("Sweetener (3)", sweetText.getText());
	}
	
	public void testRecipeActivityCream(){
		//cream - should be 5
		assertEquals(5, cream.getProgress());
		assertEquals("Cream (5)", creamText.getText());
	}
	
//	public void testSetSugarTooLow() {
//		setSugar(-34);
//		assertEquals(0, sugar);
//	}
//
//	public void testSetSugarTooHigh() {
//		setSugar(44);
//		assertEquals(10, sugar);
//	}
//
//	public void testSetSugarMax() {
//		setSugar(10);
//		assertEquals(10, sugar);
//	}
//
//	public void testSetSugarMin() {
//		setSugar(0);
//		assertEquals(0, sugar);
//	}
//
//	public void testSetSugarMid() {
//		setSugar(5);
//		assertEquals(5, sugar);
//	}
//
//	public void testSetNameOverMaxChar() {
//		setNames("qwertyuiopåasdfghjklæøzxcvbnm");
//
//		assertEquals("qwertyuiopåasdfghjkl", name);
//	}
//
//	public void testSetNameMaxChar() {
//		setNames("qwertyuiopåasdfghjkl");
//
//		assertEquals("qwertyuiopåasdfghjkl", name);
//	}
//
//	public void testSetNameNothing() {
//		setNames("");
//		assertEquals("Recipe Name", name);
//	}
//
//	public void setNames(String tempName) {
//		if (tempName.length() > 20) {
//			tempName = tempName.substring(0, 20);
//		} else if (tempName.length() == 0) {
//			if (name.length() == 0) {
//				tempName = "Recipe Name";
//			} else {
//				return;
//			}
//		}
//		this.name = tempName.trim();
//	}

	// public void testSetNameOverMax() {
	// setName("qwertyuiopåasdfghjklæøzxcvbnm");
	// assertNotNull(name);
	// assertEquals("qwertyuiopåasdfghjkl", name);
	// }
	// public void testSetGrindLow(){
	// setGrind(-22);
	// assertEquals(1, grind);
	// }
	//
	// public void testSetGrindHigh(){
	// setGrind(5);
	// assertEquals(3, grind);
	// }
	// public void testSetGrindMid(){
	// setGrind(2);
	// assertEquals(2, grind);
	// }

//	public void testUserWithRecipe() {
//		User user = new User(true, 1, "Klaus Klokmose", "kkni11@student.aau.dk");
//		ArrayList<Recipe> recipes = user.getRecipes();
//		assertNotNull(recipes);
//		assertEquals(7, recipes.size());
//	}
//
//	public void testRecipeActivityRecipe() {
//
//	}
//
//	public void setSugar(int sugar) {
//		// checking input
//		if (sugar > 10) {
//			sugar = 10;
//		} else if (sugar < 0) {
//			sugar = 0;
//		}
//		this.sugar = sugar;
//	}
}
