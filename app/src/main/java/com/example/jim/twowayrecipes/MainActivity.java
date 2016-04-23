package com.example.jim.twowayrecipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton buttonSearch;
    ImageButton buttonMake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Intent intent = new Intent(getApplicationContext(), RecepiesList.class);
                startActivity(intent);
            }
        });
    }


}
