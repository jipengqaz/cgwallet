package cgtz.com.cgwallet.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import cgtz.com.cgwallet.R;

/**
 * 服务器维护中，提示dialog
 * Created by Administrator on 2015/1/19.
 */
public class ServerMainTainDialog extends Dialog {
    private static ServerMainTainDialog instans;
    private ImageView maintain_close;
    public ServerMainTainDialog(Context context) {
        super(context);
        initView(context);
    }
    public ServerMainTainDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    public static ServerMainTainDialog getInstans(Context context){
        instans = new ServerMainTainDialog(context, R.style.dialog_untran);
        return instans;
    }

    private void initView(Context context){
        View maintainView = LayoutInflater.from(context)
                .inflate(R.layout.layout_server_maintain, null);
        maintain_close = (ImageView) maintainView.findViewById(R.id.maintain_close);
        setContentView(maintainView);
        setCancelable(false);
    }

    public void withMaintainIconClick(View.OnClickListener listener){
        maintain_close.setOnClickListener(listener);
    }
}
