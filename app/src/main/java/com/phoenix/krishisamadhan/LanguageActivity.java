package com.phoenix.krishisamadhan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;

public class LanguageActivity extends AppCompatActivity {
    Button languagebtn;
    ImageButton hindisel, engsel;
    SharedPreferences sharedPreferences;


    private static final String SHARED_PREF_NAME = "mypref";
    private static String KEY_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        languagebtn = findViewById(R.id.languagebtn);
        hindisel = findViewById(R.id.hindisel);
        engsel = findViewById(R.id.engsel);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        AtomicReference<String> name = new AtomicReference<>(sharedPreferences.getString(KEY_NAME, null));
        if(name.get()!=null) {

            Intent intent = new Intent(LanguageActivity.this, DiagnoseActivity.class);
            startActivity(intent);
        }

        hindisel.setOnClickListener(view -> {
            hindisel.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on_png));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            name.set("Hindi");
            editor.apply();

                });
        engsel.setOnClickListener(view -> {
            engsel.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on_png));

        });

        languagebtn.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            name.set("Hindi");
            editor.apply();
            startActivity(new Intent(LanguageActivity.this, LoginActivity.class));
            finish();

        });

    }

}
