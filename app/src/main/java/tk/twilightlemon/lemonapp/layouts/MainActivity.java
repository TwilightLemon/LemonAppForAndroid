package tk.twilightlemon.lemonapp.layouts;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import tk.twilightlemon.lemonapp.Adapters.MusicListAdapter;
import tk.twilightlemon.lemonapp.Adapters.MyFragmentAdapter;
import tk.twilightlemon.lemonapp.Helpers.Image.BitmapUtils;
import tk.twilightlemon.lemonapp.Helpers.Image.BlurBitmap;
import tk.twilightlemon.lemonapp.Fragments.FirstFragment;
import tk.twilightlemon.lemonapp.Helpers.HttpHelper;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcView;
import tk.twilightlemon.lemonapp.Helpers.MusicLib;
import tk.twilightlemon.lemonapp.Helpers.MiuiUtils;
import tk.twilightlemon.lemonapp.Helpers.TextHelper;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Fragments.SecondFragment;
import tk.twilightlemon.lemonapp.Helpers.Settings;

import static tk.twilightlemon.lemonapp.Helpers.TextHelper.FindByAb;

public class MainActivity extends AppCompatActivity {
    //<editor-fold desc="常量">
    private InfoHelper.KeepliveBCR keeplive=null;

    private int REQUEST_STORAGE_PERMISSION = 1;

    private boolean isplaying = false;
    private int xhindex = 0;
    private int PlayListIndex = -1;
    private InfoHelper.Music Musicdt = new InfoHelper().new Music();

    private SharedPreferences musicPlayerSP = null;
    //</editor-fold>

    //<editor-fold desc="控件">
    private LrcView lrcBig = null;
    private SeekBar MseekBar = null;
    private View lyricView = null;
    private View MusicList = null;

    private ImageButton PlayBottom_ControlBtn;
    private ImageButton MButton;

    private NotificationManager notificationManager = null;
    private InfoHelper.NotificationBCR myBroadcastReceiver = null;
    private RemoteViews remoteViews = null;
    private Notification notification = null;

    private TextView MusicList_title=null;
    private ListView MusicList_list=null;
    //</editor-fold>

