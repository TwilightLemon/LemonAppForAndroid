package tk.twilightlemon.lemonapp.layouts;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tk.twilightlemon.lemonapp.Adapters.MyFragmentAdapter;
import tk.twilightlemon.lemonapp.Helpers.Image.BitmapUtils;
import tk.twilightlemon.lemonapp.Helpers.Image.BlurBitmap;
import tk.twilightlemon.lemonapp.Fragments.FirstFragment;
import tk.twilightlemon.lemonapp.Helpers.HttpHelper;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcView;
import tk.twilightlemon.lemonapp.Helpers.MusicLib;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Fragments.SecondFragment;
import tk.twilightlemon.lemonapp.Helpers.Settings;

public class MainActivity extends AppCompatActivity {
    public static String SDPATH = "";
    MediaPlayer mp = new MediaPlayer();
    LrcView lrcBig = null;
    boolean isplaying = false;
    int PlayListIndex = -1;
    InfoHelper.Music Musicdt = null;
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
///更新播放进度
            SeekBar MseekBar = findViewById(R.id.MusicSeek);
            int in = mp.getCurrentPosition();
            MseekBar.setProgress(in);
            MseekBar.setMax(mp.getDuration());
            if (findViewById(R.id.LyricView).getVisibility() == View.VISIBLE)
                lrcBig.updateTime(in);
            if (isplaying && MseekBar.getProgress() + 2000 >= MseekBar.getMax()) {
                mHandler.removeCallbacks(r);
                MseekBar.setProgress(0);
                if (xhindex == 0) {
                    if(Settings.ListData.name=="Radio"){
                        MusicLib.GetRadioMusicById(Settings.ListData.id, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                PlayListIndex = 0;
                                InfoHelper.MusicGData GData = new InfoHelper().new MusicGData();
                                GData.Data.add((InfoHelper.Music) msg.obj);
                                GData.name="Radio";
                                GData.id=Settings.ListData.id;
                                Settings.ListData = GData;
                                Musicdt = Settings.ListData.Data.get(PlayListIndex);
                                PlayMusic();
                            }
                        });
                    }else {
                        if (PlayListIndex == Settings.ListData.Data.size() - 1)
                            PlayListIndex = 0;
                        else ++PlayListIndex;
                        Musicdt = Settings.ListData.Data.get(PlayListIndex);
                        PlayMusic();
                    }
                }else{
                    Musicdt = Settings.ListData.Data.get(PlayListIndex);
                    PlayMusic();
                }
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
        MainActivity.Loading(findViewById(R.id.USERTX));
        LoadMusicControls();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    int xhindex = 0;
    int REQUEST_STORAGE_PERMISSION = 1;

    public void PlayMusic() {
        MainActivity.Loading(findViewById(R.id.USERTX));
        mp.stop();
        mHandler.removeCallbacks(r);
        mp = new MediaPlayer();
        isplaying = true;
        lrcBig.updateTime(0);
        final ImageView PlayBottom_img = findViewById(R.id.PlayBottom_img);
        TextView PlayBottom_title = findViewById(R.id.PlayBottom_title);
        TextView PlayBottom_mss = findViewById(R.id.PlayBottom_mss);
        ImageButton PlayBottom_ControlBtn = findViewById(R.id.PlayBottom_ControlBtn);
        Drawable ic = getResources().getDrawable(R.drawable.ic_unplaybtn);
        PlayBottom_ControlBtn.setImageDrawable(ic);
        ImageButton MButton = findViewById(R.id.MButton);
        MButton.setImageDrawable(ic);
        BitmapUtils bu=new BitmapUtils();
        Handler hl=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bitmap bmp = (Bitmap) msg.obj;
                RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), bmp);
                RBD.setCircular(true);
                ImageView MUSICZJ = findViewById(R.id.MUSICZJ);
                MUSICZJ.setImageDrawable(RBD);
                PlayBottom_img.setImageDrawable(RBD);
                RelativeLayout MImageBackground = findViewById(R.id.MImageBackground);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    MImageBackground.setBackground(new BitmapDrawable(BlurBitmap.blur(MainActivity.this, bmp)));
                }
            }
        };
        bu.disPlay(hl,Musicdt.ImageUrl);
        PlayBottom_title.setText(Musicdt.MusicName);
        PlayBottom_mss.setText(Musicdt.Singer);
        MusicLib.GetUrl(Musicdt.MusicID, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    String url = msg.obj.toString();
                    mp.setDataSource(url);
                    mp.prepare();
                    mp.start();
                    MusicLib.GetMusicLyric(Musicdt.MusicID, lrcBig);
                    mHandler.postDelayed(r, 1000);
                    TextView Title = findViewById(R.id.MusicTitle);
                    Title.setText(Musicdt.MusicName);
                    TextView Mss = findViewById(R.id.MusicMss);
                    Mss.setText(Musicdt.Singer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
///////配置标题栏
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
            BitmapUtils bu=new BitmapUtils();
            Handler hl=new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                    RBD.setCircular(true);
                    ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                }
            };
            bu.disPlay(hl,"http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100");
            Settings.qq = nu;
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
                            final String nu = qq.getText().toString();
                            BitmapUtils bu=new BitmapUtils();
                            Handler hl=new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(), (Bitmap) msg.obj);
                                    RBD.setCircular(true);
                                    ((ImageView) findViewById(R.id.USERTX)).setImageDrawable(RBD);
                                }
                            };
                            bu.disPlay(hl,"http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin=" + nu + "&spec=100");
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
                                            restartApplication();
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
///播放回调
        Settings.Callback_PlayMusic = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (((int) msg.obj) != -1) {
//TODO:此处处理由MusicListPage传来的播放事件
                        PlayListIndex = (int) msg.obj;
                        Musicdt = Settings.ListData.Data.get(PlayListIndex);
                        PlayMusic();
                    }
                } catch (Exception e) {
                }
            }
        };
