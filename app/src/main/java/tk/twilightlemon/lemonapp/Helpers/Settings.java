package tk.twilightlemon.lemonapp.Helpers;
/*
*                     L:Settings：
*                         #mSP.putString("MusicName",Musicdt.MusicName);
                          #mSP.putString("Singer",Musicdt.Singer);
                          #mSP.putString("MusicID",Musicdt.MusicID);            保存临时MainActivity.Musicdt数据
                          #mSP.putString("ImageUrl",Musicdt.ImageUrl);
                          #mSP.putString("GC",Musicdt.GC);
                      L:Info:
                          #Settings.ListData  #. Search_'Base64Code'
                                              #. Diss_id
                                              #. Radio_id
                          #Index  in#Settings.ListData
                          #isRadio
* */

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
public class Settings {
    //<editor-fold desc="一些常量">
    public static String qq="";
    public static String Cookie="";
    public static String g_tk="";

    public static boolean isActive=true;
    public static MediaPlayer mp = new MediaPlayer();

    public static SharedPreferences.Editor mSP = null;

    public static String ModeID="";
    //</editor-fold>

    //<editor-fold desc="一些跳转数据">
    public static InfoHelper.MusicGData ListData=new InfoHelper().new MusicGData();
    public static InfoHelper.AdaptiveData AdapData=new InfoHelper().new AdaptiveData();
    //</editor-fold>

    //<editor-fold desc="一些回调事件">
    public static Handler Callback_PlayMusic=null;
    public static Handler Callback_DownloadMusic=null;
    public static Handler Callback_Close=null;
    public static Handler ACTIONCALLBACK=null;
    //</editor-fold>
}
