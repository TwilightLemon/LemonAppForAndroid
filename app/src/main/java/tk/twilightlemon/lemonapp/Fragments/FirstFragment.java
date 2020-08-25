package tk.twilightlemon.lemonapp.Fragments;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        LoadGDList(lv);
        LoadILikeDissList(slvx);
        SetLikegdList(slvx);
        SetgdList(lv);
        return view;
    }

    //<editor-fold desc="加载数据">

    //获取 我创建的歌单 列表
    @SuppressLint("HandlerLeak")
    public void LoadGDList(final ListView lv) {
        //Step 1: 发送Get请求
            final HashMap<String, String> data = HttpHelper.GetHandler();
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            String json = (String) msg.obj;
                            Log.d("LoadGDList",Settings.qq+ " --- "+Settings.g_tk+" --- "+Settings.Cookie);
                            Log.d("LoadGDList",json);

                            GDdata.clear();
                            try {
                                JSONObject jo = new JSONObject(json);
                                int i = 0;
                                //歌单列表
                                while (i < jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list").length()) {
                                    JSONArray ja = jo.getJSONObject("data").getJSONObject("mydiss").getJSONArray("list");
                                    JSONObject obj = ja.getJSONObject(i);
                                    InfoHelper.MusicGData df = new InfoHelper().new MusicGData();
                                    df.id = obj.getString("dissid");
                                    df.sub = obj.getString("subtitle");
                                    df.name = obj.getString("title");
                                    if (obj.getString("picurl").length() > 10)
                                        df.pic = obj.getString("picurl").replace("http://","https://");
                                    else
                                        df.pic = "https://y.gtimg.cn/mediastyle/global/img/cover_playlist.png?max_age=31536000";
                                    GDdata.add(df);
                                    i++;
                                }
                                //"我喜欢"歌单
                                JSONArray cc=jo.getJSONObject("data").getJSONArray("mymusic");
                                for(int ig=0;ig<cc.length();ig++){
                                    if(cc.getJSONObject(ig).getString("title").equals("我喜欢")){
                                        String id=cc.getJSONObject(ig).getString("id");
                                        InfoHelper.MusicGData df = new InfoHelper().new MusicGData();
                                        df.id = id;
                                        df.sub =cc.getJSONObject(ig).getString("subtitle");
                                        df.name = "我喜欢";
                                        df.pic = "https://y.gtimg.cn/mediastyle/y/img/cover_love_300.jpg";
                                        GDdata.add(df);
                                        break;
                                    }
                                }
                                //添加到listbox适配器
                                GDListAdapter ga = new GDListAdapter(getActivity(), GDdata,getContext());
                                lv.setAdapter(ga);
                                setListViewHeightBasedOnChildren(lv);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }, "https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?loginUin="+Settings.qq+"&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205360838&ct=20&userid="+Settings.qq+"&reqfrom=1&reqtype=0", data);
/////end/////
    }
    //获取 我收藏的歌单 列表
    public void LoadILikeDissList(final ListView lv){
        final HashMap<String, String> data = HttpHelper.GetHandler();
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
                            df.pic = obj.getString("logo").replace("http://","https://");
                        else
                            df.pic = "https://y.gtimg.cn/mediastyle/global/img/cover_playlist.png?max_age=31536000";
                        LikeGDdata.add(df);
                        i++;
                    }
                    GDListAdapter ga = new GDListAdapter(getActivity(), LikeGDdata,getContext());
                    lv.setAdapter(ga);
                    setListViewHeightBasedOnChildren(lv);
                } catch (Exception e) {}
            }
        },"https://c.y.qq.com/fav/fcgi-bin/fcg_get_profile_order_asset.fcg?g_tk="+Settings.g_tk+"&loginUin="+Settings.qq+"&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&ct=20&cid=205360956&userid="+Settings.qq+"&reqtype=3&sin=0&ein=25",data);
    }
    //</editor-fold>

    //<editor-fold desc="初始化控件">

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
