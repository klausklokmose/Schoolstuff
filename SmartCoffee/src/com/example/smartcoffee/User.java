package com.example.smartcoffee;

import java.util.ArrayList;

public class User {
	private boolean loggedIn;
	private int userID;
	private String userName;
	private String emailAddress;
	private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	
	public User(boolean loggedIn, int userID, String userName, String emailAdd){
		this.loggedIn = loggedIn;
		this.userID = userID;
		this.userName = userName;
		this.emailAddress = emailAdd;

		recipes.add(new Recipe(1, 1, false, "Favorite coffee", 0, 2, 2, false,
				true, 9, 0, 0, 0));
		recipes.add(new Recipe(2, 1, false, "Espresso", 1, 3, 3, false, true, 0,
				2, 5, 0));
		recipes.add(new Recipe(3, 1, false, "Hot coco", 2, 1, 1, true, true, 0, 0,
				2, 0));
		recipes.add(new Recipe(4, 1, false, "Sweeeeeet", 4, 1, 1, true, false, 10,
				7, 2, 0));
		recipes.add(new Recipe(5, 1, false, "Mondays", 5, 3, 3, false, false, 0,
				2, 3, 0));
		recipes.add(new Recipe(6, 1, false, "Tuesdays", 6, 2, 2, false, true, 2,
				3, 0, 0));
		recipes.add(new Recipe(8, 1, false, "Wednessdays", 7, 3, 3, false, true,
				2, 2, 3, 0));
		
	}
	
	public ArrayList<Recipe> getRecipes(){
		return recipes;
	}
	
	public void queryForRecipes(){
		//database connection
		
		//save result in recipes
		recipes.add(new Recipe(1, 1, false, "Favorite coffee", 0, 2, 2, false,
				true, 9, 0, 0, 0));
		recipes.add(new Recipe(2, 1, false, "Espresso", 1, 3, 3, false, true, 0,
				2, 5, 0));
		recipes.add(new Recipe(3, 1, false, "Hot coco", 2, 1, 1, true, true, 0, 0,
				2, 0));
		recipes.add(new Recipe(4, 1, false, "Sweeeeeet", 4, 1, 1, true, false, 10,
				7, 2, 0));
		recipes.add(new Recipe(5, 1, false, "Mondays", 5, 3, 3, false, false, 0,
				2, 3, 0));
		recipes.add(new Recipe(6, 1, false, "Tuesdays", 6, 2, 2, false, true, 2,
				3, 0, 0));
		recipes.add(new Recipe(8, 1, false, "Wednessdays", 7, 3, 3, false, true,
				2, 2, 3, 0));
	}
	
	public void updateRecipe(Recipe recipe){
		//database connection
//		int id = recipe.getRecipeID();
		
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		//Save in database
	}
}
