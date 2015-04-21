package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "Feed_back_Activity";
    private EditText feed_advise;
    //输入表情前EditText中的文本
    private String tmp;
    //是否重置了EditText的内容
    private boolean resetText;
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!resetText){
                char codePoint = s.charAt(start);
                if(isEmojiCharacter(codePoint)){
                    resetText = true;
                    //是表情符号就将文本还原为输入表情符号之前的内容
                    feed_advise.setText(tmp);
                    feed_advise.setSelection(tmp.length());
                    Utils.makeToast(FeedBackActivity.this, "不支持表情输入");
                }
            }else{
                resetText = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(!resetText) {
                tmp = s.toString();//这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private SplashPresenter presenter;
    private String editMsg;
    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what){

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
        progressDialog = new ProgressDialog(this);
        TextView ke_fu= (TextView) findViewById(R.id.ke_fu);
//        ke_fu.setText(KeFu_Share.getSaveKefu(this));
        feed_advise= (EditText) findViewById(R.id.feed_advise);
        feed_advise.addTextChangedListener(watcher);//设置判断
        Button feed_send= (Button) findViewById(R.id.feed_send);
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
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        progressDialog.dismiss();
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this,"错误");
    }

    @Override
    public void startNextActivity() {
        //服务器数据交互操作

    }
}
