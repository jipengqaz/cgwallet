package cgtz.com.cgwallet.bean;

import org.litepal.crud.DataSupport;

/**
 * 本机登录过的手机号
 * Created by ryan on 15/6/18.
 */
public class LoginMobileBean extends DataSupport{
    private int id;
    private String userId;
    private String mobile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
