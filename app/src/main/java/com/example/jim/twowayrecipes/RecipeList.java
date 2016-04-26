package com.example.jim.twowayrecipes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeList {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("recipes")
    @Expose
    private List<Recipe> recipes = new ArrayList<>();

    /**
     *
     * @return
     * The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     *
     * @param count
     * The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     *
     * @return
     * The recipes
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     *
     * @param recipes
     * The recipes
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

}