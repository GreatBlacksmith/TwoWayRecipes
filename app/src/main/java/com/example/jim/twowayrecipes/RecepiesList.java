package com.example.jim.twowayrecipes;


import android.app.ProgressDialog;
import android.content.Context;
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


import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecepiesList extends Activity {

    private List<Recipe> recipeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipesAdapter mAdapter;
    private EditText searchText;
    private Button searchButton;
    private String searchTherm;
    //URL for the recepies
    private static String url = "http://food2fork.com/api/search";

    //Tags for Json
    private static final String TAG_PUBLISHER = "publisher";
    private static final String TAG_IMAGE_URL = "image_url";
    private static final String TAG_SOURCE_URL = "source_url";
    private static final String TAG_F2F_URL = "f2f_url";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PUBLISHER_URL = "publisher_url";
    private static final String TAG_SOCIAL_RANK = "social_rank";
    private static final String TAG_INGREDIENTS = "ingredients";
    private static final String TAG_COUNT = "count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_recepies_list);


        searchText = (EditText) findViewById(R.id.searchList);
        searchButton = (Button) findViewById(R.id.search_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_view);

        new GetRecipies().execute();
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                hideKeyboard(textView);
                searchButton.requestFocus();
                searchButton.performClick();
                return true;
            }
        });



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTherm = searchText.getText().toString();
                hideKeyboard(view);
                new GetRecipies().execute();
            }
        });
    }
    private void hideKeyboard(View view){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private class GetRecipies extends AsyncTask<Void,Void,Void>{

        //Hashmap for the ListView
        ArrayList<Recipe> recepiesList;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            pDialog = new ProgressDialog(RecepiesList.this);
            pDialog.setMessage("Loading recepies..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            Request webreq = new Request();
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("key", getResources().getString(R.string.api_key));


            if(searchTherm != null){
                params.put("q",searchTherm);
            }
            String jsonStr = webreq.makeWebServiceCall(url,params);
            Log.d("Response: ", "> " + jsonStr);
            recepiesList = ParseJSON(jsonStr);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            mAdapter = new RecipesAdapter(recepiesList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        }
        }
    private ArrayList<Recipe> ParseJSON(String json){
        if(json != null){
            try{
                //Hashmap za list view
                ArrayList<Recipe> recepiesList = new ArrayList<>();
                JSONObject jsonObj = new JSONObject(json);

                JSONArray recepies = jsonObj.getJSONArray("recipes");

                for(int i = 0; i<recepies.length(); i++){
                    JSONObject c = recepies.getJSONObject(i);

                    String publisher = c.getString(TAG_PUBLISHER);
                    String image_url = c.getString(TAG_IMAGE_URL);
                    String title = c.getString(TAG_TITLE);
                    //TO_DO dodat ostale

                    Recipe rec = new Recipe();
                    rec.setTitle(title);
                    rec.setImage_url(image_url);

                    recepiesList.add(rec);

                }
                return recepiesList;
            }catch(JSONException e){
                e.printStackTrace();
                return null;
            }
        }else{
            Log.e("ServiceHandler","Coudn't get that from the url");
            return null;
        }
    }
}
