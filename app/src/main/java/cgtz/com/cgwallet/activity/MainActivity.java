package cgtz.com.cgwallet.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.view.BidirSlidingLayout;
import cgtz.com.cgwallet.view.CustormLayout;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements ISplashView{
    private BidirSlidingLayout bidirSldingLayout;
    private LinearLayout conter_menu_layout;
    private ImageView showLeftButton;
    private ImageView showRightButton;
    private ListView contentList;
    private ArrayAdapter<String> contentListAdapter;
    /**
     * 用于填充contentListAdapter的数据源。
     */
    private String[] contentItems = { "Content Item 1", "Content Item 2", "Content Item 3",
            "Content Item 4", "Content Item 5", "Content Item 6", "Content Item 7",
            "Content Item 8", "Content Item 9", "Content Item 10", "Content Item 11",
            "Content Item 12", "Content Item 13", "Content Item 14", "Content Item 15",
            "Content Item 16" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        contentListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                contentItems);
        contentList.setAdapter(contentListAdapter);
        bidirSldingLayout.setScrollEvent(contentList);
        showLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidirSldingLayout.isLeftLayoutVisible()) {
                    bidirSldingLayout.scrollToContentFromLeftMenu();
                } else {
                    bidirSldingLayout.initShowLeftState();
                    bidirSldingLayout.scrollToLeftMenu();
                }
            }
        });
        showRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidirSldingLayout.isRightLayoutVisible()) {
                    bidirSldingLayout.scrollToContentFromRightMenu();
                } else {
                    bidirSldingLayout.initShowRightState();
                    bidirSldingLayout.scrollToRightMenu();
                }
            }
        });
    }

    private void initViews(){
        bidirSldingLayout = (BidirSlidingLayout) findViewById(R.id.custom_sliding_layout);
        conter_menu_layout = (LinearLayout) findViewById(R.id.conter_menu_layout);
        showLeftButton = (ImageView) findViewById(R.id.show_left_button);
        showRightButton = (ImageView) findViewById(R.id.show_right_button);
        contentList = (ListView) findViewById(R.id.contentList);
    }

    @Override
    public void startProcessBar() {

    }

    @Override
    public void hideProcessBar() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void startNextActivity() {

    }
}
