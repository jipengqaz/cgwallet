package cgtz.com.cgwallet.bean;

/**
 * 绑定银行卡工具类
 * Created by hehe on 14/9/25.
 */
public class Bank {
    int id;//银行id
    String name;//显示的选项
    int needBranch;//判断银行是否需要开户行详情
    String value;//开户行全称
    String priority;//支付通道
    String bindFee;//绑定银行卡扣的钱
    double pay_limit;//银行单笔限额

    public double getPay_limit() {
        return pay_limit;
    }

    public void setPay_limit(double pay_limit) {
        this.pay_limit = pay_limit;
    }

    public String getBindFee() {
        return bindFee;
    }

    public void setBindFee(String bindFee) {
        this.bindFee = bindFee;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getNeedBranch() {
        return needBranch;
    }

    public void setNeedBranch(int needBranch) {
        this.needBranch = needBranch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
