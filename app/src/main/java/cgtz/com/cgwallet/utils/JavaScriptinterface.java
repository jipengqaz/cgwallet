package cgtz.com.cgwallet.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.widget.ProgressDialog;


/**
 * 与js交互时用到的方法类 类名叫JavaScriptinterface
 * 或在方法前加 @JavascriptInterface   否则不能调用到该方法的
 */
public class JavaScriptinterface {

    private ProgressDialog pd;
	private Context mContext;
    private WebView webVie;
	//这个一定要定义，要不在showToast()方法里没办法启动intent
	Activity activity;

    private String action;//传给服务器的
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                JSONObject json = new JSONObject(msg.obj.toString().trim());
                int success = json.optInt("success");
                if(success == 1){
                    save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @JavascriptInterface
    public void save(){
        webVie.post(new Runnable() {
            @Override
            public void run() {
//                User user = UserSession.getInstance().getLoginedUser();
                Log.e("aaaaaaaa",action);
//                webVie.loadUrl("javascript:CgtzAppActGetReward('"+user.getUserID()+"','"+user.getToken()+"','android-"+ Constants.version+"','"+action+"')");
            }
        });
    }

	/** Instantiate the interface and set the context */
	public JavaScriptinterface(Context c, WebView webView) {
        webVie = webView;
		mContext = c;
		activity = (Activity) c;
	}

	/** 与js交互时用到的方法，在js里直接调用的 */
    @JavascriptInterface
	public void showToast(String  i) {
//        if(i.equals("1")){
//            if (UserSession.getInstance().isLogined()) {
//                if (pd == null)
//                    pd = new CustomProgressDialog(activity, R.style.loading_dialog);//显示加载弹窗
//                if (pd != null) {
//                    pd.show();
//                }
//                action = i;
//            Token_Client.setToken(handler);
//            }else{
//                UtilityUtils.startLoginActivity(activity, false);//登录
//            }
        }
//    }
}
