package cgtz.com.cgwallet.paymoney;

/**
 * Created by penny_yao on 2015/12/31.
 */
public class User {
    String phoneNum;
    String name;
    String rawContactId;

    public String getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(String rawContactId) {
        this.rawContactId = rawContactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
