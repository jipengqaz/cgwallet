package cgtz.com.cgwallet.utils;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cgtz.com.cgwallet.utility.Constants;

/**
 * Http工具类
 * Created by Administrator on 2014/9/19.
 */
public class HttpUtils {
    private static int timeout = 30*1000;//连接超时时间
    private static int getTimeOut = 3 * 1000;//get请求超时时间
    /**
     * 通过http post 提交数据
     * @param url  访问路径
     * @param content 内容
     * @param encoding  返回内容字符编码
     * @return String
     */
    public static String HttpPost(String url,String content,String encoding){
        LogUtils.e("HttpUtils", "content: "+content);
        HttpURLConnection conn = null;
        String str="";
        try{
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);// 打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);// 打开输出流，以便向服务器提交数据
            conn.setConnectTimeout(timeout); // 设置连接超时时间
            conn.setReadTimeout(timeout); //设置返回超时时间,下面要对超时进行处理
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);// 使用Post方式不能使用缓存
            conn.setInstanceFollowRedirects(true);
            //conn.setRequestProperty("Cookie", SessionId);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close(); // flush and close
            int response = conn.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null){
                    line = new String(line.getBytes(),"UTF-8");
                    sb.append(line);
                }
                str = sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            //里面会抛连接和返回超时java.net.SocketTimeoutException，还有IO异常
            return "event";
        }finally{
            conn.disconnect();
            conn = null;
        }
        return str;
    }

    /**
     * GET请求方式
     * @param url
     * @return
     */
    public static String HttpGet(String url,String encoding){
        LogUtils.e("HttpUtils", "encoding: "+encoding);
        HttpURLConnection conn = null;
        String str="";
        try{
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);// 打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);// 打开输出流，以便向服务器提交数据
            conn.setConnectTimeout(getTimeOut); // 设置连接超时时间
            conn.setReadTimeout(getTimeOut); //设置返回超时时间,下面要对超时进行处理
            conn.setRequestMethod("GET");
            conn.connect();
            int response = conn.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null){
                    line = new String(line.getBytes(),"UTF-8");
                    sb.append(line);
                }
                str = sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            //里面会抛连接和返回超时java.net.SocketTimeoutException，还有IO异常
            return "event";
        }finally{
            if(conn != null){
                conn.disconnect();
            }
        }
        return str;
    }
    /**
     * 通过https post 提交数据
     * @param url  访问路径
     * @param content 内容
     * @param encoding  返回内容字符编码
     * @return
     */
    public static String HttpsPost(String url, String content, String encoding) {
        LogUtils.e("HttpUtils","content: "+content);
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            MyTrustManager mtm = new MyTrustManager();
            sc.init(null, new TrustManager[]{mtm}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "event";
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return "event";
        }
        HttpsURLConnection conn = null;
        String str="";
        try{
            conn = (HttpsURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);// 打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);// 打开输出流，以便向服务器提交数据
            conn.setConnectTimeout(timeout); // 设置连接超时时间
            conn.setReadTimeout(timeout); //设置返回超时时间,下面要对超时进行处理
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);// 使用Post方式不能使用缓存
            conn.setInstanceFollowRedirects(true);
            //conn.setRequestProperty("Cookie", SessionId);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close(); // flush and close
            int response = conn.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null){
                    line = new String(line.getBytes(),"UTF-8");
                    sb.append(line);
                }
                str = sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            //里面会抛连接和返回超时java.net.SocketTimeoutException，还有IO异常
            return "event";
        }finally{
            conn.disconnect();
            conn = null;
        }
        return str;
    }

    public static String HttpsGet(String url,String encoding) {
        LogUtils.e("HttpUtils", "httpsget url: " + url + " encoding: " + encoding);
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            MyTrustManager mtm = new MyTrustManager();
            sc.init(null, new TrustManager[]{mtm}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "event";
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return "event";
        }
        HttpsURLConnection conn = null;
        String str="";
        try{
            conn = (HttpsURLConnection)new URL(url).openConnection();
            conn.setDoInput(true);// 打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);// 打开输出流，以便向服务器提交数据
            conn.setConnectTimeout(getTimeOut); // 设置连接超时时间
            conn.setReadTimeout(getTimeOut); //设置返回超时时间,下面要对超时进行处理
            conn.setRequestMethod("GET");
            conn.connect();
            int response = conn.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),encoding));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null){
                    line = new String(line.getBytes(),"UTF-8");
                    sb.append(line);
                }
                str = sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            //里面会抛连接和返回超时java.net.SocketTimeoutException，还有IO异常
            return "event";
        }finally{
            if(conn != null){
                conn.disconnect();
            }
        }
        return str;
    }

    /**
     * 封装请求体信息
     * params 请求体内容，
     * encode 编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params,String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        stringBuffer.append(Constants.service_date);//向服务器传递全局数据
//        stringBuffer.append("device_id="+ CGApp.getApp().getDeviceId()+"&");//向服务器发送设备号
//        stringBuffer.append("channel="+ CGApp.getApp().getChannel()+"&");//向服务器发送渠道号
//        stringBuffer.append("device_serial_id="+ CGApp.getApp().getImi_id()+"&");//向服务器发送手机本身的设备号
//        stringBuffer.append(Constants.latitude+"="+CGApp.getApp().getLatitude()+"&");//向服务器发送经度
//        stringBuffer.append(Constants.longitude+"="+CGApp.getApp().getLongitude()+"&");//向服务器发送纬度
        try {
            if(params != null && params.size()>0){
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuffer.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                }
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i("",stringBuffer.toString());
        return stringBuffer;
    }

    static class MyTrustManager implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /**
     * 服务器下载文件
     * @param fileDir 文件保存路径
     * @param fileName 文件名称
     * @param url_ 下载路径
     * @return
     */
    public static boolean downLoadingFile(String fileDir,String fileName,String url_){
        boolean flag = false;
        HttpURLConnection conn = null;
        FileUtils fileUtils = new FileUtils();
        try {
            conn = (HttpURLConnection) (new URL(url_)).openConnection();
            conn.setRequestMethod("GET");
            conn.getDoInput();
            conn.getDoOutput();
            conn.connect();
            if(conn.getResponseCode() == 200){
                File file = fileUtils.write2SDFromInput(fileDir,fileName,conn.getInputStream());
                if(file != null){
                    flag = true;
                }else{
                    flag = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }finally {
            if(conn != null){
                conn.disconnect();
                conn = null;
            }
        }
        return flag;
    }

    /**
     * 从网络获取图像
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public static byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeout);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }
    /**
     * 得到数据流
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

}