///一些控件
        lrcBig = findViewById(R.id.lrc);
        final ImageButton PlayBottom_ControlBtn = findViewById(R.id.PlayBottom_ControlBtn);
        final ImageButton MButton = findViewById(R.id.MButton);
        Drawable playic = getResources().getDrawable(R.drawable.ic_playbtn);
        PlayBottom_ControlBtn.setImageDrawable(playic);
        MButton.setImageDrawable(playic);
        View.OnClickListener lister = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable ic = null;
                if (isplaying) {
                    ic = getResources().getDrawable(R.drawable.ic_playbtn);
                    isplaying = false;
                    mp.pause();
                } else {
                    ic = getResources().getDrawable(R.drawable.ic_unplaybtn);
                    isplaying = true;
                    mp.start();
                }
                PlayBottom_ControlBtn.setImageDrawable(ic);
                MButton.setImageDrawable(ic);
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
                mp.seekTo(MseekBar.getProgress());
                isChanging[0] = false;
            }
        });
        findViewById(R.id.PlayBottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View s = findViewById(R.id.LyricView);
                s.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(s, "translationY", 1200f, 0f);
                animator.setDuration(200);
                animator.start();
                lrcBig.initEntryList();
                lrcBig.initNextTime();
            }
        });
        findViewById(R.id.M_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View s = findViewById(R.id.LyricView);
                ObjectAnimator animator = ObjectAnimator.ofFloat(s, "translationY", 1200f);
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
                if(Settings.ListData.name!="Radio") {
                    if (PlayListIndex == 0)
                        PlayListIndex = Settings.ListData.Data.size() - 1;
                    else --PlayListIndex;
                    Musicdt = Settings.ListData.Data.get(PlayListIndex);
                    PlayMusic();
                }
            }
        });
        findViewById(R.id.musicnext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Settings.ListData.name=="Radio") {
                    MusicLib.GetRadioMusicById(Settings.ListData.id, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            PlayListIndex = 0;
                            InfoHelper.MusicGData GData = new InfoHelper().new MusicGData();
                            GData.Data.add((InfoHelper.Music) msg.obj);
                            GData.name="Radio";
                            GData.id=Settings.ListData.id;
                            Settings.ListData = GData;
                            Musicdt = Settings.ListData.Data.get(PlayListIndex);
                            PlayMusic();
                        }
                    });
                }else {
                    if (PlayListIndex == Settings.ListData.Data.size() - 1)
                        PlayListIndex = 0;
                    else ++PlayListIndex;
                    Musicdt = Settings.ListData.Data.get(PlayListIndex);
                    PlayMusic();
                }
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
    }

    public void Lv() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    public void OnShareClick(View view) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "小萌音乐分享");
        share_intent.putExtra(Intent.EXTRA_TEXT, Musicdt.MusicName + " - " + Musicdt.Singer + ": http://suo.im/api.php?url=https://i.y.qq.com/v8/playsong.html?songmid=" + Musicdt.MusicID);
//创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "小萌音乐分享");
        startActivity(share_intent);
    }

    ///////加载区end//////
//////功能区//////
    public static String FindByAb(String all, String a, String b) {
        return all.substring(all.indexOf(a) + a.length(), all.indexOf(b));
    }

    public static void sdm(String m, Context co) {
        Toast.makeText(co, m,
                Toast.LENGTH_SHORT).show();
    }

    public static void Loading(View view) {
        Snackbar.make(view, "加载中...", Snackbar.LENGTH_LONG)
                .setAction("Loading...", null).show();
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
//////功能区end////
}