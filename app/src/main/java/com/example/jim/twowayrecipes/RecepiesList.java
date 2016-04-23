package com.example.jim.twowayrecipes;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RecepiesList extends ListActivity {

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
        setContentView(R.layout.activity_recepies_list);


        new GetRecipies().execute();
    }
    private class GetRecipies extends AsyncTask<Void,Void,Void>{

        //Hashmap for the ListView
        ArrayList<HashMap<String,String>> recepiesList;
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

            ListAdapter adapter = new SimpleAdapter(RecepiesList.this, recepiesList, R.layout.list_item, new String[]{
                    TAG_PUBLISHER, TAG_TITLE, TAG_SOURCE_URL}, new int[]{R.id.name, R.id.email, R.id.mobile});

            setListAdapter(adapter);
        }
        }
    private ArrayList<HashMap<String,String>> ParseJSON(String json){
        if(json != null){
            try{
                //Hashmap za list view
                ArrayList<HashMap<String,String>> recepiesList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                JSONArray recepies = jsonObj.getJSONArray("recipes");

                for(int i = 0; i<recepies.length(); i++){
                    JSONObject c = recepies.getJSONObject(i);

                    String publisher = c.getString(TAG_PUBLISHER);
                    String publisher_url = c.getString(TAG_PUBLISHER_URL);
                    String title = c.getString(TAG_TITLE);
                    //TO_DO dodat ostale

                    HashMap<String,String> recepie = new HashMap<String,String>();
                    recepie.put(TAG_PUBLISHER,publisher);
                    recepie.put(TAG_PUBLISHER_URL,publisher_url);
                    recepie.put(TAG_TITLE,title);

                    recepiesList.add(recepie);

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
