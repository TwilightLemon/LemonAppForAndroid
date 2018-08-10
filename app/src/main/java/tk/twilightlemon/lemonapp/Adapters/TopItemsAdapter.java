package tk.twilightlemon.lemonapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tk.twilightlemon.lemonapp.Helpers.HttpHelper;
import tk.twilightlemon.lemonapp.Helpers.Image.BitmapUtils;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Helpers.Settings;

///items_top in java/SecondPage 的适配器
public class TopItemsAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<InfoHelper.MusicTop> MData = null;
    private HashMap<String, InfoHelper.MusicTop> Mdata = new HashMap<>();

    public TopItemsAdapter(Activity a, ArrayList<InfoHelper.MusicTop> data) {
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MData = data;
    }

    public HashMap<String, InfoHelper.MusicTop> getMdata() {
        return Mdata;
    }

    public int getCount() {
        return MData.size();
    }

    public View getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    private View makeItemView(final InfoHelper.MusicTop data) {
        final View itemView = inflater.inflate(R.layout.items_top, null);
        TextView tit = itemView.findViewById(R.id.top_title);
        tit.setText(data.Name);
        if (data.Data.size() == 0) {
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        super.handleMessage(msg);
                        JSONObject o = new JSONObject(msg.obj.toString());
                        ArrayList<InfoHelper.Music> dat = new ArrayList<>();
                        for (int i = 0; i < o.getJSONArray("songlist").length(); ++i) {
                            JSONObject dt = o.getJSONArray("songlist").getJSONObject(i).getJSONObject("data");
                            InfoHelper.Music m = new InfoHelper().new Music();
                            m.MusicName = dt.getString("songname").replace("\\", "-").replace("?", "").replace("/", "").replace(":", "").replace("*", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
                            String Singer = "";
                            for (int osxc = 0; osxc != dt.getJSONArray("singer").length(); osxc++) {
                                Singer += dt.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                            }
                            m.Singer = Singer.substring(0, Singer.lastIndexOf("&"));
                            m.MusicID = dt.getString("songmid");
                            m.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + dt.getString("albummid") + ".jpg";
                            m.GC = dt.getString("songmid");
                            dat.add(m);
                        }
                        data.Data = dat;
                        TextView text = itemView.findViewById(R.id.top_item1);
                        text.setText(data.Data.get(0).MusicName + " - " + data.Data.get(0).Singer);
                        TextView text2 = itemView.findViewById(R.id.top_item2);
                        text2.setText(data.Data.get(1).MusicName + " - " + data.Data.get(1).Singer);
                        TextView text3 = itemView.findViewById(R.id.top_item3);
                        text3.setText(data.Data.get(2).MusicName + " - " + data.Data.get(2).Singer);
                        Mdata.put(data.ID, data);
                    } catch (Exception e) {
                    }
                }
            }, "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&topid=" + data.ID + "&type=top&song_begin=0&song_num=30&g_tk=1206122277&loginUin=" + Settings.qq + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", null);
        }
        final ImageView img = itemView.findViewById(R.id.top_image);
        BitmapUtils bu=new BitmapUtils();
        Handler hl=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                img.setImageBitmap((Bitmap) msg.obj);
            }
        };
        bu.disPlay(hl,data.Photo);
        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return makeItemView(MData.get(position));
        return convertView;
    }
}
