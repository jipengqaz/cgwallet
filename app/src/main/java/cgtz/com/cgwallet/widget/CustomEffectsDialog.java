package cgtz.com.cgwallet.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.R;

/**
 * 自定义Dialog
 * Created by Administrator on 2014/11/18.
 */
public class CustomEffectsDialog extends Dialog implements DialogInterface {
    private View mDialogView;
    private RelativeLayout mRelativeLayoutView;
    private LinearLayout mLinearLayoutPanelView;
    private LinearLayout mLinearLayoutTopView;
    private LinearLayout mLinearLayoutMsgView;
    private FrameLayout mFrameLayoutCustomView;
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mMsgView;
    private TextView mButton1;
    private TextView mButton2;
    private View btn_line;
    private View btn_content_line;
    private long mDuration = 1*700;
    private static CustomEffectsDialog instans;

    public CustomEffectsDialog(Context context) {
        super(context);
        init(context);
    }
    public CustomEffectsDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static CustomEffectsDialog getInstans(Context context){
        instans = new CustomEffectsDialog(context, R.style.dialog_untran);
        return instans;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    private void init(Context context){
        mDialogView = View.inflate(context, R.layout.dialog_layout, null);
        mRelativeLayoutView = (RelativeLayout) mDialogView.findViewById(R.id.main);
        mLinearLayoutPanelView = (LinearLayout) mDialogView.findViewById(R.id.parentPanel);
        mLinearLayoutTopView = (LinearLayout) mDialogView.findViewById(R.id.topPanel);
        mLinearLayoutMsgView = (LinearLayout) mDialogView.findViewById(R.id.contentPanel);
        mFrameLayoutCustomView = (FrameLayout) mDialogView.findViewById(R.id.customPanel);

        mIconView = (ImageView) mDialogView.findViewById(R.id.icon);
        mTitleView = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMsgView = (TextView) mDialogView.findViewById(R.id.message);
        mButton1 = (TextView) mDialogView.findViewById(R.id.button1);
        mButton2 = (TextView) mDialogView.findViewById(R.id.button2);
        btn_line = mDialogView.findViewById(R.id.btn_line);
        btn_content_line = mDialogView.findViewById(R.id.btn_centet_line);
        setContentView(mDialogView);
        setCancelable(false);
    }

    public void windowEffect(){
        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
    }

    public void withLayoutPanelBg(int resid){
        mLinearLayoutPanelView.setBackgroundResource(resid);
    }

    public void withTitle(CharSequence title){
        toggleView(mLinearLayoutTopView,title);
        mTitleView.setText(title);
    }

    public void withTitleSize(float demin){
        mTitleView.setTextSize(demin);
    }

    public void withTitleColor(String colorString){
        mTitleView.setTextColor(Color.parseColor(colorString));
    }

    public void withTitleColor(int color){
        mTitleView.setTextColor(color);
    }

    public void withIcon(Drawable drawable){
        mIconView.setImageDrawable(drawable);
    }

    public void withIcon(int drawableResId){
        mIconView.setImageResource(drawableResId);
    }


    public void withMessage(CharSequence msg){
        toggleView(mLinearLayoutMsgView,msg);
    }

    public void withMessageSize(float dimen){
        mMsgView.setTextSize(dimen);
    }

    public void withMessageColor(String colorString){
        mMsgView.setTextColor(Color.parseColor(colorString));
    }

    public void withMessageColor(int color){
        mMsgView.setTextColor(color);
    }

    public void withBtnLineColor(int resid){
        btn_line.setVisibility(View.VISIBLE);
        btn_line.setBackgroundResource(resid);
    }

    public void withBtnContentLineColor(int resid){
        btn_content_line.setVisibility(View.VISIBLE);
        btn_content_line.setBackgroundResource(resid);
    }

    public void withButtonDrawable(int resid){
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
    }

    public void withButton1Text(CharSequence text){
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);
    }

    public void withButton1TextColor(int resid){
        mButton1.setTextColor(resid);
    }

    public void withButton2Text(CharSequence text){
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
    }

    public void withButton2TextColor(int resid){
        mButton2.setTextColor(resid);
    }

    public void withButton1Click(View.OnClickListener click){
        mButton1.setOnClickListener(click);
    }

    public void withButton2Click(View.OnClickListener click){
        mButton2.setOnClickListener(click);
    }

    public void setCustomView(int resId,Context context){
        View customView = View.inflate(context, resId, null);
        if(mFrameLayoutCustomView.getChildCount() > 1){
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
    }

    public void setCustomView(View view,Context context){
        if(mFrameLayoutCustomView.getChildCount() > 1){
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);
    }

    public void setDuration(long duration){
        mDuration = duration;
    }

    public void dismiss(){
        super.dismiss();
        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
    }

    public void toggleView(View view,Object obj){
        if(obj == null){
            view.setVisibility(View.GONE);
        }else{
            view.setVisibility(View.VISIBLE);
            mMsgView.setText(obj.toString());
        }
    }

    public void setEffectBox(AnimationSet set){
        mDialogView.startAnimation(set);
    }

    public void withBackground(int resid){
        mLinearLayoutPanelView.setBackgroundResource(resid);
    }

    public TextView getmButton1() {
        return mButton1;
    }

    public TextView getmMsgView() {
        return mMsgView;
    }

    public View getmDialogView() {
        return mDialogView;
    }

    public RelativeLayout getmRelativeLayoutView() {
        return mRelativeLayoutView;
    }

    public LinearLayout getmLinearLayoutPanelView() {
        return mLinearLayoutPanelView;
    }

    public LinearLayout getmLinearLayoutTopView() {
        return mLinearLayoutTopView;
    }

    public LinearLayout getmLinearLayoutMsgView() {
        return mLinearLayoutMsgView;
    }

    public FrameLayout getmFrameLayoutCustomView() {
        return mFrameLayoutCustomView;
    }

    public ImageView getmIconView() {
        return mIconView;
    }

    public TextView getmTitleView() {
        return mTitleView;
    }

    public TextView getmButton2() {
        return mButton2;
    }

    public View getBtn_line() {
        return btn_line;
    }

    public View getBtn_content_line() {
        return btn_content_line;
    }

    public long getmDuration() {
        return mDuration;
    }
}
