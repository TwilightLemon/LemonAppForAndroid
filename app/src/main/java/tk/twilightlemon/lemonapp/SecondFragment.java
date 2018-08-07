package tk.twilightlemon.lemonapp;
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
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

//layout/second_fragment_layout.xml的交互逻辑
public class SecondFragment extends Fragment {

     public static Fragment newInstance(){
         SecondFragment fragment = new SecondFragment();
         return fragment;
     }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment_layout,null);
        LoadTopList((ListView) view.findViewById(R.id.Top_list));
        LoadSearchTab(view);
        return view;
    }
    public void LoadSearchTab(View view){
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText qq = new EditText(getContext());
                qq.setHint("搜索");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("搜索").setView(qq)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                        String url="http://59.37.96.220/soso/fcgi-bin/client_search_cp?format=json&t=0&inCharset=GB2312&outCharset=utf-8&qqmusic_ver=1302&catZhida=0&p=1&n=20&w="+ URLEncoder.encode(qq.getText().toString(), "utf-8")+"&flag_qc=0&remoteplace=sizer.newclient.song&new_json=1&lossless=0&aggr=1&cr=1&sem=0&force_zonghe=0";
                        HttpHelper.GetWeb(new Handler(){
                            @Override
                            public void handleMessage(Message msg){
                                try{
                                    String json = msg.obj.toString().replace("<em>","").replace("</em>","");
                                    JSONObject jo = new JSONObject(json);
                                    InfoHelper.MusicGData Data=new InfoHelper().new MusicGData();
                                    Data.name="搜索:"+qq.getText();
                                    for (int i=0;i < jo.getJSONObject("data").getJSONObject("song").getJSONArray("list") .length();++i)
                                    {
                                        InfoHelper.Music dt=new InfoHelper().new Music();
                                        JSONObject jos=jo.getJSONObject("data").getJSONObject("song").getJSONArray("list").getJSONObject(i);
                                        dt.MusicName=jos.getString("name");
                                        String isx="";
                                        for(int ix=0;ix!=jos.getJSONArray("singer").length();ix++){
                                            isx+=jos.getJSONArray("singer").getJSONObject(ix).getString("name")+"&";
                                        }
                                        dt.Singer=isx.substring(0, isx.lastIndexOf("&"));
                                        dt.MusicID=jos.getString("mid");
                                        dt.ImageUrl="http://y.gtimg.cn/music/photo_new/T002R300x300M000"+jos.getJSONObject("album").getString("mid")+".jpg";
                                        dt.GC=jos.getJSONObject("action").getString("alert");
                                        Data.Data.add(dt);
                                    }
                                    Settings.ListData=Data;
                                    Intent intent = new Intent(getActivity(), MusicListPage.class);
                                    getActivity().startActivityForResult(intent, 1000);
                                }catch(Exception e){}
                            }
                        },url,null);
                    }catch(Exception e){}}});
                builder.show();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    ArrayList<String> Top_idList=new ArrayList<>();
    public void LoadTopList(final ListView lv){
         HttpHelper.GetWeb(new Handler() {
             @Override
             public void handleMessage(Message msg) {
                 super.handleMessage(msg);
                 try {
                     String jsondata = "{\"data\":" + msg.obj.toString().replace("jsonCallback(", "").replace("}]\n)", "") + "}]" + "}";
                     JSONObject o = new JSONObject(jsondata);
                     ArrayList<InfoHelper.MusicTop> data = new ArrayList<>();
                     for (int dat = 0; dat < o.getJSONArray("data").length(); ++dat) {
                         for (int i = 0; i < o.getJSONArray("data").getJSONObject(dat).getJSONArray("List").length(); ++i) {
                             JSONObject json = o.getJSONArray("data").getJSONObject(dat).getJSONArray("List").getJSONObject(i);
                             InfoHelper.MusicTop dt = new InfoHelper().new MusicTop();
                             dt.Name = json.getString("ListName");
                             if(dt.Name.contains("MV"))//排除MV榜
                                 continue;
                             dt.ID = json.getString("topID");
                             dt.Photo = json.getString("pic_v12");
                             data.add(dt);
                             Top_idList.add(dt.ID);
                         }
                     }
                     TopItemsAdapter tia=new TopItemsAdapter(getActivity(),data);
                     lv.setAdapter(tia);
                     FirstFragment.setListViewHeightBasedOnChildren(lv);
                 }catch (Exception e){}
             }
         },"https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_opt.fcg?page=index&format=html&tpl=macv4&v8debug=1",null);

         lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 InfoHelper.MusicGData dt= new InfoHelper().new MusicGData();
                 InfoHelper.MusicTop dat=((TopItemsAdapter)lv.getAdapter()).getMdata().get(Top_idList.get(i));
                 dt.Data=dat.Data;
                 dt.name=dat.Name;
                 Settings.ListData =dt;
                 Intent intent = new Intent(getActivity(), MusicListPage.class);
                 getActivity().startActivityForResult(intent, 1000);
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
}
