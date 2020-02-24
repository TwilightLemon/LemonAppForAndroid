package tk.twilightlemon.lemonapp.Helpers;

import android.content.Context;

import java.io.File;

public class FileUtils {
    private String path = "";

    public FileUtils(Context context) {
        path=context.getExternalCacheDir().getPath()+"/DownloadCache/";
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String FileName) {
        return new File(path, FileName);
    }
}