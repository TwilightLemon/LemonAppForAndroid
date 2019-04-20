package tk.twilightlemon.lemonapp.Fragments;
import android.annotation.SuppressLint;
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
    private ArrayList<InfoHelper.MusicGData> GDdata=new ArrayList<>();
    private ArrayList<InfoHelper.MusicGData> LikeGDdata=new ArrayList<>();

    public static Fragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.first_fragment_layout, null);
        ListView lv = view.findViewById(R.id.gd_list);
        ListView slvx = view.findViewById(R.id.likegd_list);
        ListView slv = view.findViewById(R.id.like_list);
        LoadGDList(lv, slv);
        LoadILikeDissList(slvx);
        SetLikeList(slv);
        SetLikegdList(slvx);
        SetgdList(lv);
        return view;
    }

    //<editor-fold desc="加载数据">
    InfoHelper.MusicGData ListData = new InfoHelper().new MusicGData();

    @SuppressLint("HandlerLeak")
    public void LoadGDList(final ListView lv, final ListView slv) {
        final HashMap<String, String> data = new HashMap<String, String>();
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvid=628769750; ptcz=323fab8e686b0804909689c16997ef7ebeb05ad86f35a5f0efbfddb53cfde431; pgv_pvi=578551808; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221680831408845b-04db59e502e9528-552317d-2073600-1680831408a736%22%2C%22%24device_id%22%3A%221680831408845b-04db59e502e9528-552317d-2073600-1680831408a736%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; RK=3LKEPA2s0s; pgv_si=s9201464320; _qpsvr_localtk=0.1869525627964156; uin=o2728578956; skey=@8ymlyzolM; ptisp=cm; luin=o2728578956; lskey=0001000066b9b6a80b3d79d7be9a890bd616cbd1579857cb6c4ebd62a9225286577b131068eec9b96c09e15f; ts_refer=xui.ptlogin2.qq.com/cgi-bin/xlogin; ts_uid=247815470; p_uin=o2728578956; pt4_token=fQqicJ80WCpNDE8jWGkT-RLRy2kTxBbTgfMdmDFH6sE_; p_skey=rOP4nfyjAvXqgMyGGkeYF-nUrw3pVGhcV3Ql14LbkL8_; p_luin=o2728578956; p_lskey=000400000a795e474000b2d7ae15b3f74b87c9db6af1053492f9480344e2fb40f8a31e33b06d8e48386ab6c6; yqq_stat=0");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String json = (String) msg.obj;
                        GDdata.clear();
                        try {
                            JSONObject jo = new JSONObject(json);
                            int i = 0;
                            while (i < jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list").length()) {
                                JSONArray ja = jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list");
                                JSONObject obj = ja.getJSONObject(i);
                                InfoHelper.MusicGData df = new InfoHelper().new MusicGData();
                                df.id = obj.getString("dissid");
                                df.sub = obj.getString("subtitle");
                                df.name = obj.getString("title");
                                if (obj.getString("picurl").length()>10)
                                    df.pic = obj.getString("picurl");
                                else
                                    df.pic = "https://y.gtimg.cn/mediastyle/global/img/cover_playlist.png?max_age=31536000";
                                GDdata.add(df);
                                i++;
                            }
                            GDListAdapter ga = new GDListAdapter(getActivity(), GDdata);
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

    public void LoadILikeDissList(final ListView lv){
        HttpHelper.GetWeb(new Handler(){
            @Override
            public void handleMessage(Message msg){
                try {
                    JSONArray o=new JSONObject(msg.obj.toString()).getJSONObject("data").getJSONArray("cdlist");
                    LikeGDdata.clear();
                    int i = 0;
                    while (i < o.length()) {
                        JSONObject obj = o.getJSONObject(i);
                        InfoHelper.MusicGData df = new InfoHelper().new MusicGData();
                        df.id = obj.getString("dissid");
                        df.sub ="创建者:"+obj.getString("nickname");
                        df.name = obj.getString("dissname");
                        if (obj.getString("logo").length()>10)
                            df.pic = obj.getString("logo");
                        else
                            df.pic = "https://y.gtimg.cn/mediastyle/global/img/cover_playlist.png?max_age=31536000";
                        LikeGDdata.add(df);
                        i++;
                    }
                    GDListAdapter ga = new GDListAdapter(getActivity(), LikeGDdata);
                    lv.setAdapter(ga);
                    setListViewHeightBasedOnChildren(lv);
                } catch (Exception e) {}
            }
        },"https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg?g_tk=1470138129&loginUin="+Settings.qq+"&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&ct=20&cid=205360956&userid="+Settings.qq+"&reqtype=3&sin=0&ein=25",null);
    }

    @SuppressLint("HandlerLeak")
    public void LoadLikeList(String id, final ListView lv) {
        final HashMap<String, String> data = new HashMap<String, String>();
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvid=628769750; ptcz=323fab8e686b0804909689c16997ef7ebeb05ad86f35a5f0efbfddb53cfde431; pgv_pvi=578551808; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221680831408845b-04db59e502e9528-552317d-2073600-1680831408a736%22%2C%22%24device_id%22%3A%221680831408845b-04db59e502e9528-552317d-2073600-1680831408a736%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; RK=3LKEPA2s0s; pgv_si=s9201464320; _qpsvr_localtk=0.1869525627964156; uin=o2728578956; skey=@8ymlyzolM; ptisp=cm; luin=o2728578956; lskey=0001000066b9b6a80b3d79d7be9a890bd616cbd1579857cb6c4ebd62a9225286577b131068eec9b96c09e15f; ts_refer=xui.ptlogin2.qq.com/cgi-bin/xlogin; ts_uid=247815470; p_uin=o2728578956; pt4_token=fQqicJ80WCpNDE8jWGkT-RLRy2kTxBbTgfMdmDFH6sE_; p_skey=rOP4nfyjAvXqgMyGGkeYF-nUrw3pVGhcV3Ql14LbkL8_; p_luin=o2728578956; p_lskey=000400000a795e474000b2d7ae15b3f74b87c9db6af1053492f9480344e2fb40f8a31e33b06d8e48386ab6c6; yqq_stat=0");
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
        }, "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=" + id + "&format=json&g_tk=914750006&loginUin={qq}&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0".replace("{qq}", Settings.qq), data);
   ////END////
    }
    //</editor-fold>

    //<editor-fold desc="初始化控件">
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
                MusicLib.GetGDbyID(GDdata.get(position), getActivity(),true);
            }
        });
    }

    public void SetLikegdList(ListView lv) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                MusicLib.GetGDbyID(LikeGDdata.get(position), getActivity(),true);
            }
        });
    }
    //</editor-fold>

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
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
}
