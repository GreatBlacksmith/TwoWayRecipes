package com.example.jim.twowayrecipes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeListCheckboxActivity extends Activity {
    List<CharSequence> list = new ArrayList<CharSequence>();
    String[] ingredients;
    private List<Recipe> listRecipe;
    private RecyclerView recyclerView;
    private RecipesAdapter mAdapter;
    private int currPage;
    private boolean previousCallEmpty = false;
    StringBuilder stringBuilder;
    StringBuilder oldStringBuilder;
    ProgressDialog pDialog;
    HashMap<String,String> params;
    private boolean loading = true;
    private Parcelable recyclerViewState = null;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView.LayoutManager layoutManagerRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_checkbox);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view_checkbox);
        ingredients = getResources().getStringArray(R.array.ingredients_array);
        for(int i = 0;i<ingredients.length; i++){

            list.add(ingredients[i]);
        }
        currPage = 1;
        View openDialog = (View) findViewById(R.id.checkbox_layout);
        openDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Intialize  readable sequence of char values
                final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
                final AlertDialog.Builder builderDialog = new AlertDialog.Builder(RecipeListCheckboxActivity.this);
                builderDialog.setTitle("Select ingredients");
                int count = dialogList.length;
                boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

                // Creating multiple selection by using setMutliChoiceItem method
                builderDialog.setMultiChoiceItems(dialogList, is_checked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton, boolean isChecked) {
                            }
                        });

                builderDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ListView list = ((AlertDialog) dialog).getListView();
                                // make selected item in the comma seprated string
                                stringBuilder = new StringBuilder();
                                for (int i = 0; i < list.getCount(); i++) {
                                    boolean checked = list.isItemChecked(i);

                                    if (checked) {
                                        if (stringBuilder.length() > 0) stringBuilder.append(",");
                                        stringBuilder.append(list.getItemAtPosition(i));

                                    }
                                }

                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                                if (stringBuilder.toString().trim().equals("")) {

                                    ((TextView) findViewById(R.id.text)).setText(getResources().getString(R.string.ingredients_dropdown_placeholder));
                                    stringBuilder.setLength(0);

                                } else {
                                    currPage = 1;
                                    previousCallEmpty = false;
                                    doServerCall();

                                }
                            }
                        });

                builderDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView) findViewById(R.id.text)).setText(getResources().getString(R.string.ingredients_dropdown_placeholder));
                            }
                        });
                AlertDialog alert = builderDialog.create();
                alert.show();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManagerRef.getChildCount();
                    totalItemCount = layoutManagerRef.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;

                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            if(!previousCallEmpty) {
                                doServerCall();
                            }

                        }
                    }
                }
            }
        });
    }
    private void doServerCall(){

        pDialog = new ProgressDialog(RecipeListCheckboxActivity.this);
        pDialog.setMessage("Loading recepies..");
        pDialog.setCancelable(false);
        pDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.api_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeDetailsInterface recipeInterface = retrofit.create(RecipeDetailsInterface.class);
        params =  new LinkedHashMap<String,String>();
        params.put("key", getResources().getString(R.string.api_key));
        if(stringBuilder != null){
            params.put("q",stringBuilder.toString());
        }
        if(stringBuilder != oldStringBuilder){
            previousCallEmpty = false;
            oldStringBuilder = stringBuilder;
            listRecipe = new ArrayList<>();
        }
        if(currPage !=1){
            params.put("page",String.valueOf(currPage));
        }
        currPage++;
        Call<RecipeList> call = recipeInterface.getRecipeList(params);
        call.enqueue(new Callback<RecipeList>() {
            @Override
            public void onResponse(Call<RecipeList> call, Response<RecipeList> response) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    if (response.body().getCount() == 0){
                        previousCallEmpty = true;
                    }
                    if (response.body().getCount() == 0 && currPage == 2) {
                        Toast.makeText(getApplicationContext(), "Can't find recipe matching criteria :)", Toast.LENGTH_SHORT).show();
                    } else {
                        RecipeList recipeList = response.body();
                        for (Recipe rec : recipeList.getRecipes()) {
                            listRecipe.add(rec);
                        }
                        mAdapter = new RecipesAdapter(listRecipe);
                        mAdapter.notifyDataSetChanged();
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        layoutManagerRef = mLayoutManager;
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                        if (recyclerViewState != null && currPage != 2) {
                            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }
                        loading = true;
                        //    mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeList> call, Throwable t) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
