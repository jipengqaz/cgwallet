package cgtz.com.cgwallet.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.utility.Constants;

/**
 * ���������ݱ������
 * Created by Administrator on 2015/4/11.
 */
public class JsonBean {
    private int code;//���ݴ����ж�ֵ
    private String error_msg;//������Ϣ
    private String jsonString;//����json��ʽ��������

    public JsonBean(){}
    public JsonBean(String str){
        if(TextUtils.isEmpty(str)){
            code = Constants.NO_DATA;//����Ϊ�ջ�null����ʾ������û�з�������
            error_msg = Constants.NO_DATA_MSG;
        }else {
            if (str.equals("event")) {
                code = Constants.IS_EVENT;//���������ʳ�ʱ������http�����쳣
                error_msg = Constants.IS_EVENT_MSG;
            } else {
                //���������ݷ�������
                try {
                    JSONObject obj = new JSONObject(str);
                    String action = obj.optString("action");
                    int state = obj.optInt("success");
                    if (action.equals("login")) {
                        //����token���ڣ���Ҫ���µ�¼
                        code = Constants.NEED_LOGIN_AGAIN;
                        error_msg = obj.optString("msg");
                    } else if (action.equals("maintain")) {
                        //������ά����
                        code = Constants.SERVICE_MAINTAIN;
                        error_msg = obj.optString("msg");
                    } else {
                        //���ݷ�������
                        if (state == 0) {
                            //����ʧ��
                            code = Constants.OPERATION_FAIL;
                            error_msg = obj.optString("msg");
                            jsonString = str;
                        } else {
                            //�����ɹ�
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
