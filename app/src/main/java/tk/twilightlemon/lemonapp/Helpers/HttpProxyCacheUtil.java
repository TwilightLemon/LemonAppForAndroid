package tk.twilightlemon.lemonapp.Helpers;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class HttpProxyCacheUtil {

    private static HttpProxyCacheServer audioProxy;

    public static HttpProxyCacheServer getAudioProxy(Context context) {
        if (audioProxy== null) {
            audioProxy= new HttpProxyCacheServer.Builder(context)
                    .cacheDirectory(new File(context.getExternalCacheDir().getPath()+"/AudioCache/"))
                    .maxCacheSize(1024 * 1024 * 1024) // 缓存大小
                    .fileNameGenerator(new CacheFileNameGenerator())
                    .build();
        }
        return audioProxy;
    }
}
