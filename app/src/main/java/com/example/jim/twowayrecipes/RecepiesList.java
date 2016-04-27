package com.example.jim.twowayrecipes;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
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


import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private List<Recipe> list;
    private RecyclerView recyclerView;
    private RecipesAdapter mAdapter;
    private EditText searchText;
    private String searchTherm;
    ProgressDialog pDialog;
    HashMap<String,String> params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepies_list);

        searchText = (EditText) findViewById(R.id.searchList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);

        this.doServerCall();
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                hideKeyboard(textView);
                searchTherm = searchText.getText().toString();
                if(searchTherm != null){
                    params.put("q",searchTherm);
                }
                doServerCall();
                return true;
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
        Call<RecipeList> call = recipeInterface.getRecipeList(params);
        call.enqueue(new Callback<RecipeList>() {
            @Override
            public void onResponse(Call<RecipeList> call, Response<RecipeList> response) {
                if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
                list =  new ArrayList<Recipe>();
                if(response.isSuccessful()){
                    RecipeList recipeList = response.body();
                    for(Recipe rec : recipeList.getRecipes()){
                        list.add(rec);
                    }
                    mAdapter = new RecipesAdapter(list);
                    mAdapter.notifyDataSetChanged();
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
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
