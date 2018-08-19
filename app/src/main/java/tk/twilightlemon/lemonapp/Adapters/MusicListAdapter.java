package tk.twilightlemon.lemonapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tk.twilightlemon.lemonapp.Helpers.InfoHelper;
import tk.twilightlemon.lemonapp.R;

public class MusicListAdapter extends BaseAdapter {
    private InfoHelper.MusicGData mData = null;
    private View.OnClickListener onClick = null;
    private LayoutInflater inflater = null;

    public MusicListAdapter(Activity a, InfoHelper.MusicGData data, View.OnClickListener DownloadBtnOnClick) {
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        onClick = DownloadBtnOnClick;
    }

    public int getCount() {
        return mData.Data.size();
    }

    public View getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    private View makeItemView(InfoHelper.Music dt, int index) {
        View itemView = inflater.inflate(R.layout.items_musiclist, null);
        TextView tit = itemView.findViewById(R.id.MusicList_title);
        tit.setText(dt.MusicName);
        TextView text = itemView.findViewById(R.id.MusicList_mss);
        if(dt.MusicName_Lyric.length()!=0)
          text.setText(dt.Singer+"•"+dt.MusicName_Lyric);
        else text.setText(dt.Singer);
        TextView indextx = itemView.findViewById(R.id.MusicList_index);
        indextx.setText(index + "");
        itemView.findViewById(R.id.MusicDownload).setOnClickListener(onClick);
        return itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return makeItemView(mData.Data.get(position), position);
        return convertView;
    }
}