package com.example.smartcoffee.test;

import android.test.ActivityInstrumentationTestCase2;
import com.example.smartcoffee.MainActivity;

public class TestMethods extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mainActiv;
	 private int sugar;
	// private int grind;
	private String name;
	private int grind;

	public TestMethods() {
		super("com.example.smartcoffee", MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mainActiv = this.getActivity();

	}

	public void testSetNameOverMaxChar() {
		setName("qwertyuiopåasdfghjklæøzxcvbnm");

		assertEquals("qwertyuiopåasdfghjkl", name);
	}
	
	public void testSetNameMaxChar() {
		setName("qwertyuiopåasdfghjkl");
		
		assertEquals("qwertyuiopåasdfghjkl", name);
	}
	

	public void setName(String name) {
		 if(name.length()>20){
		 name = name.substring(0, 20);
		 }
		 else if(name.length()==0){
		 return;
		 }
		this.name = name;
	}

//	 public void testSetSugarTooLow(){
//	 setSugar(-34);
//	 assertEquals(0, sugar);
//	 }
	//
	// public void testSetSugarTooHigh(){
	// setSugar(44);
	// assertEquals(10, sugar);
	// }
	//
	// public void testSetSugarMax(){
	// setSugar(10);
	// assertEquals(10, sugar);
	// }
	//
	// public void testSetSugarMin(){
	// setSugar(0);
	// assertEquals(0, sugar);
	// }
	//
	// public void testSetSugarMid(){
	// setSugar(5);
	// assertEquals(5, sugar);
	// }
	//
	// public void testSetGrindLow(){
	// setGrind(-22);
	// assertEquals(1, grind);
	// }
	//
	// public void testSetGrindHigh(){
	// setGrind(5);
	// assertEquals(3, grind);
	// }
	
	public void testSetGrindMid() {
		setGrind(2);
		assertEquals(2, grind);
	}
	
	public void setGrind(int grind) {
		if (grind > 3) {
			grind = 3;
		} else if (grind < 1) {
			grind = 1;
		}
		this.grind = grind;
	}
	
	
	 public void setSugar(int sugar) {
			// checking input
			if (sugar > 10) {
				sugar = 10;
			} else if (sugar < 0) {
				sugar = 0;
			}
			this.sugar = sugar;
		}
}
