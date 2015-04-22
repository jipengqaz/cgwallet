package cgtz.com.cgwallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.Earnings_record;

/**
 * �ݸ�草根钱包
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
                startActivity(new Intent(getActivity(), Earnings_record.class));
            }
        });
//        Button aaaa= (Button) layoutView.findViewById(R.id.aaaaaaaaa);//设置手势密码
//        Button bbbb = (Button) layoutView.findViewById(R.id.bbbbbbbb);//校验手势密码
//        View.OnClickListener click   = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                switch (v.getId()){
//                    case R.id.aaaaaaaaa:
//                        intent.setClass(getActivity(),GestureEditActivity.class);
//                        startActivity(intent);
//                    break;
//                    case R.id.bbbbbbbb:
//                        if(Utils.getLockPassword(getActivity(),"123456")!=""){
//                            intent.setClass(getActivity(),GestureVerifyActivity.class);
//                            startActivity(intent);
//                        }else{
//                            Toast.makeText(getActivity(),"未设置手势密码",Toast.LENGTH_SHORT);
//                        }
//                        break;
//            }
//            }
//        };
//        aaaa.setOnClickListener(click);
//        bbbb.setOnClickListener(click);
        return layoutView;
    }
}
