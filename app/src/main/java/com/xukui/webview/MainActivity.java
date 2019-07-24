package com.xukui.webview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xukui.webview.crosswalk.CrosswalkActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_crosswalk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();
    }

    private void initView() {
        btn_crosswalk = findViewById(R.id.btn_crosswalk);
    }

    private void setView() {
        btn_crosswalk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CrosswalkActivity.class);
                startActivity(intent);
            }

        });
    }

}