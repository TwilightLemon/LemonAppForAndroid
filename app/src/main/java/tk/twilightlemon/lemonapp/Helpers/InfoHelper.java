package tk.twilightlemon.lemonapp.Helpers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import tk.twilightlemon.lemonapp.layouts.KeepliveActivity;
import tk.twilightlemon.lemonapp.layouts.MainActivity;

public class InfoHelper {
    public static class LoginData{
        public String g_tk="";
        public String Cookie="";
        public String qq="";
    }
    public static class AdaptiveData{
        public AdaptiveData(){}
        public String title="";
        public ArrayList<String[]> ChooseData=new ArrayList<>();
        public ArrayList<Handler> ChooseCallBack=new ArrayList<>();
        public ListAdapter CSData;
        public AdapterView.OnItemClickListener ListOnClick;
    }

    //<editor-fold desc="音乐播放&显示列表所用的数据">
    public static class Music {
        public Music() {
        }

        public String MusicName = "";
        public String MusicName_Lyric="";
        public String Singer = "";
        public String MusicID = "";
        public String ImageUrl = "";
        public String GC = "";
    }
    public static class MusicGData {
        public MusicGData() {
        }

        public ArrayList<Music> Data = new ArrayList<>();
        public String name = "";
        public String pic = "";
        public String id = "";
        public String sub = "";

        public boolean IsOwn=false;
        public String dirid="";
        public ArrayList<String> sogids;
    }
    public static class MusicTop {
        public String Name = "";
        public String Photo = "";
        public String ID = "";
        public ArrayList<Music> Data = new ArrayList<>();
    }
    public static class MusicFLGDIndexItemsList {
        public ArrayList<MusicFLGDIndexItems> Data = new ArrayList<MusicFLGDIndexItems>();
    }
    public static class MusicFLGDIndexItems {
        public String name;
        public String id;
    }
    public static class SingerAndRadioData {
        public String name;
        public String url;
        public String id;
    }
    //</editor-fold>

    //<editor-fold desc="广播所使用的Receiver">
    public static class NotificationBCR extends BroadcastReceiver {

        public static final String ACTION_LAST = "ACTION_LAST";
        public static final String ACTION_PRESS = "ACTION_PRESS";
        public static final String ACTION_NEXT="ACTION_NEXT";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Message msg = new Message();
            msg.obj = intent.getAction();
            Settings.ACTIONCALLBACK.sendMessage(msg);
        }
    }
    public static class KeepliveBCR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Intent inten = new Intent(context, KeepliveActivity.class);
                inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(inten);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                context.sendBroadcast(new Intent("finish"));
            }
        }
    }
    //</editor-fold>
}