    //<editor-fold desc="播放Timer">
    private Handler mHandler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (isplaying && MseekBar.getProgress() + 2000 >= MseekBar.getMax()) {
                isplaying = false;
                mHandler.removeCallbacks(r);
                MseekBar.setProgress(0);
                if (xhindex == 0) {
                    if (Settings.ListData.name == "Radio") {
                        MusicLib.GetRadioMusicById(Settings.ListData.id, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                PlayListIndex = 0;
                                InfoHelper.MusicGData GData = new InfoHelper().new MusicGData();
                                GData.Data.add((InfoHelper.Music) msg.obj);
                                GData.name = "Radio";
                                GData.id = Settings.ListData.id;
                                Settings.ListData = GData;
                                Musicdt = Settings.ListData.Data.get(PlayListIndex);
                                PlayMusic(true,0);
                            }
                        });
                    } else {
                        if (PlayListIndex == Settings.ListData.Data.size() - 1)
                            PlayListIndex = 0;
                        else ++PlayListIndex;
                        Musicdt = Settings.ListData.Data.get(PlayListIndex);
                        PlayMusic(true,0);
                    }
                } else {
                    Musicdt = Settings.ListData.Data.get(PlayListIndex);
                    PlayMusic(true,0);
                }
            } else {
                try {
                    int in = Settings.mp.getCurrentPosition();
                    MseekBar.setProgress(in);
                    Settings.mSP.putInt("NowDuration",in);
                    if (findViewById(R.id.LyricView).getVisibility() == View.VISIBLE)
                        lrcBig.updateTime(in);
                    mHandler.postDelayed(this, 1000);
                } catch (Exception E) {
                }
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="Activity重写事件">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.start(this);
        Lv();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (lyricView.getVisibility() != View.GONE)
                LyricBack();
            else if(MusicList.getVisibility()!=View.GONE)
                MusicListBack();
            else moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        if (!Settings.isActive) {
            Settings.isActive = true;
            Updata();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (!isAppOnForeground())
            Settings.isActive = false;
        Settings.mSP.commit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        notificationManager.cancelAll();
        unregisterReceiver(keeplive);
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="OnCreate方法集">
    public void SetWindow() {
        /////配置沉浸式窗口
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void SetTitle() {
        ViewPager mViewPager = findViewById(R.id.view_pager);
        TabLayout mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FirstFragment.newInstance());
        fragments.add(SecondFragment.newInstance());
        //TODO:fragments.add(ThirdFragment.newInstance());
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments, Arrays.asList(new String[]{"我的", "音乐馆"}));//TODO:new String[]{"我的","音乐馆","电台"}));
        mViewPager.setAdapter(adapter);
    }

    public void LoadSettings() {
        //////读取配置
        SharedPreferences sp = MainActivity.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
        if (sp.contains("name")) {
            ((TextView) findViewById(R.id.USERNAME)).setText(sp.getString("name", ""));
            final String nu = sp.getString("qq", "");
            BitmapUtils bu = new BitmapUtils();
            @SuppressLint("HandlerLeak") Handler hl = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                    RBD.setCircular(true);
                    ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                }
            };
            bu.disPlay(hl, "http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100");
            Settings.qq = nu;
        }

        //读取上次播放
        musicPlayerSP = MainActivity.this.getSharedPreferences("MusicPlayer", Context.MODE_PRIVATE);
        Settings.mSP = musicPlayerSP.edit();
        if(musicPlayerSP.contains("MusicName")){
            Musicdt.MusicName=musicPlayerSP.getString("MusicName","");
            Musicdt.Singer=musicPlayerSP.getString("Singer","");
            Musicdt.MusicID=musicPlayerSP.getString("MusicID","");
            Musicdt.ImageUrl=musicPlayerSP.getString("ImageUrl","");
            Musicdt.GC=musicPlayerSP.getString("GC","");
            PlayMusic(false,musicPlayerSP.getInt("NowDuration",0));
            String ListID=musicPlayerSP.getString("ListData","");
            if(ListID.contains("Search"))
                MusicLib.Search(this,TextHelper.Base64Coder.Decode(FindByAb(ListID,"[","]")),false);
            else if(ListID.contains("Diss")){
                InfoHelper.MusicGData gData=new InfoHelper().new MusicGData();
                gData.id=FindByAb(ListID,"[","]");
                gData.name=FindByAb(ListID,"skName{","}");
                MusicLib.GetGDbyID(gData,this,false);
            }else if(musicPlayerSP.getBoolean("isRadio",false)){
                Settings.ListData.name="Radio";
                Settings.ListData.id=FindByAb(ListID,"[","]");
            }
            PlayListIndex=musicPlayerSP.getInt("Index",0);
        }
    }

    public void SetLoginPage() {
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
                            StatService.onEvent(MainActivity.this, "tw_Login", "活跃用户", 1);
                            final String nu = qq.getText().toString();
                            BitmapUtils bu = new BitmapUtils();
                            Handler hl = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                                    RBD.setCircular(true);
                                    ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                                }
                            };
                            bu.disPlay(hl, "http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100");
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
                                            Settings.qq = qq.getText().toString();
                                            SetTitle();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }, "https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?loginUin=" + nu + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205360838&ct=20&userid=" + nu + "&reqfrom=1&reqtype=0", null);
                        } catch (Exception e) {
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void LoadMusicControls() {
        //<editor-fold desc="回调&事件">
        ///播放回调
        Settings.Callback_PlayMusic = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (((int) msg.obj) != -1) {
                        PlayListIndex = (int) msg.obj;
                        Musicdt = Settings.ListData.Data.get(PlayListIndex);
                        PlayMusic(true,0);
                    }
                } catch (Exception e) {
                }
            }
        };
        //Notification控制回调
        Settings.ACTIONCALLBACK = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String code = msg.obj.toString();
                switch (code) {
                    case InfoHelper.NotificationBCR.ACTION_LAST:
                        Music_Last();
                        break;
                    case InfoHelper.NotificationBCR.ACTION_PRESS:
                        Music_Press(isplaying);
                        isplaying = !isplaying;
                        break;
                    case InfoHelper.NotificationBCR.ACTION_NEXT:
                        Music_Next();
                        break;
                }
            }
        };
        ///Audio回调
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                           @Override
                                           public void onAudioFocusChange(int i) {
                                               switch (i) {
                                                   case AudioManager.AUDIOFOCUS_GAIN:
                                                       if (isplaying) {
                                                           Music_Press(false);
                                                           Settings.mp.setVolume(1.0f, 1.0f);
                                                       }
                                                       break;
                                                   case AudioManager.AUDIOFOCUS_LOSS:
                                                       if (isplaying)
                                                           Music_Press(true);
                                                       break;
                                                   case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                                       if (isplaying)
                                                           Music_Press(true);
                                                       break;
                                                   case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                                       if (isplaying) Settings.mp.setVolume(0.1f, 0.1f);
                                                       break;
                                               }

                                           }
                                       }, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        //</editor-fold>

        //<editor-fold desc="控件实例化">
        MusicList_list=findViewById(R.id.MusicList_list);
        MusicList_title=findViewById(R.id.MusicList_title);
        MusicList = findViewById(R.id.MusicList);
        lyricView = findViewById(R.id.LyricView);
        MseekBar = findViewById(R.id.MusicSeek);
        lrcBig = findViewById(R.id.lrc);
        PlayBottom_ControlBtn = findViewById(R.id.PlayBottom_ControlBtn);
        MButton = findViewById(R.id.MButton);
        //</editor-fold>

        //<editor-fold desc="初始内容&事件">
        lrcBig.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                Settings.mp.seekTo((int) time);
                return true;
            }
        });
        ((ImageView) findViewById(R.id.PlayBottom_img)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_round));
        Drawable playic = getResources().getDrawable(R.drawable.ic_playbtn);
        PlayBottom_ControlBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bom_play));
        MButton.setImageDrawable(playic);
        View.OnClickListener lister = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Music_Press(isplaying);
                isplaying = !isplaying;
            }
        };
        PlayBottom_ControlBtn.setOnClickListener(lister);
        MButton.setOnClickListener(lister);
        final SeekBar MseekBar = findViewById(R.id.MusicSeek);
        final boolean[] isChanging = {false};
        MseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging[0] = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings.mp.seekTo(MseekBar.getProgress());
                isChanging[0] = false;
            }
        });
        findViewById(R.id.PlayBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LyricShow();
            }
        });
        findViewById(R.id.M_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LyricBack();
            }
        });
        findViewById(R.id.musicnexts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Music_Last();
            }
        });
        findViewById(R.id.musicnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Music_Next();
            }
        });
        final ImageButton musicxh = findViewById(R.id.musicxh);
        musicxh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (xhindex == 0) {
                    xhindex = 1;
                    musicxh.setImageResource(R.drawable.ic_musicdq);
                } else {
                    xhindex = 0;
                    musicxh.setImageResource(R.drawable.ic_musiclb);
                }
            }
        });
        findViewById(R.id.musiclistBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicListShow();
            }
        });
        //</editor-fold>
    }

    public void Lv() {
        //TODO:GO TO HELL,MIUI!!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SharedPreferences preferences = MainActivity.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
            if (MiuiUtils.isMIUI()) {
                if (!preferences.contains("isLocked")) {
                    final TextView tv = new TextView(MainActivity.this);
                    tv.setText("#锁屏显示权限:由于MIUI系统限制，需要手动授权，此权限仅用于锁屏下正常播放");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("需要新权限嘞(っ °Д °;)っ").setView(tv)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            StatService.onEvent(MainActivity.this, "tw_miui", "辣鸡MIUI使用人数", 1);
                            sdm("请给予小萌“锁屏显示”权限(●'◡'●)", MainActivity.this);
                            editor.putBoolean("isLocked", true);
                            editor.commit();
                            MiuiUtils.jumpToPermissionsEditorActivity(MainActivity.this);
                        }
                    });
                    builder.show();
                }
            }
        }
        SetWindow();
        Updata();
        SetTitle();
        SetLoginPage();
        MainActivity.Loading(findViewById(R.id.USERTX));
        LoadMusicControls();
        LoadSettings();
        LoadNotification();
        KeepLive();
    }

    public void Updata() {
        HashMap<String, String> data = new HashMap<>();
        data.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        data.put("Accept-Language", "zh-CN,zh;q=0.9");
        data.put("Cache-Control", "max-age=0");
        data.put("Connection", "keep-alive");
        data.put("Host", "coding.net");
        data.put("Referer", "https://coding.net/u/twilightlemon/p/Updata/git/blob/master/AndroidUpdata.json");
        data.put("Upgrade-Insecure-Requests", "1");
        data.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    JSONObject o = new JSONObject(msg.obj.toString());
                    if (Integer.parseInt(o.getString("version").replace(".", "")) > MainActivity.this.getPackageManager().
                            getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode) {
                        final TextView tv = new TextView(MainActivity.this);
                        tv.setText("新版本:" + o.getString("version") + "\n" + o.getString("description").replace("@32", "\n"));
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("小萌有新版本啦").setView(tv)
                                .setNegativeButton("关闭", null);
                        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                StatService.onEvent(MainActivity.this, "tw_updata", "更新数", 1);
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("https://coding.net/u/twilightlemon/p/Updata/git/raw/master/app-release.apk");
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                    }
                } catch (Exception e) {
                }
            }
        }, "https://coding.net/u/twilightlemon/p/Updata/git/raw/master/AndroidUpdata.json", data);
    }

    @SuppressLint("HandlerLeak")
    public void OnShareClick(View view) {
        String uri="http://tools.aeink.com/tools/dwz/urldwz.php?longurl=http%3a%2f%2ftwilightlemon.coding.me%2fMusicPlayer%2f%3fid%3d"+Musicdt.MusicID+"%26name%3d"+URLEncoder.encode(Musicdt.MusicName)+"%26singer%3d"+URLEncoder.encode(Musicdt.Singer)+"%26imgid%3dhttp%3a%2f%2fy.gtimg.cn%2fmusic%2fphoto_new%2fT002R300x300M000"+TextHelper.FindByAb(Musicdt.ImageUrl,"300x300M000",".jpg")+".jpg&api=urlcn";
        HttpHelper.GetWeb(new Handler(){
            @Override
            public void handleMessage(Message msg){
                try {
                    String short_url = new JSONObject(msg.obj.toString()).getString("ae_url");
                    Intent share_intent = new Intent();
                    share_intent.setAction(Intent.ACTION_SEND);
                    share_intent.setType("text/plain");
                    share_intent.putExtra(Intent.EXTRA_SUBJECT, "小萌音乐分享");
                    share_intent.putExtra(Intent.EXTRA_TEXT, Musicdt.MusicName + " - " + Musicdt.Singer + "：" + short_url);
                    share_intent = Intent.createChooser(share_intent, "小萌音乐分享");
                    startActivity(share_intent);
                }catch (Exception e){}
            }
        },uri,null);
    }

    public void LoadNotification() {
        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        myBroadcastReceiver = new InfoHelper().new NotificationBCR();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InfoHelper.NotificationBCR.ACTION_LAST);
        intentFilter.addAction(InfoHelper.NotificationBCR.ACTION_PRESS);
        intentFilter.addAction(InfoHelper.NotificationBCR.ACTION_NEXT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        PendingIntent LASTPI = PendingIntent.getBroadcast(this, 0, new Intent(InfoHelper.NotificationBCR.ACTION_LAST), 0);
        PendingIntent PRESSPI = PendingIntent.getBroadcast(this, 0, new Intent(InfoHelper.NotificationBCR.ACTION_PRESS), 0);
        PendingIntent NEXTPI = PendingIntent.getBroadcast(this, 0, new Intent(InfoHelper.NotificationBCR.ACTION_NEXT), 0);
        PendingIntent ALLSHOWPI = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setOnClickPendingIntent(R.id.Notification_LastBtn, LASTPI);
        remoteViews.setOnClickPendingIntent(R.id.Notification_OpenBtn, PRESSPI);
        remoteViews.setOnClickPendingIntent(R.id.Notification_NextBtn, NEXTPI);
        remoteViews.setOnClickPendingIntent(R.id.Notification_Layout, ALLSHOWPI);
        remoteViews.setImageViewResource(R.id.Notification_OpenBtn, R.drawable.ic_not_open);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MusicControl", "控制音乐播放", NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId("MusicControl");
        }
        notification = mBuilder
                .setContentTitle("Lemon App")
                .setContentText("Music")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setContent(remoteViews)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(2, notification);
    }

    public void KeepLive() {
        keeplive = new InfoHelper().new KeepliveBCR();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(keeplive, intentFilter);
    }
    //</editor-fold>

    //<editor-fold desc="播放控制">
    public void Music_Last() {
        if (Settings.ListData.name != "Radio") {
            if (PlayListIndex == 0)
                PlayListIndex = Settings.ListData.Data.size() - 1;
            else --PlayListIndex;
            Musicdt = Settings.ListData.Data.get(PlayListIndex);
            PlayMusic(true,0);
        }
    }

    public void Music_Next() {
        if (Settings.ListData.name == "Radio") {
            MusicLib.GetRadioMusicById(Settings.ListData.id, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    PlayListIndex = 0;
                    InfoHelper.MusicGData GData = new InfoHelper().new MusicGData();
                    GData.Data.add((InfoHelper.Music) msg.obj);
                    GData.name = "Radio";
                    GData.id = Settings.ListData.id;
                    Settings.ListData = GData;
                    Musicdt = Settings.ListData.Data.get(PlayListIndex);
                    PlayMusic(true,0);
                }
            });
        } else {
            if (PlayListIndex == Settings.ListData.Data.size() - 1)
                PlayListIndex = 0;
            else ++PlayListIndex;
            Musicdt = Settings.ListData.Data.get(PlayListIndex);
            PlayMusic(true,0);
        }
    }

    public void Music_Press(boolean isplay) {
        Drawable ic = null;
        Drawable ic_bom = null;
        if (isplay) {
            ic = getResources().getDrawable(R.drawable.ic_playbtn);
            ic_bom = getResources().getDrawable(R.drawable.ic_bom_play);
            remoteViews.setImageViewResource(R.id.Notification_OpenBtn, R.drawable.ic_not_open);
            Settings.mp.pause();
        } else {
            ic = getResources().getDrawable(R.drawable.ic_unplaybtn);
            ic_bom = getResources().getDrawable(R.drawable.ic_bom_unplay);
            remoteViews.setImageViewResource(R.id.Notification_OpenBtn, R.drawable.ic_not_stop);
            Settings.mp.start();
        }
        PlayBottom_ControlBtn.setImageDrawable(ic_bom);
        MButton.setImageDrawable(ic);
        notificationManager.notify(2, notification);
    }
    //</editor-fold>

    //<editor-fold desc="页面控制">

    //<editor-fold desc="LyricView">
    public void LyricShow() {
        if(Musicdt.MusicName.length()!=0) {
            lyricView.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(lyricView, "translationY", 1200f, 0f);
            animator.setDuration(300);
            animator.start();
            lrcBig.initEntryList();
        }
    }

    public void LyricBack(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(lyricView, "translationY", 1200f);
        animator.setDuration(300);
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lyricView.setVisibility(View.GONE);
            }
        }, 300);
    }
    //</editor-fold>

    //<editor-fold desc="MusicList">
    public void MusicListLoad(boolean isShow){
        MusicList_title.setText(Settings.ListData.name);
        MusicListAdapter ad = new MusicListAdapter(MainActivity.this, Settings.ListData, new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View view) {
                try {
                    RelativeLayout PATENT = (RelativeLayout) view.getParent();
                    int index = Integer.parseInt(((TextView) PATENT.findViewById(R.id.MusicList_index)).getText().toString());
                    final InfoHelper.Music Data = Settings.ListData.Data.get(index);
                    String MusicID = Data.MusicID;
                    MusicLib.GetUrl(MusicID, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            String name = (Data.MusicName + "-" + Data.Singer + ".mp3").replace("\\", "").replace("/", "");
                            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new
                                    DownloadManager.Request(Uri.parse(msg.obj.toString()));
                            request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
                            request.setTitle(name);
                            request.setDescription("小萌音乐正在下载中");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                            MainActivity.sdm("正在下载:" + name, getApplicationContext());
                        }
                    });
                } catch (Exception ignored) {}
            }
        });
        MusicList_list.setAdapter(ad);
        FirstFragment.setListViewHeightBasedOnChildren(MusicList_list);
        MusicList_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != -1) {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = position;
                    Settings.Callback_PlayMusic.sendMessage(message);
                }
            }
        });
        if(isShow)
        MusicListShow();
    }

    public void MusicListShow(){
        if(MusicList_list.getAdapter()!=null) {
            MusicList.setVisibility(View.VISIBLE);
            LyricBack();
            ObjectAnimator animator = ObjectAnimator.ofFloat(MusicList, "translationY", 1200f, 0f);
            animator.setDuration(300);
            animator.start();
        }
    }

    public void MusicListBack() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(MusicList, "translationY", 1200f);
        animator.setDuration(300);
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MusicList.setVisibility(View.GONE);
            }
        }, 300);
    }

    public void MusicAllDownloadOnClick(View view) {
        try {
            for (int index = 0; index != Settings.ListData.Data.size() - 1; ++index) {
                final InfoHelper.Music Data = Settings.ListData.Data.get(index);
                String MusicID = Data.MusicID;
                MusicLib.GetUrl(MusicID, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String name = (Data.MusicName + "-" + Data.Singer + ".mp3").replace("\\", "").replace("/", "");
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new
                                DownloadManager.Request(Uri.parse(msg.obj.toString()));
                        request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
                        request.setTitle(name);
                        request.setDescription("小萌音乐正在下载中");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadManager.enqueue(request);
                        MainActivity.sdm("正在下载:" + name, getApplicationContext());
                    }
                });
            }
        } catch (Exception e) {
        }
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="一些方法">
    @SuppressLint("HandlerLeak")
    public void PlayMusic(final boolean isplay,final int ex) {
        StatService.onEvent(this, "tw_Play", "播放音乐", 1);
        MainActivity.Loading(findViewById(R.id.USERTX));
        if (Settings.ListData.name == "Radio")
            Settings.mSP.putBoolean("isRadio",true);
        else         Settings.mSP.putBoolean("isRadio",false);
        Settings.mSP.putInt("Index",PlayListIndex);
        mHandler.removeCallbacks(r);
        if(isplay) {
            Settings.mp.stop();
            Settings.mp = new MediaPlayer();
        }
        final ImageView PlayBottom_img = findViewById(R.id.PlayBottom_img);
        TextView PlayBottom_title = findViewById(R.id.PlayBottom_title);
        TextView PlayBottom_mss = findViewById(R.id.PlayBottom_mss);
        BitmapUtils bu = new BitmapUtils();
        Handler hl = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bitmap bmp = (Bitmap) msg.obj;
                RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), bmp);
                RBD.setCircular(true);
                ImageView MUSICZJ = findViewById(R.id.MUSICZJ);
                MUSICZJ.setImageDrawable(RBD);
                PlayBottom_img.setImageDrawable(RBD);
                remoteViews.setImageViewBitmap(R.id.Notification_ImageView, bmp);
                RelativeLayout MImageBackground = findViewById(R.id.MImageBackground);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    MImageBackground.setBackground(new BitmapDrawable(BlurBitmap.blur(MainActivity.this, bmp)));
                }
            }
        };
        bu.disPlay(hl, Musicdt.ImageUrl);
        PlayBottom_title.setText(Musicdt.MusicName);
        PlayBottom_mss.setText(Musicdt.Singer);
        MusicLib.GetUrl(Musicdt.MusicID, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if(msg.what!=200){
                        sdm("这首歌还在来的路上哦，已经为你播放下一首(*/ω＼*)",MainActivity.this);
                        Music_Next();
                        return;
                    }
                    String url = msg.obj.toString();
                    Settings.mp.setDataSource(url);
                    Settings.mp.prepare();
                    if(isplay){
                        Settings.mp.start();
                        PlayBottom_ControlBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bom_unplay));
                        MButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_unplaybtn));
                        remoteViews.setImageViewResource(R.id.Notification_OpenBtn, R.drawable.ic_not_stop);
                        isplaying = true;}
                    MusicLib.GetMusicLyric(Musicdt.MusicID, lrcBig);
                    mHandler.postDelayed(r, 1000);
                    TextView Title = findViewById(R.id.MusicTitle);
                    Title.setText(Musicdt.MusicName);
                    TextView Mss = findViewById(R.id.MusicMss);
                    Mss.setText(Musicdt.Singer);
                    remoteViews.setTextViewText(R.id.Notification_SongName, Musicdt.MusicName);
                    remoteViews.setTextViewText(R.id.Notification_Singer, Musicdt.Singer);
                    notificationManager.notify(2, notification);
                    MseekBar.setMax(Settings.mp.getDuration());

                    Settings.mSP.putString("MusicName",Musicdt.MusicName);
                    Settings.mSP.putString("Singer",Musicdt.Singer);
                    Settings.mSP.putString("MusicID",Musicdt.MusicID);
                    Settings.mSP.putString("ImageUrl",Musicdt.ImageUrl);
                    Settings.mSP.putString("GC",Musicdt.GC);
                    Settings.mSP.commit();

                    MseekBar.setProgress(ex);
                    Settings.mp.seekTo(ex);
                } catch (Exception e) { }
            }
        });
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static void sdm(String m, Context co) {
        Toast.makeText(co, m,
                Toast.LENGTH_SHORT).show();
    }

    public static void Loading(View view) {
        Snackbar.make(view, "加载中...", Snackbar.LENGTH_LONG)
                .setAction("Loading...", null).show();
    }

    public static void SendMessageBox(String msg, Context context) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("提示");
        dlg.setMessage(msg);
        dlg.setPositiveButton("确定", null);
        dlg.show();
    }
    //</editor-fold>
}
