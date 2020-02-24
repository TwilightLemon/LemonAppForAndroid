package tk.twilightlemon.lemonapp.Helpers.Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class BitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public BitmapUtils(){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    public void disPlay(Handler handler, String url, Context context) {
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            Message msg=new Message();
            msg.obj=bitmap;
            handler.sendMessage(msg);
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(context,url);
        if(bitmap !=null){
            Message msg=new Message();
            msg.obj=bitmap;
            handler.sendMessage(msg);
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        mNetCacheUtils.getBitmapFromNet(handler,url);
    }
}
