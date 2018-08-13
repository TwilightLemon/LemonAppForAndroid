package tk.twilightlemon.lemonapp.Fragments;
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
 * <p>
 * *************************************************************
 * *
 * .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 * |                     ______                     |      *
 * |                  .-"      "-.                  |      *
 * |                 /            \                 |      *
 * |     _          |              |          _     |      *
 * |    ( \         |,  .-.  .-.  ,|         / )    |      *
 * |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 * |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 * |           "=._"(_     ^^     _)"_.="           |      *
 * |               "=\__|IIIIII|__/="               |      *
 * |              _.="| \IIIIII/ |"=._              |      *
 * |    _     _.="_.="\          /"=._"=._     _    |      *
 * |   ( \_.="_.="     `--------`     "=._"=._/ )   |      *
 * |    > _.="                            "=._ <    |      *
 * |   (_/                                    \_)   |      *
 * |                                                |      *
 * '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 * *
 * NO BUG!                           *
 * *
 * *************************************************************
 */
/**
 **************************************************************
 *                                                            *
 *   .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 *    |                     ______                     |      *
 *    |                  .-"      "-.                  |      *
 *    |                 /            \                 |      *
 *    |     _          |              |          _     |      *
 *    |    ( \         |,  .-.  .-.  ,|         / )    |      *
 *    |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 *    |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 *    |           "=._"(_     ^^     _)"_.="           |      *
 *    |               "=\__|IIIIII|__/="               |      *
 *    |              _.="| \IIIIII/ |"=._              |      *
 *    |    _     _.="_.="\          /"=._"=._     _    |      *
 *    |   ( \_.="_.="     `--------`     "=._"=._/ )   |      *
 *    |    > _.="                            "=._ <    |      *
 *    |   (_/                                    \_)   |      *
 *    |                                                |      *
 *    '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 *                                                            *
 *                          NO BUG!                           *
 *                                                            *
 **************************************************************
 */

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tk.twilightlemon.lemonapp.Adapters.GDListAdapter;
import tk.twilightlemon.lemonapp.Adapters.MusicListAdapter;
import tk.twilightlemon.lemonapp.Helpers.HttpHelper;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.Helpers.MusicLib;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Helpers.Settings;
import tk.twilightlemon.lemonapp.layouts.MainActivity;

import static android.content.Context.DOWNLOAD_SERVICE;

//layout/first_fragment_layout.xml的交互逻辑
public class FirstFragment extends Fragment {
    public static Fragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_fragment_layout, null);
        ListView lv = view.findViewById(R.id.gd_list);
        ListView slv = view.findViewById(R.id.like_list);
        LoadGDList(lv, slv);
        SetLikeList(slv);
        SetgdList(lv);
        return view;
    }

    InfoHelper.MusicGData ListData = new InfoHelper().new MusicGData();

    public void LoadGDList(final ListView lv, final ListView slv) {
        final HashMap<String, String> data = new HashMap<String, String>();/////start   加载歌单列表
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvi=9798155264; RK=JKKMei2V0M; ptcz=f60f58ab93a9b59848deb2d67b6a7a4302dd1208664e448f939ed122c015d8d1; pgv_pvid=4173718307; ts_uid=5327745136; ts_uid=5327745136; pt2gguin=o2728578956; ts_refer=xui.ptlogin2.qq.com/cgi-bin/xlogin; yq_index=0; o_cookie=2728578956; pac_uid=1_2728578956; pgv_info=ssid=s8910034002; pgv_si=s3134809088; _qpsvr_localtk=0.8145813010716534; uin=o2728578956; skey=@ZF3GfLQsE; ptisp=ctc; luin=o2728578956; lskey=00010000c504a12a536ab915ce52f0ba2a3d24042adcea8e3b78ef55972477fd6d67417e4fc27cdaa8a0bd86; p_uin=o2728578956; pt4_token=YoecK598VtlFoQ7Teus8nC51UayhpD9rfitjZ6BMUkc_; p_skey=SFU7-V*Vwn3XsXtF3MF4T2OAOBbSp96ol-zzMbhcCzM_; p_luin=o2728578956; p_lskey=00040000768e027ce038844edbd57908c83024d365b4a86c9c12cf8b979d473a573567e70c30bd779d5f20cd; yqq_stat=0");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String json = (String) msg.obj;
                        Settings.data.clear();
                        try {
                            JSONObject jo = new JSONObject(json);
////加载歌单列表
                            ArrayList<InfoHelper.MusicGData> data = new ArrayList<InfoHelper.MusicGData>();
                            int i = 0;
                            while (i < jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list").length()) {
                                JSONArray ja = jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list");
                                JSONObject obj = ja.getJSONObject(i);
                                InfoHelper.MusicGData df = new InfoHelper().new MusicGData();
                                df.id = obj.getString("dissid");
                                df.sub = obj.getString("subtitle");
                                df.name = obj.getString("title");
                                if (obj.getString("picurl") != "")
                                    df.pic = obj.getString("picurl");
                                else
                                    df.pic = "https://y.gtimg.cn/mediastyle/global/img/cover_playlist.png?max_age=31536000";
                                Settings.data.add(df);
                                data.add(df);
                                i++;
                            }
                            GDListAdapter ga = new GDListAdapter(getActivity(), data);
                            lv.setAdapter(ga);
                            setListViewHeightBasedOnChildren(lv);
                            LoadLikeList(jo.getJSONObject("data").getJSONArray("mymusic").getJSONObject(0).getString("id"), slv);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }, "https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?loginUin={qq}&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205360838&ct=20&userid={qq}&reqfrom=1&reqtype=0".replace("{qq}", Settings.qq), data);
