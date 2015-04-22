package cgtz.com.cgwallet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.LogUtils;

/**
 * 我的钱包
 * Created by Administrator on 2015/4/11.
 */
public class MyWalletFragment extends BaseFragment {
    private static final String TAG = "MyWalletFragment";
    private LinearLayout layoutAuther;
    private LinearLayout layoutBank;
    private LinearLayout layoutDraw;
    private LinearLayout layoutSave;
    private LinearLayout layoutDrawRecord;
    private LinearLayout layoutSaveRecord;
    private int screenWidth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        View layoutView = inflater.inflate(R.layout.layout_my_wallet,container,false);
        initViews(layoutView);
        setWidgetAttrs();
        setListener();
        return layoutView;
    }

    private void initViews(View view){
        layoutAuther = (LinearLayout) view.findViewById(R.id.layout_auther);
        layoutBank = (LinearLayout) view.findViewById(R.id.layout_bank);
        layoutDraw = (LinearLayout) view.findViewById(R.id.layout_draw);
        layoutSave = (LinearLayout) view.findViewById(R.id.layout_save);
        layoutDrawRecord = (LinearLayout) view.findViewById(R.id.layout_draw_record);
        layoutSaveRecord = (LinearLayout) view.findViewById(R.id.layout_save_record);
    }

    private void setWidgetAttrs(){
        LinearLayout.LayoutParams autherParams = (LinearLayout.LayoutParams) layoutAuther.getLayoutParams();
        autherParams.width = screenWidth/2;
        layoutAuther.setLayoutParams(autherParams);
        LinearLayout.LayoutParams bankParams = (LinearLayout.LayoutParams) layoutBank.getLayoutParams();
        bankParams.width = screenWidth/2;
        layoutBank.setLayoutParams(bankParams);
        LinearLayout.LayoutParams drawParams = (LinearLayout.LayoutParams) layoutDraw.getLayoutParams();
        drawParams.width = screenWidth/2;
        layoutDraw.setLayoutParams(drawParams);
        LinearLayout.LayoutParams drawRecordParams = (LinearLayout.LayoutParams) layoutDrawRecord.getLayoutParams();
        drawRecordParams.width = screenWidth/2;
        layoutDrawRecord.setLayoutParams(drawRecordParams);
        LinearLayout.LayoutParams saveParams = (LinearLayout.LayoutParams) layoutSave.getLayoutParams();
        saveParams.width = screenWidth/2;
        layoutSave.setLayoutParams(saveParams);
        LinearLayout.LayoutParams saveRecordParams = (LinearLayout.LayoutParams) layoutSaveRecord.getLayoutParams();
        saveRecordParams.width = screenWidth/2;
        layoutSaveRecord.setLayoutParams(saveRecordParams);
    }

    private void setListener(){
    }
}
