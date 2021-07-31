package com.example.imhungry3.Class;


public class IngredientsItemClass {
    private String ingredientName;
    private String ingredientsImage;
    public IngredientsItemClass(String ingredientName, String ingredientsImage) {
        this.ingredientName = ingredientName;
        this.ingredientsImage = ingredientsImage;
    }
    public String getIngredientName() {
        return ingredientName;
    }
    public String getIngredientImage() {
        return ingredientsImage;
    }
}
