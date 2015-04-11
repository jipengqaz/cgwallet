package cgtz.com.cgwallet.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.utility.Constants;

/**
 * 解析的数据保存对象
 * Created by Administrator on 2015/4/11.
 */
public class JsonBean {
    private int code;//数据错误判断值
    private String error_msg;//错误信息
    private String jsonString;//正常json格式数据内容

    public JsonBean(){}
    public JsonBean(String str){
        if(TextUtils.isEmpty(str)){
            code = Constants.NO_DATA;//数据为空或null，表示服务器没有返回数据
            error_msg = Constants.NO_DATA_MSG;
        }else {
            if (str.equals("event")) {
                code = Constants.IS_EVENT;//服务器访问超时，或者http访问异常
                error_msg = Constants.IS_EVENT_MSG;
            } else {
                //服务器数据返回正常
                try {
                    JSONObject obj = new JSONObject(str);
                    String action = obj.optString("action");
                    int state = obj.optInt("success");
                    if (action.equals("login")) {
                        //本地token过期，需要重新登录
                        code = Constants.NEED_LOGIN_AGAIN;
                        error_msg = obj.optString("msg");
                    } else if (action.equals("maintain")) {
                        //服务器维护中
                        code = Constants.SERVICE_MAINTAIN;
                        error_msg = obj.optString("msg");
                    } else {
                        //数据返回正常
                        if (state == 0) {
                            //操作失败
                            code = Constants.OPERATION_FAIL;
                            error_msg = obj.optString("msg");
                            jsonString = str;
                        } else {
                            //操作成功
                            code = Constants.OPERATION_SUCCESS;
                            error_msg = obj.optString("msg");
                            jsonString = str;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    code = Constants.DATA_EVENT;
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
