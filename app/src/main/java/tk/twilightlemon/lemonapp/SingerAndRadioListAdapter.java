package tk.twilightlemon.lemonapp;

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

import java.util.ArrayList;

public class SingerAndRadioListAdapter extends BaseAdapter {
    View[] itemViews;
    private Activity ac;
    private static LayoutInflater inflater=null;
    public SingerAndRadioListAdapter(Activity a, ArrayList<InfoHelper.SingerAndRadioData> data){
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemViews = new View[data.size()];

        ac=a;
        for (int i=0; i<itemViews.length; ++i){
            itemViews[i] = makeItemView(data.get(i));
        }
    }

    public int getCount()  {
        return itemViews.length;
    }

    public View getItem(int position)  {
        return itemViews[position];
    }

    public long getItemId(int position) {
        return position;
    }

    private View makeItemView(InfoHelper.SingerAndRadioData data) {
        View itemView = inflater.inflate(R.layout.items_singerandradio, null);
        TextView tit= itemView.findViewById(R.id.singer_title);
        tit.setText(data.name);
        final ImageView img = itemView.findViewById(R.id.singer_image);
        HttpHelper.GetWebImage("GD" +data.name+ ".jpg",data.url,true,
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        img.setImageBitmap((Bitmap) msg.obj);
                    }
                });

        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return itemViews[position];
        return convertView;
    }
}