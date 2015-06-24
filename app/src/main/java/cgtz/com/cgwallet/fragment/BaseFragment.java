package cgtz.com.cgwallet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.networkbench.agent.impl.NBSAppAgent;

/**
 * Created by Administrator on 2015/4/11.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NBSAppAgent.setLicenseKey("0856e9d72bbd4420ba1d0ff66de71df6").withLocationServiceEnabled(true).start(getActivity());
    }
}
