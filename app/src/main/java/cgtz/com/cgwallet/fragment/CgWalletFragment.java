package cgtz.com.cgwallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.E_wallet_record_activity;

/**
 * 草根钱包
 * Created by Administrator on 2015/4/11.
 */
public class CgWalletFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.layout_cg_wallet, container, false);
        LinearLayout cun_qian = (LinearLayout) layoutView.findViewById(R.id.cun_qian);
        cun_qian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), E_wallet_record_activity.class));
            }
        });
        return layoutView;
    }
}
