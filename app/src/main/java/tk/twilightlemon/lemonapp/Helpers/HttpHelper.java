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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpHelper {
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

    public static void Download(final String urll, final Handler pro, final Handler finished, final String fileName, final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urll);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30 * 1000);
                    InputStream is = conn.getInputStream();
                    Uri uri = null;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
                    contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());
                    //只是往 MediaStore 里面插入一条新的记录，MediaStore 会返回给我们一个空的 Content Uri
                    //接下来问题就转化为往这个 Content Uri 里面写入
                    uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
                    BufferedInputStream inputStream = new BufferedInputStream(is);
                    OutputStream os = null;
                    if (uri != null) {
                        os = context.getContentResolver().openOutputStream(uri);
                    }
                    if (os != null) {
                        byte[] buffer = new byte[1024];
                        int len;
                        int total = 0;
                        int contentLeng = conn.getContentLength();
                        while ((len = inputStream.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                            total += len;
                            int pp=total * 100 / contentLeng;
                            Message msg=new Message();
                            msg.arg1=pp;
                            pro.sendMessage(msg);
                        }
                        finished.sendMessage(new Message());
                    }
                    os.flush();
                    inputStream.close();
                    is.close();
                    os.close();
                }catch (Exception e){}
            }
        }).start();
    }
}
