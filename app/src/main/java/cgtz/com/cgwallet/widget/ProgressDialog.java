package cgtz.com.cgwallet.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.R;


/**
 * TODO: document your custom view class.
 */
public class ProgressDialog extends Dialog {
    private Context context;
    private TextView msgText;
    private View mView;
    private String msg;

    public ProgressDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public ProgressDialog(Context context, int cancelable) {
        super(context, cancelable);
        this.context = context;
        init();
    }
    public ProgressDialog(Context context, int cancelable, String msg) {
        super(context, cancelable);
        this.context = context;
        this.msg = msg;
        init();
    }

    public void setMessage(String msg){
        msgText.setText(msg);
    }

    public TextView getMessageView(){
        return msgText;
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
        mView = mInflater.inflate(R.layout.layout_progress_dialog,null);
        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.loading_dialog);
        ImageView imageView = (ImageView) mView.findViewById(R.id.loading_imageview);
        msgText = (TextView) mView.findViewById(R.id.loading_textview);
        //���ض���
        Animation jumpAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
        //ʹ��imageview��ʾ����
        imageView.setAnimation(jumpAnimation);
    }
}
