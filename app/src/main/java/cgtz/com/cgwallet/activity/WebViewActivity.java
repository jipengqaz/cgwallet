package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.ScreenUtils;

/**
 * Created by Administrator on 2015/4/21.
 */
public class WebViewActivity extends BaseActivity {

        public WebView webView;
        private TextView pb;
        private int Progress;
        private int width;
    private ViewGroup.LayoutParams lp;

        private String title;
        private String url;
        private int type;
        private long id;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_webview);
            showBack(true);
            MApplication.registActivities(this);//存储该activity
            webView = (WebView) findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);//可用JS
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            //添加手机缩放
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);//出现缩放工具
            webView.getSettings().setUseWideViewPort(true);//扩大比例缩放

            /** 与js交互，JavaScriptinterface 是个接口，与js交互时用到的，
             *  这个接口实现了从网页跳到app中的activity 的
             *  方法，特别重要
             * **/
//            webView.addJavascriptInterface(new JavascriptInterface(this, webView),
//                    "android");
//        webView.addJavascriptInterface(new MyJavaScriptInterface(this),
//                "android");

            //自适应屏幕
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setLoadWithOverviewMode(true);

            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
            CookieManager.getInstance().setAcceptCookie(true);
            pb= (TextView) findViewById(R.id.progress_bar_2);
            lp = pb.getLayoutParams();
            width = ScreenUtils.getScreenWidth(this)/100;
            webView.setWebChromeClient(new WebChromeClient(){//用于修改进度条
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    Progress=newProgress;
                    if(newProgress <100){
                        lp.width = width*Progress;
                        pb.setLayoutParams(lp);
                    }else{
                        pb.setVisibility(View.GONE);
                    }
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    try {
                        Uri uri = Uri.parse(url);
                        if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                            view.loadUrl(url); //载入网页
                            return true;
                        }
                    } catch (Exception e) {
                    }
                    return true;
                }//重写点击动作,用webview载入

                @Override
                public void onReceivedSslError(final WebView view, SslErrorHandler handler,
                                               SslError error) {
                    handler.proceed();
                }
            });

            if(savedInstanceState != null){
                id = savedInstanceState .getLong("projectID", 0);
                type =savedInstanceState .getInt("type", 0);
                if (type !=0 ) {
                    webView.loadUrl(url);
                }else {
                    url = savedInstanceState.getString("url");
                    title = savedInstanceState.getString("title");
                    setTitle(title);
                    webView.loadUrl(url);
                }
            }else{
                Intent intent = getIntent();
                id = intent.getLongExtra("projectID", 0);
                type = intent.getIntExtra("type", 0);
                if (type !=0 ) {
//                    webView.loadUrl(url);
                }else {
                    url = getIntent().getStringExtra("url");
                    title = getIntent().getStringExtra("title");
                    setTitle(title);
                    webView.loadUrl(url);
                }
            }
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onBackPressed() {
            finish();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

        /**
         * Activity被系统杀死时被调用.
         * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
         * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
         * 在onPause之前被调用.
         */
        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("url",url);
            outState.putString("title", title);
            outState.putLong("projectID", id);
            outState.putInt("type", type);
        }

}
