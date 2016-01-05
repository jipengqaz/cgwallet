package cgtz.com.cgwallet.activity;

/**
 * Created by chen on 2015-12-15.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.SlidingMenu;

/**
 * 自定义popupWindow
 *
 * @author wwj
 *
 *
 */
public class AddPopWindow extends PopupWindow {
    private View conentView;
    private final int w;
    public final LinearLayout llbill;//账单
    public final LinearLayout llshare;//分享

    public AddPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.add_popupwindow, null);
//        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 3);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        //账单
        llbill = (LinearLayout) conentView
                .findViewById(R.id.ll_bill);
        //分享
        llshare = (LinearLayout) conentView
                .findViewById(R.id.ll_share);
/*//弹出账单界面
        llbill.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.makeToast_short(context, "敬请期待");
                AddPopWindow.this.dismiss();
            }
        });
//弹出分享界面 分享界面目前在主页
        llshare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddPopWindow.this.dismiss();
            }
        });*/
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示PopupWindow   并且设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量  原：parent.getLayoutParams().width / 2
          this.showAsDropDown(parent, w, -5);
        } else {
            this.dismiss();
        }
    }
}



//    private void mainPopupWindow(){
//       /* LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View conentView = inflater.inflate(R.layout.add_popupwindow, null);*/
//        RelativeLayout popLayout=new RelativeLayout(this);
//        setContentView(R.layout.add_popupwindow);
//
//        final PopupWindow pop=new PopupWindow(popLayout,100,100);
//        //int h = context.getWindowManager().getDefaultDisplay().getHeight();
//        int w = getWindowManager().getDefaultDisplay().getWidth();
//        // 设置SelectPicPopupWindow的View
//        //this.setContentView(conentView);
//        /*// 设置SelectPicPopupWindow弹出窗体的宽
//        pop.setWidth(w / 3);
//        // 设置SelectPicPopupWindow弹出窗体的高
//        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);*/
//        // 设置SelectPicPopupWindow弹出窗体可点击
//        pop.setFocusable(true);
//        pop.setOutsideTouchable(true);
//        // 刷新状态
//        pop.update();
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0000000000);
//        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        pop.setBackgroundDrawable(dw);
//        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
//        // 设置SelectPicPopupWindow弹出窗体动画效果
//        pop.setAnimationStyle(R.style.AnimationPreview);
//        //账单
//        LinearLayout llbill = (LinearLayout) findViewById(R.id.ll_bill);
//        //分享
//        LinearLayout llshare = (LinearLayout) findViewById(R.id.ll_share);
//
//        if (!pop.isShowing()) {
//            // 以下拉方式显示PopupWindow   并且设置显示PopupWindow的位置位于View的左下方，x,y表示坐标偏移量  原：parent.getLayoutParams().width / 2   w - 2, -5
//            pop.showAsDropDown(top_title, 0, -5);
//        } else {
//            pop.dismiss();
//        }
//        //弹出账单界面
//        llbill.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                Utils.makeToast_short(MainActivity.this,"敬请期待");
//                pop.dismiss();
//            }
//        });
//        //弹出分享界面 分享界面目前在主页
//        llshare.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mMenu.rightToggle();
//                pop.dismiss();
//            }
//        });
//    }

