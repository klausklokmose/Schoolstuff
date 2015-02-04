package com.example.smartcoffee;

public class Recipe {

	private int recipeID;
	private int userOriginID;
	private boolean publicRecipe;
	private String name;
	private int iconReference;
	private int strength;
	private int grind;
	private boolean decaf;
	private boolean foam;
	private int sugar;
	private int sweetener;
	private int cream;
	private long price;

	public Recipe(int recipeID, int userOriginID, boolean publicRecipe,
			String name, int iconReference, int strength, int grind,
			boolean decaf, boolean foam, int sugar, int sweetener, int cream,
			long price) {
		super();
		this.recipeID = recipeID;
		this.userOriginID = userOriginID;
		this.publicRecipe = publicRecipe;
		this.name = name;
		this.iconReference = iconReference;
		this.strength = strength;
		this.grind = grind;
		this.decaf = decaf;
		this.foam = foam;
		this.sugar = sugar;
		this.sweetener = sweetener;
		this.cream = cream;
		this.price = price;
	}

	public int getRecipeID() {
		return recipeID;
	}

	public void setRecipeID(int recipeID) {
		this.recipeID = recipeID;
	}

	public int getUserOriginID() {
		return userOriginID;
	}

	public void setUserOriginID(int userOriginID) {
		this.userOriginID = userOriginID;
	}

	public boolean isPublicRecipe() {
		return publicRecipe;
	}

	public void setPublicRecipe(boolean publicRecipe) {
		this.publicRecipe = publicRecipe;
	}

	public String getName() {
		return name;
	}

	public void setName(String tempName) {
		if (tempName.length() > 20) {
			tempName = tempName.substring(0, 20);
		} else if (tempName.length() == 0) {
			if(name.length()==0){
				tempName = "Recipe Name";
			}else{
				return;
			}
		}
		this.name = tempName.trim();
	}

	public int getIconReference() {
		return iconReference;
	}

	public void setIconReference(int iconReference) {
		this.iconReference = iconReference;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		if (strength > 3) {
			strength = 3;
		} else if (strength < 1) {
			strength = 1;
		}
		this.strength = strength;
	}

	public int getGrind() {
		return grind;
	}

	public void setGrind(int grind) {
		if (grind > 3) {
			grind = 3;
		} else if (grind < 1) {
			grind = 1;
		}
		this.grind = grind;
	}

	public boolean isDecaf() {
		return decaf;
	}

	public void setDecaf(boolean decaf) {
		this.decaf = decaf;
	}

	public boolean isFoam() {
		return foam;
	}

	public void setFoam(boolean foam) {
		this.foam = foam;
	}

	public int getSugar() {
		return sugar;
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

	public int getSweetener() {
		return sweetener;
	}

	public void setSweetener(int sweetener) {
		// checking input
		if (sweetener > 10) {
			sweetener = 10;
		} else if (sweetener < 0) {
			sweetener = 0;
		}
		this.sweetener = sweetener;
	}

	public int getCream() {
		return cream;
	}

	public void setCream(int cream) {
		// checking input
		if (cream > 10) {
			cream = 10;
		} else if (cream < 0) {
			cream = 0;
		}
		this.cream = cream;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		// checking input
		if (price < 0) {
			price = 0;
		}
		this.price = price;
	}

}
