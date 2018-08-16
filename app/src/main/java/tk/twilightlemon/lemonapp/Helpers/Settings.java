package tk.twilightlemon.lemonapp.Helpers;
import android.media.MediaPlayer;
import android.os.Handler;

import java.util.ArrayList;

public class Settings {
    //<editor-fold desc="一些常量">
    public static String qq="";
    public static boolean isActive=true;
    public static MediaPlayer mp = new MediaPlayer();
    //</editor-fold>

    //<editor-fold desc="一些跳转数据">
    public static InfoHelper.MusicGData ListData=new InfoHelper().new MusicGData();
    public static InfoHelper.AdaptiveData AdapData=new InfoHelper().new AdaptiveData();
    //</editor-fold>

    //<editor-fold desc="一些回调事件">
    public static Handler Callback_PlayMusic=null;
    public static Handler Callback_Close=null;
    public static Handler ACTIONCALLBACK=null;
    //</editor-fold>
}
