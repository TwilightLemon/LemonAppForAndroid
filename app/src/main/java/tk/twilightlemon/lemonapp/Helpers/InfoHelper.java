package tk.twilightlemon.lemonapp.Helpers;
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class InfoHelper {
    public class AdaptiveData{
        public AdaptiveData(){}
        public String title="";
        public ArrayList<String[]> ChooseData=new ArrayList<>();
        public ArrayList<Handler> ChooseCallBack=new ArrayList<>();
        public ListAdapter CSData;
        public AdapterView.OnItemClickListener ListOnClick;
    }
    ///音乐播放信息
    public class Music {
        public Music() {
        }

        public String MusicName = "";
        public String Singer = "";
        public String MusicID = "";
        public String ImageUrl = "";
        public String GC = "";
    }

    ///歌单列表数据
    public class MusicGData {
        public MusicGData() {
        }

        public ArrayList<Music> Data = new ArrayList<>();
        public String name = "";
        public String pic = "";
        public String id = "";
        public String sub = "";
    }

    ///排行版列表数据
    public class MusicTop {
        public String Name = "";
        public String Photo = "";
        public String ID = "";
        public ArrayList<Music> Data = new ArrayList<>();
    }

    ///分类歌单ID
    public class MusicFLGDIndexItemsList {
        public ArrayList<MusicFLGDIndexItems> Data = new ArrayList<MusicFLGDIndexItems>();
    }

    public class MusicFLGDIndexItems {
        public String name;
        public String id;
    }

    public class SingerAndRadioData {
        public String name;
        public String url;
        public String id;
    }

    public class NotificationBCR extends BroadcastReceiver {

        public static final String ACTION_LAST = "ACTION_LAST";
        public static final String ACTION_PRESS = "ACTION_PRESS";
        public static final String ACTION_NEXT="ACTION_NEXT";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Message msg=new Message();
            msg.obj=intent.getAction();
            Settings.ACTIONCALLBACK.sendMessage(msg);
        }
    }
}
