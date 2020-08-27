package tk.twilightlemon.lemonapp.Helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;
import java.util.List;

public class CacheFileNameGenerator implements FileNameGenerator {
    /**
     * @param url
     * @return
     */
    @Override
    public String generate(String url) {
        String path=TextHelper.FindByAb(url,"amobile.music.tc.qq.com/C400",".m4a");
        return path+".m4a";
    }
}

