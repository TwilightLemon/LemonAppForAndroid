package tk.twilightlemon.lemonapp.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class FileHelper {
    public static void WriteAllText(String FilePath,String Text){
        try {
            File f = new File(FilePath);
            if (!f.exists())
                f.createNewFile();
            FileOutputStream w=new FileOutputStream(f,false);
            w.write(Text.getBytes("UTF-8"));
            w.close();
        }catch (Exception e){}
    }
    public static String ReadAllText(String FilePath){
        try {
            File f = new File(FilePath);
            FileInputStream r = new FileInputStream(f);
            byte[] buffer =new byte[32*1024];
            int len=0;
            StringBuffer sb=new StringBuffer();
            while((len=r.read(buffer))>0){
                sb.append(new String(buffer,0,len));
            }
            r.close();
            return sb.toString();
        }catch (Exception e){return null;}
    }
}
