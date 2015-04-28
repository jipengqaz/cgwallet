package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;

/**
 * 关于我们
 */
public class AboutCompanyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_company);
        MApplication.registActivities(this);//存储该activity
        setTitle("关于我们");
        showBack(true);
        TextView company_profile= (TextView) findViewById(R.id.company_profile);
        company_profile.setText(Html.fromHtml("<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;浙江草根网络科技有限公司成立于2013年，注册资本金2000万元人民币。</p>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;草根投资网（<a href='https://www.cgtz.com'>www.cgtz.com</a>）由浙江草根网络科技有限公司开发和运营。</p>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;草根投资在创立时即将自身定位于一家以输出风险管理为核心，以实现投融资双方共赢的互联网金融中介服务企业。</p>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;草根投资的经营管理团队均为金融、法律、互联网领域的资深专业人士，在相关领域有丰富的从业经验，" +
                        "他们通过专业的精密法律结构设计，努力实现中国本土金融思维、理念与互联网的有机结合，" +
                        "并始终致力于将草根投资网打造成一个安全、透明、诚信、高效的让广大草根阶层能够轻松实现投资理财的互联网债权投融资平台。</p>" +
                        "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;草根投资的全体员工将永远为实现广大草根人们“放心投资，轻松理财”这一终极目标而不懈努力。</p>")
        );
        Button www_cgtz_com = (Button) findViewById(R.id.www_cgtz_com);
        www_cgtz_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.cgtz.com");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
