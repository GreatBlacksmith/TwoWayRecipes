package com.example.jim.twowayrecipes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
//zove servis sa idom kliknutog recepta, te postavlja sve vrijednosti iz responsa u elemente na viewu
public class RecipeDetailsActivity extends Activity {

    private TextView titleOnImage;
    private TextView ingredients;
    private RatingBar scoreBar;
    private TextView authorTextView;
    private SimpleDraweeView imageView;
    private Button authorButton;
    private Button recipeButton;
    private String authorUrl;
    private String recipeUrl;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Verdana.ttf");
        this.titleOnImage = (TextView)findViewById(R.id.titleImageViewText);
        this.titleOnImage.setTypeface(type);
        this.ingredients = (TextView)findViewById(R.id.textViewIngridients);
        this.ingredients.setTypeface(type);
        this.scoreBar = (RatingBar)findViewById(R.id.ratingBarRecepie);
        this.authorTextView = (TextView)findViewById(R.id.textViewAuthor);
        this.authorTextView.setTypeface(type);
        this.imageView = (SimpleDraweeView) findViewById(R.id.recipeDetailsImageView);
        this.authorButton = (Button) findViewById(R.id.imageButtonAuthor);
        this.recipeButton = (Button) findViewById(R.id.imageButtonFullRecipe);
        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pDialog = new ProgressDialog(RecipeDetailsActivity.this);
        pDialog.setMessage("Loading recipe..");
        pDialog.setCancelable(false);
        pDialog.show();

        RecipeDetailsInterface recipeInterface = retrofit.create(RecipeDetailsInterface.class);
        HashMap<String,String> params = new LinkedHashMap<>();
        params.put("key",getResources().getString(R.string.api_key));
        params.put("rId", recipeId);
        Call<RecipeDetailsAnswer> call = recipeInterface.getRecipeDetails(params);
        call.enqueue(new Callback<RecipeDetailsAnswer>() {
            @Override
            public void onResponse(Call<RecipeDetailsAnswer> call, Response<RecipeDetailsAnswer> response) {
                if(response.isSuccessful()){

                    RecipeDetailsAnswer recipeDetailsAnswer = response.body();
                    authorUrl = recipeDetailsAnswer.getRecipe().getPublisherUrl();
                    recipeUrl = recipeDetailsAnswer.getRecipe().getSourceUrl();
                    titleOnImage.setText(recipeDetailsAnswer.getRecipe().getTitle());
                    ingredients.append(" Ingredients: \n");
                    for( int i=0; i< recipeDetailsAnswer.getRecipe().getIngredients().size();i++){
                        ingredients.append(" \u2022 " + recipeDetailsAnswer.getRecipe().getIngredients().get(i) + "\n");
                    }
                    scoreBar.setProgress(recipeDetailsAnswer.getRecipe().getSocialRank().intValue());
                    scoreBar.setFocusable(false);
                    authorTextView.setText(recipeDetailsAnswer.getRecipe().getPublisher());
                    Uri uri = Uri.parse(recipeDetailsAnswer.getRecipe().getImageUrl());
                    imageView.setImageURI(uri);

                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeDetailsAnswer> call, Throwable t) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        authorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authorUrl == null) {
                    Toast.makeText(getApplicationContext(), "This author doesn't have webpage.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(authorUrl));
                    startActivity(intent);
                }
            }
        });
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recipeUrl == null){
                    Toast.makeText(getApplicationContext(),"This recipe doesn't have webpage.",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(recipeUrl));
                    startActivity(intent);
                }
            }
        });
    }
}
