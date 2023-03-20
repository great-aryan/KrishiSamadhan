package com.phoenix.krishisamadhan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LanguageActivity extends AppCompatActivity {
    Button languagebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        languagebtn = findViewById(R.id.languagebtn);

        languagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LanguageActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}