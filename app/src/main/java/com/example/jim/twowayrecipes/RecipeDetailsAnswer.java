package com.example.jim.twowayrecipes;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
//holder object za server response

public class RecipeDetailsAnswer {

    @SerializedName("recipe")
    @Expose
    private RecipeDetails recipe;

    /**
     *
     * @return
     * The recipe
     */
    public RecipeDetails getRecipe() {
        return recipe;
    }

    /**
     *
     * @param recipe
     * The recipe
     */
    public void setRecipe(RecipeDetails recipe) {
        this.recipe = recipe;
    }

}
