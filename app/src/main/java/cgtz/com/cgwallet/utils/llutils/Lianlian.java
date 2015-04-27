package cgtz.com.cgwallet.utils.llutils;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.utils.LogUtils;

/**
 * 连连支付的工具类
 * Created by Administrator on 2014/11/19.
 */
public class Lianlian {
    /**
     * 卡前置支付
     * @param flag 判断该卡是否第一次使用
     * @param no_order 订单编号
     * @param dt_order 订单产生时间
     * @param notifyUrl 回调通知地址
     * @param user_id 用户id
     * @param identity 用户身份证号
     * @param userName 用户姓名
     * @param payMoney 支付金额
     * @param bankCard 银行卡号
     * @param MD5_KEY MD5签名
     * @param no_agree 银行卡与连连支付的协议号
     * @return
     */
    public static PayOrder constructPreCardPayOrder(boolean flag,String no_order,String dt_order
            ,String notifyUrl,String user_id,String identity
            ,String userName,String payMoney,String bankCard,String MD5_KEY,String no_agree) {
        PayOrder order = new PayOrder();
        order.setBusi_partner("101001");
        order.setNo_order(no_order);
        order.setDt_order(dt_order);
        order.setName_goods("草根投资充值");
        order.setNotify_url(notifyUrl);
        // MD5 签名方式
        order.setSign_type(PayOrder.SIGN_TYPE_MD5);
//        order.setValid_order("");

        order.setUser_id(user_id);
        order.setId_no(identity);
        order.setAcct_name(userName);
        order.setMoney_order(payMoney);
//        order.setFlag_modify("1");
        // TODO 风险控制参数，没必要传入的，请不要设置这个条目，
//        order.setRisk_item(constructRiskItem());

        if(flag){
            // 银行卡卡号，该卡首次支付时必填
            order.setCard_no(bankCard);//
        }else{
            //银行卡历次支付时填写，可以查询得到，协议号匹配会进入SDK，
            order.setNo_agree(no_agree);
        }
        order.setOid_partner(EnvConstants.PARTNER);//商户号

        String sign = "";
        String content = BaseHelper.sortParam(order);
        LogUtils.i("LianLian", "content: " + content);
        // MD5 签名方式
//        sign = Md5Algorithm.getInstance().sign(content,
//                EnvConstants.MD5_KEY);
//        DebugUtils.i("InvestPayOfBankCardActivity","sign: "+sign);
//        order.setSign(sign);
        order.setSign(MD5_KEY);
        return order;
    }

    /**
     * 标准支付
     * @param no_order
     * @param dt_order
     * @param notifyUrl
     * @param user_id
     * @param identity
     * @param userName
     * @param payMoney
     * @param MD5_KEY
     * @return
     */
    private PayOrder constructGesturePayOrder(String no_order,String dt_order
            ,String notifyUrl,String user_id,String identity
            ,String userName,String payMoney,String MD5_KEY) {
        PayOrder order = new PayOrder();
        // TODO busi_partner 是指商户的业务类型，"101001"为虚拟商品销售，详情请参考接口说明书
        order.setBusi_partner("101001");
        // TODO 商户订单，Demo采用时间戳模拟订单号
        order.setNo_order(no_order);
        order.setDt_order(dt_order);
        order.setName_goods("草根投资充值");
        order.setNotify_url(notifyUrl);
        // MD5 签名方式
        order.setSign_type(PayOrder.SIGN_TYPE_MD5);
//        order.setValid_order("100");

        order.setUser_id(user_id + "");
        order.setId_no(identity);
        order.setAcct_name(userName);
        order.setMoney_order(payMoney);

        order.setFlag_modify("1");
        // TODO 风险控制参数，没必要传入的，请不要设置这个条目，
//        order.setRisk_item(constructRiskItem());
        String sign = "";
        // TODO 商户号
        order.setOid_partner(EnvConstants.PARTNER);
        // TODO 对签名原串进行排序，并剔除不需要签名的串。
        String content = BaseHelper.sortParam(order);
        LogUtils.i("content",content);
        // MD5 签名方式, 签名方式包括两种，一种是MD5，一种是RSA 这个在商户站管理里有对验签方式和签名Key的配置。
        sign = Md5Algorithm.getInstance().sign(content, EnvConstants.MD5_KEY);
        LogUtils.i("sign","sign"+sign);
//        order.setSign(sign);
        order.setSign(MD5_KEY);
        return order;
    }

    /**
     * TODO 风险控制参数生成例子，请根据文档动态填写。最后返回时必须调用.toString()
     */
    private String constructRiskItem() {
        JSONObject mRiskItem = new JSONObject();
        try {
            mRiskItem.put("user_info_bind_phone", "13958069593");
            mRiskItem.put("user_info_dt_register", "201407251110120");
            mRiskItem.put("frms_ware_category", "4.0");
            mRiskItem.put("request_imei", "1122111221");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mRiskItem.toString();
    }
}
