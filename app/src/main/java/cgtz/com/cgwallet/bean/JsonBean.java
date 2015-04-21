package cgtz.com.cgwallet.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.utility.Constants;

/**
 * 解析数据对象
 * Created by Administrator on 2015/4/11.
 */
public class JsonBean {
    private int code;//数据类型判断值ֵ
    private String error_msg;//错误信息
    private String jsonString;//数据json内容

    public JsonBean(){}
    public JsonBean(String str){
        if(TextUtils.isEmpty(str)){
            code = Constants.NO_DATA;//网络不稳定或者服务器没有返回数据
            error_msg = Constants.NO_DATA_MSG;
        }else {
            if (str.equals("event")) {
                code = Constants.IS_EVENT;//访问服务器超时
                error_msg = Constants.IS_EVENT_MSG;
            } else {
                //正常获取到服务器返回的内容
                try {
                    JSONObject obj = new JSONObject(str);
                    String action = obj.optString("action");
                    int state = obj.optInt("success");
                    if (action.equals("login")) {
                        //本地token过期，需重新登录
                        code = Constants.NEED_LOGIN_AGAIN;
                        error_msg = obj.optString("msg");
                    } else if (action.equals("maintain")) {
                        //服务器维护中
                        code = Constants.SERVICE_MAINTAIN;
                        error_msg = obj.optString("msg");
                    } else {
                        //对访问服务器操作进行失败与否判断
                        if (state == 0) {
                            //访问服务器操作失败
                            code = Constants.OPERATION_FAIL;
                            error_msg = obj.optString("msg");
                            jsonString = str;
                        } else {
                            //访问服务器操作成功
                            code = Constants.OPERATION_SUCCESS;
                            error_msg = obj.optString("msg");
                            jsonString = str;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    code = Constants.DATA_EVENT;//数据解析出错
                }
            }
        }
    }

    public int getCode() {
        return code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public String getJsonString() {
        return jsonString;
    }
}
