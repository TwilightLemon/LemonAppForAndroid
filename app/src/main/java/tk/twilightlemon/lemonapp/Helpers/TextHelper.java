package tk.twilightlemon.lemonapp.Helpers;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.MessageDigest;

public final class TextHelper {
    @NonNull
    public static String FindByAb(String all, String a, String b) {
        try {
            return all.substring(all.indexOf(a) + a.length(), all.indexOf(b));
        }catch (Exception e){return "";}
    }
    public static class MD5Encoder {

        public static String encode(String string) throws Exception {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        }
    }
    public static class Base64Coder{
        public static String Encode(String str){
            return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        }
        public static String Decode(String strBase64){
           return new String(Base64.decode(strBase64.getBytes(), Base64.DEFAULT));
        }
    }
}
