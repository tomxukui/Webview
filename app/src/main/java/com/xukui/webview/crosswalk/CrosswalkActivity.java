package com.xukui.webview.crosswalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.xukui.webview.R;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class CrosswalkActivity extends AppCompatActivity {

    private static final String TAG = "CrosswalkActivity";

    private static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";

    private Toolbar toolbar;
    private TextView tv_title;
    private XWalkView webView;

    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk);
        initData();
        initView();
        setView();

        webView.loadUrl(mAddress);
    }

    private void initData() {
        mAddress = getIntent().getStringExtra(EXTRA_ADDRESS);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        tv_title = findViewById(R.id.tv_title);
        webView = findViewById(R.id.webView);
    }

    private void setView() {
        //支持javascript
        XWalkPreferences.setValue("enable-javascript", true);
        //开启调式,支持谷歌浏览器调式
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        //设置是否允许通过file url加载的Javascript可以访问其他的源,包括其他的文件和http,https等其他的源
        XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
        //支持javascript打开窗口
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        //开启多窗口
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);

        //设置缩放
        XWalkSettings settings = webView.getSettings();
        settings.setSupportSpatialNavigation(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);

        //设置滑动
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollBarStyle(XWalkView.SCROLLBARS_OUTSIDE_INSET);
        webView.setScrollbarFadingEnabled(true);
        //不使用缓存
        webView.setDrawingCacheEnabled(false);
        //清除历史记录
        webView.getNavigationHistory().clear();
        //清楚包括磁盘缓存
        webView.clearCache(true);
        //设置回调
        webView.setUIClient(new XWalkUIClient(webView) {

            @Override
            public void onReceivedTitle(XWalkView view, String title) {
                super.onReceivedTitle(view, title);
                tv_title.setText(title);
            }

            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
                Log.d(TAG, "开始加载网页: " + url);
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
                if (LoadStatus.FINISHED == status) {
                    Log.d(TAG, "加载网页成功: " + url);

                } else if (LoadStatus.CANCELLED == status) {
                    Log.d(TAG, "加载网页已取消: " + url);

                } else {
                    Log.e(TAG, "加载网页失败: " + url);
                }
            }

        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (webView != null) {
            webView.onNewIntent(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (webView != null) {
            webView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.resumeTimers();
            webView.onShow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.pauseTimers();
            webView.onHide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.onDestroy();
        }
    }

    public static Intent buildIntent(Context context, String address) {
        Intent intent = new Intent(context, CrosswalkActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        return intent;
    }

}