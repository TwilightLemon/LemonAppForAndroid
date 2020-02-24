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

import java.util.ArrayList;

import tk.twilightlemon.lemonapp.Helpers.HttpHelper;
import tk.twilightlemon.lemonapp.Helpers.Image.BitmapUtils;
import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.R;

public class SingerAndRadioListAdapter extends BaseAdapter {
    private ArrayList<InfoHelper.SingerAndRadioData> mData = null;
    private LayoutInflater inflater = null;
    private Context context;
    public SingerAndRadioListAdapter(Activity a, ArrayList<InfoHelper.SingerAndRadioData> data,Context context) {
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        this.context=context;
    }

    public int getCount() {
        return mData.size();
    }

    public View getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    private View makeItemView(InfoHelper.SingerAndRadioData data) {
        View itemView = inflater.inflate(R.layout.items_singerandradio, null);
        TextView tit = itemView.findViewById(R.id.singer_title);
        tit.setText(data.name);
        final ImageView img = itemView.findViewById(R.id.singer_image);
        BitmapUtils bu=new BitmapUtils();
        Handler hl=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                img.setImageBitmap((Bitmap) msg.obj);
            }
        };
        bu.disPlay(hl,data.url,context);
        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return makeItemView(mData.get(position));
        return convertView;
    }
}