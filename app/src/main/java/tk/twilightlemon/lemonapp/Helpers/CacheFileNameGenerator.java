package tk.twilightlemon.lemonapp.Helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import java.io.File;
import java.util.List;

import tk.twilightlemon.lemonapp.layouts.MainActivity;

public class CacheFileNameGenerator implements FileNameGenerator {
    /**
     * @param url
     * @return
     */
    @Override
    public String generate(String url) {
        String path= MainActivity.Musicdt.MusicID;
        return path+".m4a";
    }
}

