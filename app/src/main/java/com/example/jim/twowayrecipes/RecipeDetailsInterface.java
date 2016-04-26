package com.example.jim.twowayrecipes;



import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


/**
 * Created by Jim on 4/25/2016.
 */
public interface RecipeDetailsInterface {
    @GET("get")
    Call<RecipeDetailsAnswer> getRecipeDetails(@QueryMap Map<String, String> querys);

    @GET("search")
    Call<RecipeList> getRecipeList(@QueryMap Map<String, String> querys);
}