/////end/////
    }

    public void LoadLikeList(String id, final ListView lv) {
        final HashMap<String, String> data = new HashMap<String, String>();/////start   加载歌单列表
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvi=1693112320; RK=DKOGai2+wu; pgv_pvid=1804673584; ptcz=3a23e0a915ddf05c5addbede97812033b60be2a192f7c3ecb41aa0d60912ff26; pgv_si=s4366031872; _qpsvr_localtk=0.3782697029073365; ptisp=ctc; luin=o2728578956; lskey=00010000863c7a430b79e2cf0263ff24a1e97b0694ad14fcee720a1dc16ccba0717d728d32fcadda6c1109ff; pt2gguin=o2728578956; uin=o2728578956; skey=@PjlklcXgw; p_uin=o2728578956; p_skey=ROnI4JEkWgKYtgppi3CnVTETY3aHAIes-2eDPfGQcVg_; pt4_token=wC-2b7WFwI*8aKZBjbBb7f4Am4rskj11MmN7bvuacJQ_; p_luin=o2728578956; p_lskey=00040000e56d131f47948fb5a2bec49de6174d7938c2eb45cb224af316b053543412fd9393f83ee26a451e15; ts_refer=ui.ptlogin2.qq.com/cgi-bin/login; ts_last=y.qq.com/n/yqq/playlist/2591355982.html; ts_uid=1420532256; yqq_stat=0");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String json = (String) msg.obj;
                        try {
                            JSONObject jo = new JSONObject(json);
                            int i = 0;
                            while (i < jo.getJSONArray("cdlist").getJSONObject(0).getJSONArray("songlist").length()) {
                                JSONArray ja = jo.getJSONArray("cdlist").getJSONObject(0).getJSONArray("songlist");
                                JSONObject obj = ja.getJSONObject(i);
                                InfoHelper.Music md = new InfoHelper().new Music();
                                md.MusicName = obj.getString("songname");
                                String Singer = "";
                                for (int osxc = 0; osxc != obj.getJSONArray("singer").length(); ++osxc) {
                                    Singer += obj.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                                }
                                md.Singer = Singer.substring(0, Singer.lastIndexOf("&"));
                                md.GC = obj.getString("songid");
                                try {
                                    md.MusicID = obj.getString("songmid");
                                    md.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + obj.getString("albummid") + ".jpg";
                                    ListData.Data.add(md);
                                } catch (Exception e) {
                                }
                                i++;
                            }
                            MusicListAdapter ad = new MusicListAdapter(getActivity(), ListData, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        RelativeLayout PATENT = (RelativeLayout) view.getParent();
                                        int index = Integer.parseInt(((TextView) PATENT.findViewById(R.id.MusicList_index)).getText().toString());
                                        final InfoHelper.Music Data = ListData.Data.get(index);
                                        String MusicID = Data.MusicID;
                                        MusicLib.GetUrl(MusicID, new Handler() {
                                            @Override
                                            public void handleMessage(Message msg) {
                                                String name = (Data.MusicName + "-" + Data.Singer + ".mp3").replace("\\", "").replace("/", "");
                                                DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                                DownloadManager.Request request = new
                                                        DownloadManager.Request(Uri.parse(msg.obj.toString()));
                                                request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
                                                request.setTitle(name);
                                                request.setDescription("小萌音乐正在下载中");
                                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                downloadManager.enqueue(request);
                                                MainActivity.sdm("正在下载:" + name, getActivity());
                                            }
                                        });
                                    } catch (Exception e) {
                                    }
                                }
                            });
                            lv.setAdapter(ad);
                            setListViewHeightBasedOnChildren(lv);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }, "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=" + id + "&format=json&g_tk=1157737156&loginUin={qq}&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0".replace("{qq}", Settings.qq), data);
    }

    public void SetLikeList(ListView slv) {
        slv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != -1) {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = position;
                    Settings.ListData = ListData;
                    Settings.Callback_PlayMusic.sendMessage(message);
                }
            }
        });
    }

    public void SetgdList(ListView lv) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                MusicLib.GetGDbyID(Settings.data.get(position), getActivity());
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
//获取ListView对应的Adapter
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
            return;
        }
        int totalHeight = 0;
        if(listAdapter.getCount()!=0) {
            View listItem = listAdapter.getView(0, null, listView);
            for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
