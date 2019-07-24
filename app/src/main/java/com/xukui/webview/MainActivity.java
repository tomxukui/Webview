package com.xukui.webview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xukui.webview.crosswalk.CrosswalkActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_website;
    private EditText et_custom;
    private RadioGroup rg_webview;
    private Button btn_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setView();

        rg_website.check(R.id.rb_baidu);
        rg_webview.check(R.id.rb_crosswalk);
    }

    private void initView() {
        rg_website = findViewById(R.id.rg_website);
        et_custom = findViewById(R.id.et_custom);
        rg_webview = findViewById(R.id.rg_webview);
        btn_confirm = findViewById(R.id.btn_confirm);
    }

    private void setView() {
        rg_website.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                et_custom.setVisibility(checkedId == R.id.rb_custom ? View.VISIBLE : View.GONE);
            }

        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String address = getAddress();

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(MainActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (rg_webview.getCheckedRadioButtonId()) {

                    case R.id.rb_crosswalk: {
                        Intent intent = CrosswalkActivity.buildIntent(MainActivity.this, address);
                        startActivity(intent);
                    }
                    break;

                    case R.id.rb_x5: {
                        Toast.makeText(MainActivity.this, "暂不支持", Toast.LENGTH_SHORT).show();
                    }
                    break;

                    case R.id.rb_native: {
                        Toast.makeText(MainActivity.this, "暂不支持", Toast.LENGTH_SHORT).show();
                    }
                    break;

                    default:
                        break;

                }
            }

        });
    }

    private String getAddress() {
        switch (rg_website.getCheckedRadioButtonId()) {

            case R.id.rb_baidu: {
                return "https://www.baidu.com";
            }

            case R.id.rb_youku: {
                return "https://www.youku.com";
            }

            case R.id.rb_custom: {
                return et_custom.getText().toString().trim();
            }

            default:
                return null;

        }
    }

}