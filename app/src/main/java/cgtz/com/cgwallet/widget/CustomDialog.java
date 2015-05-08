package cgtz.com.cgwallet.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.R;

/**
 * TODO: document your custom view class.
 */
public class CustomDialog extends Dialog {
    private Context context;
    private TextView msgText;
    private TextView confirmBtn;
    private TextView invest_hint_dialog;
    private LinearLayout investHelpLayout;//投资页面的问号提示
    private View mView;
    private String msg;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public CustomDialog(Context context, int cancelable) {
        super(context, cancelable);
        this.context = context;
        init();
    }
    public CustomDialog(Context context, int cancelable, String msg) {
        super(context, cancelable);
        this.context = context;
        this.msg = msg;
        init();
    }

    /**
     * 添加信息内容
     * @param msg
     */
    public void setMessage(String msg){
        msgText.setText(msg);
    }

    /**
     * 按钮添加点击事件
     * @param listener
     */
    public void setConfirmListener(View.OnClickListener listener){
        confirmBtn.setOnClickListener(listener);
    }

    /**
     * 按钮添加内容
     * @param text
     */
    public void setConfirmBtnText(String text){
        confirmBtn.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        this.setCanceledOnTouchOutside(false);//点击屏幕不消失
    }

    private void init(){
        LayoutInflater mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.custom_dialog,null);
        msgText = (TextView) mView.findViewById(R.id.dialog_msg);
        invest_hint_dialog = (TextView) mView.findViewById(R.id.invest_hint_dialog);
        confirmBtn = (TextView) mView.findViewById(R.id.dialog_confirm);
        investHelpLayout = (LinearLayout) mView.findViewById(R.id.ll_investstart_hint);
    }


}
