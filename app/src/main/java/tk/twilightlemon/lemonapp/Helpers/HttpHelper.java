package tk.twilightlemon.lemonapp.Helpers;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
