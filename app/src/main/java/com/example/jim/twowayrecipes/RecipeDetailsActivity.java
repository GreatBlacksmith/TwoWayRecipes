package com.example.jim.twowayrecipes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeDetailsActivity extends Activity {

    private TextView titleOnImage;
    private TextView ingredients;
    private RatingBar scoreBar;
    private TextView authorTextView;
    private SimpleDraweeView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        this.titleOnImage = (TextView)findViewById(R.id.titleImageViewText);
        this.ingredients = (TextView)findViewById(R.id.textViewIngridients);
        this.scoreBar = (RatingBar)findViewById(R.id.ratingBarRecepie);
        this.authorTextView = (TextView)findViewById(R.id.textViewAuthor);
        this.imageView = (SimpleDraweeView) findViewById(R.id.recipeDetailsImageView);
        scoreBar.setNumStars(5);
        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RecipeDetailsInterface recipeInterface = retrofit.create(RecipeDetailsInterface.class);
        HashMap<String,String> params = new LinkedHashMap<String,String>();
        params.put("key",getResources().getString(R.string.api_key));
        params.put("rId", recipeId);
        Call<RecipeDetailsAnswer> call = recipeInterface.getRecipeDetails(params);
        call.enqueue(new Callback<RecipeDetailsAnswer>() {
            @Override
            public void onResponse(Call<RecipeDetailsAnswer> call, Response<RecipeDetailsAnswer> response) {
                if(response.isSuccessful()){
                    RecipeDetailsAnswer recipeDetailsAnswer = response.body();
                    titleOnImage.setText(recipeDetailsAnswer.getRecipe().getTitle());
                    ingredients.append("Ingredients: \n");
                    for( int i=0; i< recipeDetailsAnswer.getRecipe().getIngredients().size();i++){
                        ingredients.append("-" + recipeDetailsAnswer.getRecipe().getIngredients().get(i) + "\n");
                    }
                    scoreBar.setProgress(recipeDetailsAnswer.getRecipe().getSocialRank().intValue()/20);
                    authorTextView.setText(recipeDetailsAnswer.getRecipe().getPublisher());
                    Uri uri = Uri.parse(recipeDetailsAnswer.getRecipe().getImageUrl());
                    imageView.setImageURI(uri);
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailsAnswer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
