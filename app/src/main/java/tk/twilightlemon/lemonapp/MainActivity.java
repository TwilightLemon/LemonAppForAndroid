package tk.twilightlemon.lemonapp;
/**
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
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static String SDPATH = "";
    MediaPlayer mp=new MediaPlayer();
    LrcView lrcBig=null;
    boolean isplaying=false;
    int PlayListIndex=-1;
    InfoHelper.Music Musicdt=null;
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {

        @Override
        public void run() {
            SeekBar MseekBar=findViewById(R.id.MusicSeek);
            int in=mp.getCurrentPosition();
            MseekBar.setProgress(in); MseekBar.setMax(mp.getDuration());
            if(findViewById(R.id.LyricView).getVisibility()==View.VISIBLE)
                 lrcBig.updateTime(in);

            if(isplaying&&MseekBar.getProgress()+3000>=MseekBar.getMax()){
                mHandler.removeCallbacks(r);
                if(xhindex==0){
                    if(PlayListIndex==Settings.ListData.Data.size()-1)
                        PlayListIndex=0;
                    else ++PlayListIndex;
                }
                Musicdt = Settings.ListData.Data.get(PlayListIndex);
                PlayMusic();
            }

            mHandler.postDelayed(this, 1000);
        }
    };
    /////加载区/////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lv();
        SetWindow();
        SetTitle();
        LoadSettings();
        SetLoginPage();
        LoadMusicControls();

    }
    int xhindex=0;
    int REQUEST_STORAGE_PERMISSION=1;
    public void PlayMusic(){
        mp.stop();
        mHandler.removeCallbacks(r);
        mp = new MediaPlayer();
        isplaying=true;
        lrcBig.updateTime(0);
        final ImageView PlayBottom_img = findViewById(R.id.PlayBottom_img);
        TextView PlayBottom_title = findViewById(R.id.PlayBottom_title);
        TextView PlayBottom_mss = findViewById(R.id.PlayBottom_mss);
        ImageButton PlayBottom_ControlBtn = findViewById(R.id.PlayBottom_ControlBtn);
        Drawable ic=getResources().getDrawable(R.drawable.ic_unplaybtn);
        PlayBottom_ControlBtn.setImageDrawable(ic);
        ImageButton MButton=findViewById(R.id.MButton);
        MButton.setImageDrawable(ic);
        HttpHelper.GetWebImage("Music" + Musicdt.MusicID + ".jpg", Musicdt.ImageUrl, MainActivity.this,
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Bitmap bmp=(Bitmap) msg.obj;
                        RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), bmp);
                        RBD.setCircular(true);
                        ImageView MUSICZJ=findViewById(R.id.MUSICZJ);
                        MUSICZJ.setImageDrawable(RBD);
                        PlayBottom_img.setImageDrawable(RBD);
                        RelativeLayout MImageBackground= findViewById(R.id.MImageBackground);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            MImageBackground.setBackground(new BitmapDrawable(BlurBitmap.blur(MainActivity.this,bmp)));
                        }
                    }
                });
        PlayBottom_title.setText(Musicdt.MusicName);
        PlayBottom_mss.setText(Musicdt.Singer);
        HttpHelper.PostWeb("http://lab.mkblog.cn/music/api.php", "types=url&id=" + Musicdt.MusicID + "&source=tencent", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    String url = new JSONObject(msg.obj.toString()).getString("url");
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                    GetMusicLyric(Musicdt.MusicID);
                    mHandler.postDelayed(r, 1000);

                    TextView Title=findViewById(R.id.MusicTitle);
                    Title.setText(Musicdt.MusicName);
                    TextView Mss=findViewById(R.id.MusicMss);
                    Mss.setText(Musicdt.Singer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void SetWindow(){
        /////配置沉浸式窗口
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public void SetTitle(){
        ///////配置标题栏
        ViewPager mViewPager = findViewById(R.id.view_pager);
        TabLayout mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FirstFragment.newInstance());
        fragments.add(SecondFragment.newInstance());
        //TODO:fragments.add(ThirdFragment.newInstance());

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(),fragments, Arrays.asList(new String[]{"我的","音乐馆"}));//TODO:new String[]{"我的","音乐馆","电台"}));
        mViewPager.setAdapter(adapter);
    }
    public void LoadSettings(){
        //DATA目录
        SDPATH = getFilesDir().getPath() + "//";
        File file = new File(SDPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        //////读取设置
        SharedPreferences sp = MainActivity.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
        if (sp.contains("name")) {
            ((TextView) findViewById(R.id.USERNAME)).setText(sp.getString("name", ""));
            final String nu = sp.getString("qq", "");
            HttpHelper.GetWebImage(nu + ".jpg","http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100",MainActivity.this,
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                            RBD.setCircular(true);
                            ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                        }
                    });

            Settings.qq=nu;
        }
    }
    public void SetLoginPage(){
        /////////Login模块
        findViewById(R.id.USERNAME).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText qq = new EditText(MainActivity.this);
                qq.setHint("QQ账号");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("登录到小萌").setView(qq)
                        .setNegativeButton("关闭", null);
                builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            final String nu = qq.getText().toString();
                            HttpHelper.GetWebImage(nu + ".jpg","http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100",MainActivity.this,
                                    new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                                            RBD.setCircular(true);
                                            ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                                        }
                                    });
                            HttpHelper.GetWeb(new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    switch (msg.what) {
                                        case 0:
                                            String response = (String) msg.obj;
                                            String name = FindByAb(response, "{\"nick\":\"", "\",\"headpic\"");
                                            ((TextView) findViewById(R.id.USERNAME)).setText(name);
                                            SharedPreferences preferences = MainActivity.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("qq", qq.getText().toString());
                                            editor.putString("name", name);
                                            editor.commit();
                                            Settings.qq=qq.getText().toString();
                                            restartApplication();
                                            break;

                                        default:
                                            break;
                                    }
                                }

                            }, "https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?loginUin=" + nu + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205360838&ct=20&userid=" + nu + "&reqfrom=1&reqtype=0",null);
                        } catch (Exception e) {
                        }
                    }
                });
                builder.show();
            }
        });
    }
    public void LoadMusicControls(){
        ///播放回调
        Settings.Callback_PlayMusic=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (((int)msg.obj) != -1) {
                        //TODO:此处处理由MusicListPage传来的播放事件
                        PlayListIndex= (int) msg.obj;
                        Musicdt = Settings.ListData.Data.get(PlayListIndex);
                        PlayMusic();
                    }
                }catch (Exception e){}
            }};

        ///一些控件
        lrcBig=findViewById(R.id.lrc);
        final ImageButton PlayBottom_ControlBtn=findViewById(R.id.PlayBottom_ControlBtn);
        final ImageButton MButton=findViewById(R.id.MButton);
        Drawable playic=getResources().getDrawable(R.drawable.ic_playbtn);
        PlayBottom_ControlBtn.setImageDrawable(playic);
        MButton.setImageDrawable(playic);
        View.OnClickListener lister=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable ic=null;
                if(isplaying){
                    ic=getResources().getDrawable(R.drawable.ic_playbtn);
                    isplaying=false;
                    mp.pause();
                }else{
                    ic=getResources().getDrawable(R.drawable.ic_unplaybtn);
                    isplaying=true;
                    mp.start();
                }
                PlayBottom_ControlBtn.setImageDrawable(ic);
                MButton.setImageDrawable(ic);
            }
        };
        PlayBottom_ControlBtn.setOnClickListener(lister);
        MButton.setOnClickListener(lister);
        final SeekBar MseekBar=findViewById(R.id.MusicSeek);
        final boolean[] isChanging = {false};
        MseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging[0] =true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(MseekBar.getProgress());
                isChanging[0] =false;
            }
        });

        findViewById(R.id.PlayBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View s=findViewById(R.id.LyricView);
                s.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(s, "translationY",  1200f, 0f);
                animator.setDuration(200);
                animator.start();
                lrcBig.initEntryList();
                lrcBig.initNextTime();
            }
        });

        findViewById(R.id.M_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View s=findViewById(R.id.LyricView);
                ObjectAnimator animator = ObjectAnimator.ofFloat(s, "translationY",  1200f);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        s.setVisibility(View.GONE);
                    }
                });
                animator.setDuration(200);
                animator.start();
            }
        });

        findViewById(R.id.musicnexts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PlayListIndex==0)
                    PlayListIndex=Settings.ListData.Data.size()-1;
                else --PlayListIndex;
                Musicdt = Settings.ListData.Data.get(PlayListIndex);
                PlayMusic();
            }
        });

        findViewById(R.id.musicnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PlayListIndex==Settings.ListData.Data.size()-1)
                    PlayListIndex=0;
                else ++PlayListIndex;
                Musicdt = Settings.ListData.Data.get(PlayListIndex);
                PlayMusic();
            }
        });

        final ImageButton musicxh=findViewById(R.id.musicxh);musicxh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(xhindex ==0){
                    xhindex =1;
                    musicxh.setImageResource(R.drawable.ic_musicdq);
                }else{
                    xhindex=0;
                    musicxh.setImageResource(R.drawable.ic_musiclb);
                }
            }
        });
    }
    public void Lv(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }
    ///////加载区end//////
    //////功能区//////
    public static String FindByAb(String all, String a, String b) {
        return all.substring(all.indexOf(a) + a.length(), all.indexOf(b));
    }
    public void SendMessageBox(String msg) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("提示");
        dlg.setMessage(msg);
        dlg.setPositiveButton("确定", null);
        dlg.show();
    }
    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void GetMusicLyric(String ID)  {
        lrcBig.reset();
        lrcBig.initEntryList();
        lrcBig.initNextTime();

        HashMap<String,String> data=new HashMap<>();
        data.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Cookie", "yqq_stat=0; pgv_info=ssid=s5197017565; pgv_pvid=507491580; ts_uid=5941690444; pgv_pvi=1047845888; pgv_si=s4656565248; yq_index=0; player_exist=1; yq_playschange=0; yq_playdata=; qqmusic_fromtag=66; ts_last=y.qq.com/portal/player.html; yplayer_open=1");
        data.put("Host", "c.y.qq.com");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try{
                super.handleMessage(msg);
                String Ct=msg.obj.toString();
                String Ctlyric=Ct.substring(Ct.indexOf("\"lyric\":\"")+9,Ct.indexOf("\",\""));
                String lyricdata = escapeHtml(new String(Base64.decode(Ctlyric.getBytes(), Base64.DEFAULT)));
                String Cttrans=Ct.substring(Ct.indexOf("\"trans\":\"")+9,Ct.indexOf("\"})"));
                String transdata =new String(Base64.decode(Cttrans.getBytes(), Base64.DEFAULT));
                // SendMessageBox(lyricdata +transdata);
                ArrayList<String> datatimes=new ArrayList<String>();
                ArrayList<String> datatexs=new ArrayList<String>();
                HashMap<String,String> gcdata=new HashMap<String, String>();
                String[] dt=lyricdata.split("[\n]");
                for (String x:dt) {
                    parserLine(x,datatimes,datatexs,gcdata);
                }
                //sdm("d1");
                ArrayList<String> dataatimes=new ArrayList<String>();
                ArrayList<String> dataatexs=new ArrayList<String>();
                HashMap<String,String> fydata=new HashMap<String, String>();
                String[] dta=transdata.split("[\n]");
                for (String x:dta) {
                    parserLine(x,dataatimes,dataatexs,fydata);
                }
                //  sdm("d2");
                ArrayList<String> KEY=new ArrayList<String>();
                HashMap<String,String> gcfydata=new HashMap<String, String>();
                List<LrcEntry> list=new ArrayList<>();
                for (String x:datatimes) {
                    KEY.add(x);
                    gcfydata.put(x,"");
                }
                //sdm("d3");
                for (String x:dataatimes) {
                    if(!KEY.contains(x)){
                        KEY.add(x);
                        gcfydata.put(x,"");
                    }
                }
                //sdm("d4");
                for(int i=0;i!=gcfydata.size();i++){
                    try{
                        gcfydata.put(KEY.get(i),gcdata.get(KEY.get(i))+"^"+fydata.get(KEY.get(i)));
                    }catch(Exception e){}
                }
                //sdm("d5   "+dataatexs.size()+"   "+dataatimes.size()+"   "+datatexs.size()+"   "+datatimes.size()+"   "+KEY.size());
                for(int i=0;i!=KEY.size();i++){
                    try{
                        long key=strToTime(KEY.get(i));
                        String value =gcfydata.get(KEY.get(i));
                        list.add(new LrcEntry(key,value.replace("^","\n").replace("//","").replace("null","") ));
                    }catch(Exception e){}
                }
                lrcBig.reset();
                lrcBig.onLrcLoaded(list);
                lrcBig.initEntryList();
                lrcBig.initNextTime();}catch (Exception e){}

            }}
                ,"https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1532863605625&songmid="+ID+"&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0",data);
    }
    private long strToTime(String ts) {
        long time=0;
        Matcher timeMatcher = Pattern.compile("(\\d\\d):(\\d\\d)\\.(\\d\\d)").matcher(ts);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
        }
        return time;
    }
    public String parserLine(String str,ArrayList<String> times,ArrayList<String>texs,HashMap<String,String> data){
        if (!str.startsWith("[ti:")&&!str.startsWith("[ar:")&&!str.startsWith("[al:")&&!str.startsWith("[by:")&&!str.startsWith("[offset:")){
            String TimeData=FindByAb(str,"[","]")+"0";
            times.add(TimeData);
            String INFO=FindByAb(str,"[","]");
            String io="["+INFO+"]";
            String TexsData=str.replace(io,"");
            texs.add(TexsData);
            data.put(TimeData,TexsData);
            return TimeData+"     "+TexsData;
        }else return "";
    }
    public String escapeHtml(String str){
        return str.replace("&apos;","'").replace("&nbsp;"," ");
    }
    //////功能区end////
}