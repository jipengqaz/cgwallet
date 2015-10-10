package cgtz.com.cgwallet.bean;

import org.json.JSONObject;

import java.io.Serializable;

import cgtz.com.cgwallet.R;


/**
 * 银行卡信息
 * Created by hehe on 14/9/25.
 */
public class BankCard implements Serializable {
    private String cardLast;//银行卡后四位
    private String fullCardNumber;//银行卡卡号
    private String bankName;//发卡银行
    private String cardHolder;//开户姓名
    private String card_id;//银行卡id
    private String bankId;//银行id，用于显示不同银行图标
    private String userId;
    private String bankProvince;
    private String bankCity;
    private String bankDistrict;
    private String branchName;
    private String subBranchName;
    private String status;
    private String bindTime;
    private String cardNumber;
    private int llMark;//连连标识
    private int umpMark;//联动标识
    private Long freeOutAmount;//免手续费转出金额

    public BankCard(JSONObject json){
        cardNumber = json.optString("cardNumber");
        userId = json.optString("userId");
        bankProvince = json.optString("bankProvince");
        bankCity = json.optString("bankCity");
        bankDistrict = json.optString("bankDistrict");
        subBranchName = json.optString("subBranchName");
        status = json.optString("status");
        bindTime = json.optString("bindTime");
        branchName = json.optString("branchName");
        freeOutAmount = json.optLong("freeamount");

        cardLast = json.optString("cardLast");
        fullCardNumber = json.optString("fullCardNumber");
        bankName = json.optString("bankName");
        cardHolder = json.optString("cardHolder");
        card_id = json.optString("card_id");
        bankId = json.optString("bankId");
        llMark = json.optInt("llMark");//连连标识
        umpMark = json.optInt("umpMark");//联动标识
    }

    /**
     * 根据不同银行id获取对应的图标
     * @param bankid
     * @return
     */
    public static int getBankIcon(int bankid){
        switch (bankid){
            case 1:
                return R.drawable.bank_1;
            case 2:
                return R.drawable.bank_2;
            case 3:
                return R.drawable.bank_3;
            case 4:
                return R.drawable.bank_4;
            case 5:
                return R.drawable.bank_5;
            case 6:
                return R.drawable.bank_6;
            case 7:
                return R.drawable.bank_7;
            case 8:
                return R.drawable.bank_8;
            case 9:
                return R.drawable.bank_9;
            case 10:
                return R.drawable.bank_10;
            case 11:
                return R.drawable.bank_11;
            case 12:
                return R.drawable.bank_12;
            case 13:
                return R.drawable.bank_13;
            case 14:
                return R.drawable.bank_14;
            case 15:
                return R.drawable.bank_15;
//            case 16:
//                return R.drawable.bank_16;
            case 17:
                return R.drawable.bank_17;
//            case 18:
//                return R.drawable.bank_18;
            case 19:
                return R.drawable.bank_19;
//            case 20:
//                return R.drawable.bank_20;
//            case 21:
//                return R.drawable.bank_21;
            case 22:
                return R.drawable.bank_22;
            case 23:
                return R.drawable.bank_23;
//            case 24:
//                return R.drawable.bank_24;
            case 25:
                return R.drawable.bank_25;
//            case 26:
//                return R.drawable.bank_26;
//            case 27:
//                return R.drawable.bank_27;
            case 28:
                return R.drawable.bank_28;
//            case 29:
//                return R.drawable.bank_29;
            case 30:
                return R.drawable.bank_30;
//            case 31:
//                return R.drawable.bank_31;
//            case 32:
//                return R.drawable.bank_32;
            case 33:
                return R.drawable.bank_33;
        }
        return 0;
    }

    public Long getFreeOutAmount() {
        return freeOutAmount;
    }

    public int getLlMark() {
        return llMark;
    }

    public void setLlMark(int llMark) {
        this.llMark = llMark;
    }

    public int getUmpMark() {
        return umpMark;
    }

    public void setUmpMark(int umpMark) {
        this.umpMark = umpMark;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public String getBankDistrict() {
        return bankDistrict;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getSubBranchName() {
        return subBranchName;
    }

    public String getStatus() {
        return status;
    }

    public String getBindTime() {
        return bindTime;
    }

    public String getCardLast() {
        return cardLast;
    }

    public String getFullCardNumber() {
        return fullCardNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getCard_id() {
        return card_id;
    }

    public String getBankId() {
        return bankId;
    }
}
