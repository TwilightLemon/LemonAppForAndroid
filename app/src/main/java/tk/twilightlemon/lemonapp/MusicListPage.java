package tk.twilightlemon.lemonapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicListPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musiclist);
        SetWindow();
        TextView tv=findViewById(R.id.MusicList_title);
        tv.setText(Settings.ListData.name);

        MusicListAdapter ad=new MusicListAdapter(MusicListPage.this, Settings.ListData, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    RelativeLayout PATENT= (RelativeLayout)view.getParent();
                    int index= Integer.parseInt(((TextView)PATENT.findViewById(R.id.MusicList_index)).getText().toString());
                    final InfoHelper.Music Data=Settings.ListData.Data.get(index);
                    String MusicID=Data.MusicID;
                    MainActivity.GetUrl(MusicID,new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            String name=(Data.MusicName+"-"+Data.Singer+".mp3").replace("\\","").replace("/","");
                            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new
                                    DownloadManager.Request(Uri.parse(msg.obj.toString()));
                            request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
                            request.setTitle(name);
                            request.setDescription("小萌音乐正在下载中");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                            MainActivity.sdm("正在下载:"+name,getApplicationContext());
                        }
                    }); }catch(Exception e){}
            }
        });
        ListView listView = findViewById(R.id.MusicList_list);
        listView.setAdapter(ad);
        FirstFragment.setListViewHeightBasedOnChildren(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                if(position!=-1){
                    Message message = new Message();
                    message.what = 0;
                    message.obj = position;
                    Settings.Callback_PlayMusic.sendMessage(message);
                    finish();}
            }});
    }
    public void SetWindow(){
        /////配置沉浸式窗口
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    public void MusicAllDownloadOnClick(View view){
        try{
            for(int index= 0;index!=Settings.ListData.Data.size()-1;++index){
            final InfoHelper.Music Data=Settings.ListData.Data.get(index);
            String MusicID=Data.MusicID;
            MainActivity.GetUrl(MusicID,new Handler(){
                @Override
                public void handleMessage(Message msg){
                    String name=(Data.MusicName+"-"+Data.Singer+".mp3").replace("\\","").replace("/","");
                    DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new
                            DownloadManager.Request(Uri.parse(msg.obj.toString()));
                    request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
                    request.setTitle(name);
                    request.setDescription("小萌音乐正在下载中");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                    MainActivity.sdm("正在下载:"+name,getApplicationContext());
                }
            }); }}catch(Exception e){}
    }
}
