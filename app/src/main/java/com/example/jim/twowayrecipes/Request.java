package com.example.jim.twowayrecipes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jim on 4/23/2016.
 */
public class Request {
    static String response = null;
    public final static int GET = 1;
    public Request(){
    }

    public String makeWebServiceCall(String url){
        return this.makeWebServiceCall(url ,null);
    }

    public String makeWebServiceCall(String urladdress,
                                     HashMap<String,String> params){
        URL url;
        response = "";
        try{
            url = new URL(urladdress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");

            if(params != null){
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for(Map.Entry<String,String> entry : params.entrySet()){
                    if(first) {
                        first = false;
                    }else {
                        result.append("&");
                    }
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
                }
                writer.write(result.toString());

                writer.flush();
                writer.close();
                os.close();


            }
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine()) != null) {
                    response += line;
                }
            }else{
                    response="";
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    return response;
    }
}
