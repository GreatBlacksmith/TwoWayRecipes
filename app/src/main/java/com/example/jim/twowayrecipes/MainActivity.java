package com.example.jim.twowayrecipes;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends Activity {

    ImageButton buttonSearch;
    ImageButton buttonMake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        buttonSearch = (ImageButton)findViewById(R.id.imageButtonSearch);
        buttonMake = (ImageButton)findViewById(R.id.imageButtonMake);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecepiesList.class);
                startActivity(intent);
            }
        });

        buttonMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecipeListCheckboxActivity.class);
                startActivity(intent);
            }
        });
    }


}
