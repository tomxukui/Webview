package com.xukui.webview.crosswalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xukui.webview.R;

import org.xwalk.core.XWalkView;

public class CrosswalkActivity extends AppCompatActivity {

    private XWalkView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk);
        initView();
        setView();
    }

    private void initView() {
        webView = findViewById(R.id.webView);
    }

    private void setView() {
        webView.loadUrl("https://www.baidu.com");
    }

}