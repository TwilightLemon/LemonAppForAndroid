package tk.twilightlemon.lemonapp.Adapters;

import android.annotation.SuppressLint;
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
import tk.twilightlemon.lemonapp.Helpers.MusicLib;
import tk.twilightlemon.lemonapp.R;
import tk.twilightlemon.lemonapp.Helpers.Settings;

///items_top in java/SecondPage 的适配器
public class TopItemsAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private ArrayList<InfoHelper.MusicTop> MData = null;
    private HashMap<String, InfoHelper.MusicTop> Mdata = new HashMap<>();
    private Context context;
    public TopItemsAdapter(Activity a, ArrayList<InfoHelper.MusicTop> data,Context context) {
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MData = data;
        this.context=context;
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

    @SuppressLint("HandlerLeak")
    private View makeItemView(final InfoHelper.MusicTop data) {
        final View itemView = inflater.inflate(R.layout.items_top, null);
        TextView tit = itemView.findViewById(R.id.top_title);
        tit.setText(data.Name);
        if (data.Data.size() == 0) {
            //<editor-fold desc="Get Top MusicList By Id">
            MusicLib.GetTopDataById(data.ID,new Handler(){
                @Override
                public void handleMessage(Message msg){
                    data.Data = (ArrayList<InfoHelper.Music>) msg.obj;
                    TextView text = itemView.findViewById(R.id.top_item1);
                    text.setText(data.Data.get(0).MusicName + " - " + data.Data.get(0).Singer);
                    TextView text2 = itemView.findViewById(R.id.top_item2);
                    text2.setText(data.Data.get(1).MusicName + " - " + data.Data.get(1).Singer);
                    TextView text3 = itemView.findViewById(R.id.top_item3);
                    text3.setText(data.Data.get(2).MusicName + " - " + data.Data.get(2).Singer);
                    Mdata.put(data.ID, data);
                }
            });
            //</editor-fold>
        }
        final ImageView img = itemView.findViewById(R.id.top_image);
        BitmapUtils bu=new BitmapUtils();
        Handler hl=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                img.setImageBitmap((Bitmap) msg.obj);
            }
        };
        bu.disPlay(hl,data.Photo,context);
        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return makeItemView(MData.get(position));
        return convertView;
    }
}
