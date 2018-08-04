package tk.twilightlemon.lemonapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicListAdapter extends BaseAdapter {
    View[] itemViews;
    private static LayoutInflater inflater=null;
    public MusicListAdapter(Activity a, InfoHelper.MusicGData data,View.OnClickListener DownloadBtnOnClick){
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemViews = new View[data.Data.size()];

        for (int i=0; i<itemViews.length; ++i){
            itemViews[i] = makeItemView(data.Data.get(i),i,DownloadBtnOnClick);
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

    private View makeItemView(InfoHelper.Music dt,int index,View.OnClickListener DownloadBtnOnClick) {
        View itemView = inflater.inflate(R.layout.items_musiclist, null);
        TextView tit= itemView.findViewById(R.id.MusicList_title);
        tit.setText(dt.MusicName);
        TextView text = itemView.findViewById(R.id.MusicList_mss);
        text.setText(dt.Singer);
        TextView indextx = itemView.findViewById(R.id.MusicList_index);
        indextx.setText(index+"");
        itemView.findViewById(R.id.MusicDownload).setOnClickListener(DownloadBtnOnClick);
        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return itemViews[position];
        return convertView;
    }
}