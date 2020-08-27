package tk.twilightlemon.lemonapp.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpHelper {
    public static void PostWeb(final Handler handler,final String Url,final String postData,final HashMap<String,String> Headers){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(50000);//超时时间
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    if (Headers != null) {
                        Iterator iter = Headers.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            conn.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
                        }
                    }
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(postData);
                    out.flush();
                    out.close();

                    InputStream inputStream = conn.getInputStream();
                    String result=streamToString(inputStream);
                    Message msg=new Message();
                    msg.obj=result;
                    handler.sendMessage(msg);
                }catch (Exception e){}
            }
        }).start();
    }
    public static void GetWeb(final Handler handler, final String url, final HashMap<String, String> Headers) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL ur = new URL(url);
                    HttpURLConnection urlConn = (HttpURLConnection) ur.openConnection();
                    urlConn.setRequestMethod("GET");
                    if (Headers != null) {
                        Iterator iter = Headers.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            urlConn.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
                        }
                    }
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.addRequestProperty("Connection", "Keep-Alive");
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {
                        String result = streamToString(urlConn.getInputStream());
                        Message message = new Message();
                        message.what = 0;
                        message.obj = result;
                        handler.sendMessage(message);
                    }
                    urlConn.disconnect();
                }catch (Exception e){}
            }
        }).start();
    }

    public static HashMap<String,String> GetHandler(){
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", Settings.Cookie);
        return data;
    }

    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            return null;
        }
    }

}
