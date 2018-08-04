package tk.twilightlemon.lemonapp;
/**                           HTTP请求类
 *                      1.    GetWeb :发送get请求
 *                      2.    PostWeb: 发送POST请求
 *                      3.    GetWebImage :获取并缓存网络图片
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
*/
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

public class HttpHelper {
    public static void GetWeb(final Handler handler, final String url, final HashMap<String,String> Headers) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpClient httpCient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                if(Headers!=null){
                    Iterator iter = Headers.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        httpGet.addHeader(entry.getKey().toString(),entry.getValue().toString());
                    }
                }
                try {
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        Message message = new Message();
                        message.what = 0;
                        message.obj = response.toString();
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public static void PostWeb(final String Url, final String data, final Handler handler){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                URL url = new URL(Url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Length", data.length()+"");
                connection.setRequestProperty("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Cookie", "Hm_lvt_6e8dac14399b608f633394093523542e=1522910113; Hm_lpvt_6e8dac14399b608f633394093523542e=1522910122; Hm_lvt_ea4269d8a00e95fdb9ee61e3041a8f98=1522910125; Hm_lpvt_ea4269d8a00e95fdb9ee61e3041a8f98=1522910125");
                connection.setRequestProperty("Host", "lab.mkblog.cn");
                connection.setRequestProperty("Origin", "http://lab.mkblog.cn");
                connection.setRequestProperty("Referer", "http://lab.mkblog.cn/music/");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());

                //获得结果码
                int responseCode = connection.getResponseCode();
                if(responseCode ==200) {
                    //请求成功
                    InputStream ins = connection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    StringBuilder sb = new StringBuilder();
                    while ((length = ins.read(buffer)) != -1) {
                        sb.append(new String(buffer, 0, length));
                    }

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    handler.sendMessage(msg);
                }
                }catch (Exception e){}
            }
        }).start();
    }
    public static void GetWebImage(String filename, String Url, Context t, final Handler handler){
        final File f = new File(MainActivity.SDPATH + "LemonApp/UserCache/" + filename);
        if (f.exists()) {
            Bitmap cacha = BitmapFactory.decodeFile(f.toString());
            Message msg = Message.obtain();
            msg.obj = cacha;
            msg.what = 1000;
            handler.sendMessage(msg);
        } else {
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request
                    .Builder()
                    .get()
                    .url(Url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bit = BitmapFactory.decodeStream(inputStream);
                    Message msg = Message.obtain();
                    msg.obj = bit;
                    msg.what = 1000;
                    handler.sendMessage(msg);
                    //将图片保存到本地存储卡中
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    byte[] temp = new byte[128];
                    int length;
                    while ((length = inputStream.read(temp)) != -1) {
                        fileOutputStream.write(temp, 0, length);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                }
            });
        }
    }
}
