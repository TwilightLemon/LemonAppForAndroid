package tk.twilightlemon.lemonapp.layouts;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.Helpers.TextHelper;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.layouts.MainActivity;

public class LoginActivity extends AppCompatActivity {

    public static Handler LoginCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WebView wv=(WebView)findViewById(R.id.Login_wv);
        WebSettings settings=wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.53 Safari/537.36 Edg/80.0.361.33");
        wv.loadUrl("https://xui.ptlogin2.qq.com/cgi-bin/xlogin?daid=384&pt_no_auth=1&style=40&hide_border=1&appid=1006102&s_url=https%3A%2F%2Fy.qq.com%2Fn%2Fyqq%2Fsong%2F000edOaL1WZOWq.html%23stat%3Dy_new.top.pop.logout&low_login=1&hln_css=&hln_title=&hln_acc=&hln_pwd=&hln_u_tips=&hln_p_tips=&hln_autologin=&hln_login=&hln_otheracc=&hide_close_icon=1&hln_qloginacc=&hln_reg=&hln_vctitle=&hln_verifycode=&hln_vclogin=&hln_feedback=");
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.contains("xui.ptlogin2.qq.com")) {
                    view.evaluateJavascript("var e = document.createEvent(\"MouseEvents\");\n" +
                            "e.initEvent(\"click\", true, true);\n" +
                            "document.getElementById(\"switcher_plogin\").dispatchEvent(e);",null);
                    view.evaluateJavascript("document.getElementById(\"title_2\").innerHTML=\"登录到Lemon App\";",null);
                }else if(url.equals("https://y.qq.com/n/yqq/song/000edOaL1WZOWq.html#stat=y_new.top.pop.logout")){
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie  = cookieManager.getCookie(url);
                    Matcher mc = Pattern.compile("p_luin=o.*?;").matcher(cookie);
                    mc.find();
                    String qq=TextHelper.FindByAb(mc.group(), "p_luin=o", ";");
                    long g_tk=0;
                    if (cookie.contains("p_skey="))
                    {
                        String pattern = "p_skey=.*?;";
                        Pattern r = Pattern.compile(pattern);
                        Matcher m = r.matcher(cookie);
                        m.find();
                        String p_skey = TextHelper.FindByAb(m.group(), "p_skey=", ";");
                        long hash = 5381;
                        for (int i = 0; i < p_skey.length(); i++)
                        {
                            hash += (hash << 5) + p_skey.charAt(i);
                        }
                        g_tk = hash & 0x7fffffff;
                    }
                    Message msg=new Message();
                    InfoHelper.LoginData ld= new InfoHelper.LoginData();
                    ld.Cookie=cookie;
                    ld.g_tk=g_tk+"";
                    ld.qq=qq;
                    msg.obj=ld;
                    LoginCallback.sendMessage(msg);
                    view.setWebViewClient(null);
                    finish();
                }
                super.onPageFinished(view, url);
            }
        });
        wv.onResume();
    }
}
