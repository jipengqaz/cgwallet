package cgtz.com.cgwallet.data;

import org.json.JSONObject;

/**
 * 草根钱包  流水记录
 * Created by Administrator on 2015-3-16.
 */
public class E_records {
    private Double amount;//金额
    private String operation;//转入转出方式
    private String time;//时间
    private String progress;//1:转出中，0：不是转出中

    private String eTotal;//本金加代收利息之和
    private String eBalance;//本金
    private String receivableInterest;//代收利息

    private int code;//用于判断item布局的

    public E_records(JSONObject json, int code){
        amount = json.optDouble("amount");
        operation = json.optString("operation");
        time = json.optString("time");
        progress = json.optString("progress");


        eTotal = json.optString("eTotal");
        eBalance = json.optString("eBalance");
        receivableInterest = json.optString("receivableInterest");

        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String geteTotal() {
        return eTotal;
    }

    public String geteBalance() {
        return eBalance;
    }

    public String getReceivableInterest() {
        return receivableInterest;
    }

    public Double getAmount() {
        return amount;
    }

    public String getOperation() {
        return operation;
    }

    public String getTime() {
        return time;
    }

    public String getProgress() {
        return progress;
    }
}
