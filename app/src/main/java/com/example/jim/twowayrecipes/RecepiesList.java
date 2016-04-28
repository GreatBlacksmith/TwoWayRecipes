package com.example.jim.twowayrecipes;


import android.app.ProgressDialog;
import android.content.Context;

import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecepiesList extends Activity {

    private List<Recipe> listOfRecipes;
    private RecyclerView recyclerView;
    private RecipesAdapter mAdapter;
    private EditText searchText;
    private String searchTherm;
    private String oldSearchTherm;
    private int currPage = 1;
    ProgressDialog pDialog;
    HashMap<String,String> params;
    private int loaded = 0;
    private boolean loading = true;
    private boolean previousCallEmpty = false;
    private Parcelable recyclerViewState = null;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RecyclerView.LayoutManager layoutManagerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepies_list);
        listOfRecipes = new ArrayList<>();
        searchText = (EditText) findViewById(R.id.searchList);;
        searchTherm = searchText.getText().toString();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);

        this.doServerCall();
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                hideKeyboard(textView);
                searchTherm = searchText.getText().toString();

                if (searchTherm != null) {
                    currPage = 1;
                }
                previousCallEmpty = false;
                doServerCall();
                return true;
            }
        });
      //  layoutManagerRef = recyclerView.getLayoutManager();
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
    private void hideKeyboard(View view){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void doServerCall(){

        pDialog = new ProgressDialog(RecepiesList.this);
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
        if(searchTherm != null){
            params.put("q",searchTherm);
        }
        if(searchTherm != oldSearchTherm){
            previousCallEmpty = false;
            oldSearchTherm = searchTherm;
            listOfRecipes = new ArrayList<>();
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
               // list =  new ArrayList<Recipe>();
                if(response.isSuccessful()) {

                    if (response.body().getCount() == 0){
                        previousCallEmpty = true;
                    }

                    if (response.body().getCount() == 0 && currPage == 2) {
                        Toast.makeText(getApplicationContext(), "Can't find recipe matching criteria :)", Toast.LENGTH_SHORT).show();
                    } else {
                        RecipeList recipeList = response.body();
                        loaded = recipeList.getCount();
                        for (Recipe rec : recipeList.getRecipes()) {
                            listOfRecipes.add(rec);
                        }
                        mAdapter = new RecipesAdapter(listOfRecipes);
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
                        // mAdapter.notifyDataSetChanged();
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
