package tk.twilightlemon.lemonapp;
/**
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 * O\  =  /O
 * ____/`---'\____
 * .'  \\|     |//  `.
 * /  \\|||  :  |||//  \
 * /  _||||| -:- |||||-  \
 * |   | \\\  -  /// |   |
 * | \_|  ''\---/''  |   |
 * \  .-\__  `-`  ___/-. /
 * ___`. .'  /--.--\  `. . __
 * ."" '<  `.___\_<|>_/___.'  >'"".
 * | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 佛祖保佑        永无BUG
 */

import android.os.Handler;
import android.widget.AdapterView;

import java.util.ArrayList;

public class InfoHelper {
    public class AdaptiveData{
        public AdaptiveData(){}

        public String title="";
        public String[] ChooseData;
        public Handler ChooseCallBack;
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
}
