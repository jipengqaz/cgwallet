package cgtz.com.cgwallet.fragment;

import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.JavaScriptinterface;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.ScreenUtils;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by chen on 2015-11-5.
 */
public class Invest_web_fragment extends BaseFragment {
    private String TAG = "Invest_web_fragment";
    public WebView webView;
    private TextView pb;
    private int Progress;
    private int width;
    private ViewGroup.LayoutParams lp;
    private String url = Constants.WALLET_INVEST;
    private String urlWin = Constants.WALLET_INVEST_WX;

    private String loginMobileWebsite(String mobile, String password) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        String checkMobile = "http://m.cgtz.com/site/CheckMobile.html";
        String checkMobile = url+"/site/CheckMobile.html";
        Map<String, String> checkMobileParams = new HashMap<>();
        checkMobileParams.put("mobile", mobile);
        httpPost(httpClient, checkMobile, checkMobileParams);
//        String login = "http://m.cgtz.com/login2.html";
        String login = url+"/login2.html";
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("LoginForm[password]", password);
        CloseableHttpResponse response = httpPost(httpClient, login, loginParams);
        Header[] cookies = response.getHeaders("Set-Cookie");
        String phpSessionId = null;
        for (Header cookie : cookies) {
            String value = cookie.getValue();
            if (value.startsWith("PHPSESSID")) {
                phpSessionId = value;
                break;
            }
        }
        if (phpSessionId == null) {
            throw new RuntimeException("无法获取PHPSESSID");
        }
        return phpSessionId;
    }

    private CloseableHttpResponse httpPost(CloseableHttpClient httpClient, String url, Map<String, String> data) {
        HttpPost httpPost = null;
        String charset = "UTF-8";
        try {
            httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();
            for (String dataKey : data.keySet()) {
                nvps.add(new BasicNameValuePair(dataKey, data.get(dataKey)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("网络故障", e);
        } finally {
            try {
                if (httpPost != null) {
                    httpPost.abort();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新页面
     */
    public void reload() {
        webView.reload();
    }

    public void synCookies(Context context, String url, String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        if(cookies != null) {
            cookieManager.setCookie(url, cookies);
        }
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_webview, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//可用JS
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//优先使用缓存
        //添加手机缩放
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);//出现缩放工具
        webView.getSettings().setUseWideViewPort(true);//扩大比例缩放
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        CookieManager.getInstance().setAcceptCookie(true);
        pb = (TextView) view.findViewById(R.id.progress_bar_2);
        /** 与js交互，JavaScriptinterface 是个接口，与js交互时用到的，
         *  这个接口实现了从网页跳到app中的activity 的
         *  方法，特别重要
         * **/
        webView.addJavascriptInterface(new JavaScriptinterface(getActivity(), webView),
                "android");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        String phpSessionId = null;
        try {
//获取存储的手机号和密码
            String mobile = Utils.getUserPhone(getActivity());
            String password = Utils.getLoginPwd(getActivity());
//根据手机号和密码拿到SessionId
            phpSessionId = this.loginMobileWebsite(mobile, password);
        } catch (Throwable throwable) {
        }
        synCookies(getActivity(), Constants.WALLET_INVEST, phpSessionId);
        setClient();
        LogUtils.i(TAG, "访问路径：" + url);
        webView.loadUrl(url);
        return view;

    }

    /**
     * 设置事件
     */
    private void setClient() {
        lp = pb.getLayoutParams();
        width = ScreenUtils.getScreenWidth(getActivity()) / 100;
        webView.setWebChromeClient(new WebChromeClient() {//用于修改进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Progress = newProgress;
                if (newProgress < 100) {
                    lp.width = width * Progress;
                    pb.setLayoutParams(lp);
                } else {
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
    }
}