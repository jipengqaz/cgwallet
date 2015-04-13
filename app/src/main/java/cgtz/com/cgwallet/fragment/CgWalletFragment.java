package cgtz.com.cgwallet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cgtz.com.cgwallet.R;

/**
 * 草根钱包
 * Created by Administrator on 2015/4/11.
 */
public class CgWalletFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.layout_cg_wallet,container,false);

        return layoutView;
    }
}
