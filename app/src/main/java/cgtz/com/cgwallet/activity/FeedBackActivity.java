package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.Start_update_value;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "Feed_back_Activity";
    private EditText feed_advise;
    private Button feed_send;
    //输入表情前EditText中的文本
    private String tmp;
    //是否重置了EditText的内容
    private boolean resetText;

    private SplashPresenter presenter;
    private String editMsg;
    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            if(code == Constants.DATA_EVENT){
                Utils.makeToast(FeedBackActivity.this,Constants.ERROR_MSG_CODE+code);
                return;
            }
            switch (what){
                case Constants.WHAT_FEED_BACK://意见反馈，服务器结果返回
                    boolean flag = Utils.filtrateCode(FeedBackActivity.this,jsonBean);
                    if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                        Utils.makeToast(FeedBackActivity.this,errorMsg);
                    }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                        Utils.makeToast(FeedBackActivity.this,errorMsg);
                        finish();
                    }else{
                        progressDialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setTitle("意见反馈");
        showBack(true);
        presenter = new SplashPresenter(this);
        MApplication.registActivities(this);//存储该activity
        TextView ke_fu= (TextView) findViewById(R.id.ke_fu);
        ke_fu.setText(Ke_Fu_data.getContent(this));
        feed_advise= (EditText) findViewById(R.id.feed_advise);
        feed_send= (Button) findViewById(R.id.feed_send);
        setListener();

    }

    private void setListener(){
        feed_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMsg = feed_advise.getText().toString();//获取输入的内容
                if(TextUtils.isEmpty(editMsg)){
                    Utils.makeToast(FeedBackActivity.this,"请输入意见内容");
                }else{
                    presenter.didFinishLoading(FeedBackActivity.this);
                }
            }
        });
//        feed_advise.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String str = s.toString();
//                if(!resetText){
//                    char codePoint = str.charAt(start-1);
//                    if(isEmojiCharacter(codePoint)){
//                        resetText = true;
//                        //是表情符号就将文本还原为输入表情符号之前的内容
//                        feed_advise.setText(tmp);
//                        feed_advise.setSelection(tmp.length());
//                        Utils.makeToast(FeedBackActivity.this, "不支持表情输入");
//                    }
//                }else{
//                    resetText = false;
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if(!resetText) {
//                    tmp = s.toString();//这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });//设置判断
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.closeDialog(this, progressDialog);
    }

    /**
     * 是否包含表情
     *
     * @param codePoint
     * @return 如果不包含 返回false,包含 则返回true
     */

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }else if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        progressDialog.dismiss();
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this, "错误");
    }

    @Override
    public void startNextActivity() {
        //服务器数据交互操作
        HashMap<String,String> maps = new HashMap<>();
        maps.put("user_id",Utils.getUserId());
        maps.put("token",Utils.getToken());
        maps.put("advise",editMsg);
        maps.put("source","1");
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_FEED_BACK,
                                                    Constants.URL_FEED_BACK,
                                                    true,maps,true);
        task.execute();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
