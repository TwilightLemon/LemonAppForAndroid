package tk.twilightlemon.lemonapp.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcEntry;
import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcView;
import tk.twilightlemon.lemonapp.layouts.MainActivity;

import static tk.twilightlemon.lemonapp.Helpers.TextHelper.FindByAb;

public class MusicLib {
    @SuppressLint("HandlerLeak")
    public static void GetMusicLyric(String ID, final LrcView lrc) {
        lrc.reset();
        lrc.initEntryList();
        HashMap<String, String> data = HttpHelper.GetHandler();
        HttpHelper.GetWeb(new Handler() {
                              @Override
                              public void handleMessage(Message msg) {
                                  try {
                                      super.handleMessage(msg);
                                      String Ct = msg.obj.toString();
                                      String Ctlyric = Ct.substring(Ct.indexOf("\"lyric\":\"") + 9, Ct.indexOf("\",\""));
                                      String lyricdata = escapeHtml(new String(Base64.decode(Ctlyric.getBytes(), Base64.DEFAULT)));
                                      String Cttrans = Ct.substring(Ct.indexOf("\"trans\":\"") + 9, Ct.indexOf("\"})"));
                                      String transdata = new String(Base64.decode(Cttrans.getBytes(), Base64.DEFAULT));
                                      if (Ct.contains("\"trans\":\"\"})")) {
                                          try {
                                              List<LrcEntry> list = new ArrayList<>();
                                              String[] dt = lyricdata.split("[\n]");
                                              for (String x : dt) {
                                                  ArrayList<String> line = parserLine(x, null, null, null);
                                                  if (line != null) if (line.get(1) != "")
                                                      list.add(new LrcEntry(strToTime(line.get(0)), line.get(1)));
                                              }
                                              lrc.reset();
                                              lrc.onLrcLoaded(list);
                                              lrc.initEntryList();
                                          } catch (Exception e) {
                                          }
                                      } else {
                                          ArrayList<String> datatimes = new ArrayList<String>();
                                          ArrayList<String> datatexs = new ArrayList<String>();
                                          HashMap<String, String> gcdata = new HashMap<String, String>();
                                          String[] dt = lyricdata.split("[\n]");
                                          for (String x : dt) {
                                              parserLine(x, datatimes, datatexs, gcdata);
                                          }
                                          ArrayList<String> dataatimes = new ArrayList<String>();
                                          ArrayList<String> dataatexs = new ArrayList<String>();
                                          HashMap<String, String> fydata = new HashMap<String, String>();
                                          String[] dta = transdata.split("[\n]");
                                          for (String x : dta) {
                                              parserLine(x, dataatimes, dataatexs, fydata);
                                          }
                                          ArrayList<String> KEY = new ArrayList<String>();
                                          HashMap<String, String> gcfydata = new HashMap<String, String>();
                                          List<LrcEntry> list = new ArrayList<>();
                                          for (String x : datatimes) {
                                              KEY.add(x);
                                              gcfydata.put(x, "");
                                          }
                                          for (String x : dataatimes) {
                                              if (!KEY.contains(x)) {
                                                  KEY.add(x);
                                                  gcfydata.put(x, "");
                                              }
                                          }
                                          for (int i = 0; i != gcfydata.size(); i++) {
                                              try {
                                                  gcfydata.put(KEY.get(i), gcdata.get(KEY.get(i)) + "^" + fydata.get(KEY.get(i)));
                                              } catch (Exception e) {
                                              }
                                          }
                                          for (int i = 0; i != KEY.size(); i++) {
                                              try {
                                                  long key = strToTime(KEY.get(i));
                                                  String value = gcfydata.get(KEY.get(i)).replace("^", "\n").replace("//", "").replace("null", "");
                                                  Log.d("ST",value+"    length"+value.length());
                                                  if(value.length()>1) {
                                                      list.add(new LrcEntry(key, value));
                                                  }
                                              } catch (Exception e) {}
                                          }
                                          lrc.reset();
                                          lrc.onLrcLoaded(list);
                                          lrc.initEntryList();
                                      }
                                  } catch (Exception e) {}
                              }
                          }
                , "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1532863605625&songmid=" + ID + "&g_tk="+Settings.g_tk+"&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", data);
    }

    public static long strToTime(String ts) {
        long time = 0;
        Matcher timeMatcher = Pattern.compile("(\\d\\d):(\\d\\d)\\.(\\d\\d)").matcher(ts);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
        }
        return time;
    }

    public static ArrayList<String> parserLine(String str, ArrayList<String> times, ArrayList<String> texs, HashMap<String, String> data) {
        if (!str.startsWith("[ti:") && !str.startsWith("[ar:") && !str.startsWith("[al:") && !str.startsWith("[by:") && !str.startsWith("[offset:")) {
            String TimeData = FindByAb(str, "[", "]");
            String unTimeData = TimeData.substring(0, TimeData.length() - 1) + "0";
            String io = "[" + TimeData + "]";
            String TexsData = str.replace(io, "");
            if (times != null) {
                times.add(unTimeData);
                texs.add(TexsData);
                data.put(unTimeData, TexsData);
            }
            ArrayList<String> line = new ArrayList<>();
            line.add(unTimeData);
            line.add(TexsData);
            return line;
        } else return null;
    }

    public static String escapeHtml(String str) {
        return str.replace("&apos;", "'").replace("&nbsp;", " ");
    }

    @SuppressLint("HandlerLeak")
    public static void GetTopList(final int forni, final Handler handler){
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    String jsondata = "{\"data\":" + msg.obj.toString().replace("jsonCallback(", "").replace("}]\n)", "") + "}]" + "}";
                    JSONObject o = new JSONObject(jsondata);
                    ArrayList<InfoHelper.MusicTop> data = new ArrayList<>();
                    int igne = forni;
                    for (int dat = 0; dat < o.getJSONArray("data").length(); ++dat) {
                        for (int i = 0; i < o.getJSONArray("data").getJSONObject(dat).getJSONArray("List").length(); ++i) {
                            JSONObject json = o.getJSONArray("data").getJSONObject(dat).getJSONArray("List").getJSONObject(i);
                            InfoHelper.MusicTop dt = new InfoHelper().new MusicTop();
                            dt.Name = json.getString("ListName");
                            if (dt.Name.contains("MV"))//排除MV榜
                                continue;
                            dt.ID = json.getString("topID");
                            dt.Photo = json.getString("pic_v12");
                            data.add(dt);
                            if (igne != -1 && i == igne)
                                break;
                        }
                        if (igne != -1)
                            break;
                    }
                    Message ms=new Message();
                    ms.obj=data;
                    handler.sendMessage(ms);
                } catch (Exception e) {
                }
            }
        }, "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_opt.fcg?page=index&format=html&tpl=macv4&v8debug=1", null);
    }

    @SuppressLint("HandlerLeak")
    public static void GetTopDataById(final String id, final Handler handler){
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    super.handleMessage(msg);
                    Settings.mSP.putString("ListData","Top_["+id+"]");
                    Settings.ModeID="TOP";
                    JSONObject o = new JSONObject(msg.obj.toString());
                    ArrayList<InfoHelper.Music> dat = new ArrayList<>();
                    for (int i = 0; i < o.getJSONArray("songlist").length(); ++i) {
                        JSONObject dt = o.getJSONArray("songlist").getJSONObject(i).getJSONObject("data");
                        InfoHelper.Music m = new InfoHelper().new Music();
                        m.MusicName = dt.getString("songname").replace("\\", "-").replace("?", "").replace("/", "").replace(":", "").replace("*", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "");
                        String Singer = "";
                        for (int osxc = 0; osxc != dt.getJSONArray("singer").length(); osxc++) {
                            Singer += dt.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                        }
                        m.Singer = Singer.substring(0, Singer.lastIndexOf("&"));
                        m.MusicID = dt.getString("songmid");
                        m.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + dt.getString("albummid") + ".jpg";
                        m.GC = dt.getString("songmid");
                        dat.add(m);
                    }
                    Message ms=new Message();
                    ms.obj=dat;
                    handler.sendMessage(ms);
                } catch (Exception e) {}
            }
        }, "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&topid=" + id + "&type=top&song_begin=0&song_num=100&g_tk=1206122277&loginUin=" + Settings.qq + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", null);
    }

    public static InfoHelper.MusicGData SearchData = new InfoHelper().new MusicGData();
    @SuppressLint("HandlerLeak")
    public static void Search(final MainActivity activity, final String text, final boolean isshow,final int pagecount) {
        try {
            Settings.mSP.putString("ListData","Search_["+TextHelper.Base64Coder.Encode(text)+"]");
            Settings.ModeID="Search_["+TextHelper.Base64Coder.Encode(text)+"]";
            int page=pagecount;
            if(page==0) {
                SearchData.Data.clear();
                page=1;
            }
            SearchData.name = "搜索:" + text;
            String url = "http://59.37.96.220/soso/fcgi-bin/client_search_cp?format=json&t=0&inCharset=GB2312&outCharset=utf-8&qqmusic_ver=1302&catZhida=0&p="+page+"&n=20&w=" + URLEncoder.encode(text, "utf-8") + "&flag_qc=0&remoteplace=sizer.newclient.song&new_json=1&lossless=0&aggr=1&cr=1&sem=0&force_zonghe=0";
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        String json = msg.obj.toString().replace("<em>", "").replace("</em>", "");
                        JSONObject jo = new JSONObject(json);
                        for (int i = 0; i < jo.getJSONObject("data").getJSONObject("song").getJSONArray("list").length(); ++i) {
                            InfoHelper.Music dt = new InfoHelper().new Music();
                            JSONObject jos = jo.getJSONObject("data").getJSONObject("song").getJSONArray("list").getJSONObject(i);
                            dt.MusicName = jos.getString("title");
                            dt.MusicName_Lyric=jos.getString("lyric");
                            String isx = "";
                            for (int ix = 0; ix != jos.getJSONArray("singer").length(); ix++) {
                                isx += jos.getJSONArray("singer").getJSONObject(ix).getString("name") + "&";
                            }
                            dt.Singer = isx.substring(0, isx.lastIndexOf("&"));
                            dt.MusicID = jos.getString("mid");
                            dt.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + jos.getJSONObject("album").getString("mid") + ".jpg";
                            dt.GC = jos.getJSONObject("action").getString("alert");
                            SearchData.Data.add(dt);
     //                       if(jos.getJSONArray("grp").length()!=0){
     //                           for(int t=0;t<jos.getJSONArray("grp").length();t++){
     //    突突突突突                  InfoHelper.Music dat = new InfoHelper().new Music();
     //                               JSONObject joss = jos.getJSONArray("grp").getJSONObject(t);
     //                               dat.MusicName = joss.getString("title");
     //                               String isxs = "";
     //                               for (int ix = 0; ix != joss.getJSONArray("singer").length(); ix++) {
     //                                   isxs += joss.getJSONArray("singer").getJSONObject(ix).getString("name") + "&";
     //                               }
      //                              dat.Singer = isxs.substring(0, isxs.lastIndexOf("&"));
     //                               dat.MusicID = joss.getString("mid");
     //                               dat.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + joss.getJSONObject("album").getString("mid") + ".jpg";
     //                               dat.GC = joss.getJSONObject("action").getString("alert");
     //                               SearchData.Data.add(dat);
     //                           }
     //                       }
                        }
                        if(SearchData.Data.size()!=0){
                        Settings.ListData = SearchData;
                        activity.MusicListLoad(isshow);
                        }else
                            MainActivity.SendMessageBox("什么都没有找到哦o(≧口≦)o",activity);
                    } catch (Exception e) { }
                }
            }, url, null);
        } catch (Exception e) { }
    }

    @SuppressLint("HandlerLeak")
    public static void Search_SmartBox(String key, final Handler handler){
        HttpHelper.GetWeb(new Handler(){
            @Override
            public void handleMessage(Message msg){
                try {
                    JSONObject data = new JSONObject(msg.obj.toString()).getJSONObject("data");
                    ArrayList<String> list = new ArrayList<>();
                    JSONArray song = data.getJSONObject("song").getJSONArray("itemlist");
                    for (int i = 0; i < song.length(); i++) {
                        JSONObject o = song.getJSONObject(i);
                        list.add("歌曲:" + o.getString("name") + " - " + o.getString("singer"));
                    }
                    JSONArray album = data.getJSONObject("album").getJSONArray("itemlist");
                    for (int i = 0; i < album.length(); i++) {
                        JSONObject o = album.getJSONObject(i);
                        list.add("专辑:" + o.getString("singer") + " - 《" + o.getString("name") + "》");
                    }
                    JSONArray singer = data.getJSONObject("singer").getJSONArray("itemlist");
                    for (int i = 0; i < singer.length(); i++) {
                        JSONObject o = singer.getJSONObject(i);
                        list.add("歌手:" + o.getString("singer"));
                    }
                    Message ms = new Message();
                    ms.obj = list;
                    handler.sendMessage(ms);
                }catch (Exception e){}
            }
        },"https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?key="+URLEncoder.encode(key)+"&utf8=1&is_xml=0&loginUin="+Settings.qq+"&qqmusic_ver=1592&searchid=3DA3E73D151F48308932D9680A3A5A1722872&pcachetime=1535710304",null);
    }

    @SuppressLint("HandlerLeak")
    public static void GetUrl(final String Musicid, final Handler handler) {
        final HashMap<String, String> hdata = new HashMap<String, String>();
        hdata.put("cache-control", "max-age=0");
        hdata.put("upgrade", "1");
        hdata.put("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3854.3 Mobile Safari/537.36");
        hdata.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        hdata.put("referer","https://i.y.qq.com/n2/m/share/details/album.html?albummid=003bBofB3UzHxS&ADTAG=myqq&from=myqq&channel=10007100");
        hdata.put("host", "i.y.qq.com");
        hdata.put("accept-language", "zh-CN,zh;q=0.9");
        hdata.put("sec-fetch-mode", "navigate");
        hdata.put("sec-fetch-site", "same - origin");
        hdata.put("sec-fetch-user", "?1");
        hdata.put("upgrade-insecure-requests", "1");
        hdata.put("cookie", Settings.Cookie);
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String st = msg.obj.toString();
                if(!st.contains("http://apd-vlive.apdcdn.tc.qq.com/amobile.music.tc.qq.com/C400000By9MX0yKL2c.m4a")){
                    //vkey获取失败
                    try {
                        Thread.sleep(500);
                    }catch (Exception e){}

                    //重连
                    GetUrl(Musicid,new Handler(){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            Message m=new Message();
                            m.what=msg.what;
                            m.obj=msg.obj;
                            handler.sendMessage(m);
                        }
                    });
                    return;
                }
                Matcher m=Pattern.compile("http://apd-vlive.apdcdn.tc.qq.com/amobile.music.tc.qq.com/C400000By9MX0yKL2c.m4a.*?&fromtag=38").matcher(st);
                m.find();
                final String vk=TextHelper.FindByAb(m.group(),"http://apd-vlive.apdcdn.tc.qq.com/amobile.music.tc.qq.com/C400000By9MX0yKL2c.m4a","&fromtag=38");
               HashMap<String,String> data=HttpHelper.GetHandler();
               HttpHelper.GetWeb(new Handler(){
                   @Override
                   public void handleMessage(Message msg) {
                       String json =(String)msg.obj;
                       try {
                           String mid=new JSONObject(json).getJSONArray("data").getJSONObject(0).getJSONObject("file").getString("media_mid");
                           String url="http://musichy.tc.qq.com/amobile.music.tc.qq.com/C400"+mid+".m4a" + vk + "&fromtag=98";
                           Log.d("GETURL",url);
                           Message ms=new Message();
                           ms.obj=url;
                           ms.what=200;
                           handler.sendMessage(ms);
                       } catch (JSONException e) {}
                   }
               },"https://c.y.qq.com/v8/fcg-bin/fcg_play_single_song.fcg?songmid="+Musicid+"&platform=yqq&format=json",data);
            }
        }, "https://i.y.qq.com/v8/playsong.html?songmid=000edOaL1WZOWq", hdata);
    }

    @SuppressLint("HandlerLeak")
    public static void GetGDbyID(InfoHelper.MusicGData gData, final Activity act, final boolean isShow) {
        Settings.mSP.putString("ListData","Diss_["+gData.id+"] skName{"+gData.name+"}");
        Settings.ModeID="Diss_["+gData.id+"] skName{"+gData.name+"}";
        Settings.ListData = gData;
        if (!Settings.ListData.name.contains("歌单:"))
            Settings.ListData.name = "歌单:" + Settings.ListData.name;
        final HashMap<String, String> data = new HashMap<String, String>();/////start   加载歌单列表
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvi=1693112320; RK=DKOGai2+wu; pgv_pvid=1804673584; ptcz=3a23e0a915ddf05c5addbede97812033b60be2a192f7c3ecb41aa0d60912ff26; pgv_si=s4366031872; _qpsvr_localtk=0.3782697029073365; ptisp=ctc; luin=o2728578956; lskey=00010000863c7a430b79e2cf0263ff24a1e97b0694ad14fcee720a1dc16ccba0717d728d32fcadda6c1109ff; pt2gguin=o2728578956; uin=o2728578956; skey=@PjlklcXgw; p_uin=o2728578956; p_skey=ROnI4JEkWgKYtgppi3CnVTETY3aHAIes-2eDPfGQcVg_; pt4_token=wC-2b7WFwI*8aKZBjbBb7f4Am4rskj11MmN7bvuacJQ_; p_luin=o2728578956; p_lskey=00040000e56d131f47948fb5a2bec49de6174d7938c2eb45cb224af316b053543412fd9393f83ee26a451e15; ts_refer=ui.ptlogin2.qq.com/cgi-bin/login; ts_last=y.qq.com/n/yqq/playlist/2591355982.html; ts_uid=1420532256; yqq_stat=0");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        String json = (String) msg.obj;
                        try {
                            JSONObject jo = new JSONObject(json);
                            int i = 0;
                            while (i < jo.getJSONArray("cdlist").getJSONObject(0).getJSONArray("songlist").length()) {
                                JSONArray ja = jo.getJSONArray("cdlist").getJSONObject(0).getJSONArray("songlist");
                                JSONObject obj = ja.getJSONObject(i);
                                InfoHelper.Music md = new InfoHelper().new Music();
                                md.MusicName = obj.getString("songname");
                                try{md.MusicName_Lyric=obj.getString("albumdesc");}catch(Exception e){}
                                String Singer = "";
                                for (int osxc = 0; osxc != obj.getJSONArray("singer").length(); ++osxc) {
                                    Singer += obj.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                                }
                                md.Singer = Singer.substring(0, Singer.lastIndexOf("&"));
                                md.GC = obj.getString("songid");
                                try {
                                    md.MusicID = obj.getString("songmid");
                                    md.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + obj.getString("albummid") + ".jpg";
                                    Settings.ListData.Data.add(md);
                                } catch (Exception e) { }
                                i++;
                            }
                            ((MainActivity)act).MusicListLoad(isShow);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }, "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid=" + Settings.ListData.id + "&format=json&g_tk=1157737156&loginUin={qq}&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0".replace("{qq}", Settings.qq), data);
    }

    public static void GetFLGDItems(String id, final Handler handler, final int maxValue) {
        final HashMap<String, String> data = new HashMap<String, String>();
        data.put("Connection", "keep-alive");
        data.put("CacheControl", "max-age=0");
        data.put("Upgrade", "1");
        data.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Host", "c.y.qq.com");
        data.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        data.put("Cookie", "pgv_pvi=9798155264; pt2gguin=o2728578956; RK=JKKMei2V0M; ptcz=f60f58ab93a9b59848deb2d67b6a7a4302dd1208664e448f939ed122c015d8d1; pgv_pvid=4173718307; ts_uid=5327745136; ts_refer=xui.ptlogin2.qq.com/cgi-bin/xlogin; pgv_info=ssid=s3825018265; pgv_si=s8085315584; _qpsvr_localtk=0.08173445548395986; ptisp=ctc; pt4_token=ZIeo22vj6enh5MYFVRG8dcF1N9S8Y*ccVSStyU4Jquw_; yq_playdata=r_99; yq_playschange=0; player_exist=1; qqmusic_fromtag=66; yplayer_open=0; yqq_stat=0; ts_last=y.qq.com/portal/playlist.html");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    JSONObject o = new JSONObject(msg.obj.toString());
                    ArrayList<InfoHelper.MusicGData> FLGDdata = new ArrayList<>();
                    for (int i = 0; i < o.getJSONObject("data").getJSONArray("list").length(); ++i) {
                        InfoHelper.MusicGData md = new InfoHelper().new MusicGData();
                        JSONObject jo = o.getJSONObject("data").getJSONArray("list").getJSONObject(i);
                        md.name = jo.getString("dissname");
                        md.pic = jo.getString("imgurl").replace("http://","https://");
                        md.id = jo.getString("dissid");
                        md.sub = jo.getJSONObject("creator").getString("name");
                        FLGDdata.add(md);
                        if (maxValue != -1 && i == maxValue)
                            break;
                    }
                    Message ms = new Message();
                    ms.obj = FLGDdata;
                    handler.sendMessage(ms);
                } catch (Exception e) {
                }
            }
        }, "https://c.y.qq.com/splcloud/fcgi-bin/fcg_get_diss_by_tag.fcg?picmid=1&rnd=0.38615680484561965&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&categoryId=" + id + "&sortId=5&sin=0&ein=29", data);
    }

    public static void GetSingerByTag(String tag, final Handler handler, final int maxValue) {
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    JSONObject o = new JSONObject(msg.obj.toString());
                    ArrayList<InfoHelper.SingerAndRadioData> list = new ArrayList<>();
                    for (int i = 0; i < o.getJSONObject("data").getJSONArray("list").length(); i++) {
                        InfoHelper.SingerAndRadioData sd = new InfoHelper().new SingerAndRadioData();
                        JSONObject jo = o.getJSONObject("data").getJSONArray("list").getJSONObject(i);
                        sd.name = jo.getString("Fsinger_name");
                        sd.url = "https://y.gtimg.cn/music/photo_new/T001R150x150M000" + jo.getString("Fsinger_mid") + ".jpg?max_age=2592000";
                        list.add(sd);
                        if (maxValue != -1 && i == maxValue)
                            break;
                    }
                    Message ms = new Message();
                    ms.obj = list;
                    handler.sendMessage(ms);
                } catch (Exception e) {
                }
            }
        }, "https://c.y.qq.com/v8/fcg-bin/v8.fcg?channel=singer&page=list&key=" + tag + "&pagesize=100&pagenum=1&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", null);
    }

    public static void GetRadioListByTag(final Handler handler, final int maxValue) {
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    ArrayList<ArrayList<InfoHelper.SingerAndRadioData>> Data = new ArrayList<>();
                    JSONObject o = new JSONObject(msg.obj.toString());
                    for (int csr = 0; csr < o.getJSONObject("data").getJSONObject("data").getJSONArray("groupList").length(); csr++) {
                        ArrayList<InfoHelper.SingerAndRadioData> Mdata = new ArrayList<>();
                        for (int i = 0; i < o.getJSONObject("data").getJSONObject("data").getJSONArray("groupList").getJSONObject(csr).getJSONArray("radioList").length(); i++) {
                            JSONObject jo = o.getJSONObject("data").getJSONObject("data").getJSONArray("groupList").getJSONObject(csr).getJSONArray("radioList").getJSONObject(i);
                            InfoHelper.SingerAndRadioData rd = new InfoHelper().new SingerAndRadioData();
                            rd.name = jo.getString("radioName");
                            rd.id = jo.getString("radioId");
                            rd.url = jo.getString("radioImg");
                            Mdata.add(rd);
                            if (maxValue != -1 && i == maxValue)
                                break;
                        }
                        Data.add(Mdata);
                        if (maxValue != -1)
                            break;
                    }
                    Message ms = new Message();
                    ms.obj = Data;
                    handler.sendMessage(ms);
                } catch (Exception e) {
                }
            }
        }, "https://c.y.qq.com/v8/fcg-bin/fcg_v8_radiolist.fcg?channel=radio&format=json&page=index&tpl=wk&new=1&p=0.8663229811059507&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", null);
    }

    public static void GetRadioMusicById(String id, final Handler handler) {
        Settings.mSP.putString("ListData","Radio_["+id+"]");
        Settings.ModeID="Radio_["+id+"]";
        if (id.length() == 2) {
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        JSONObject o = new JSONObject(msg.obj.toString()).getJSONArray("songlist").getJSONObject(0);
                        String Singer = "";
                        for (int osxc = 0; osxc != o.getJSONArray("singer").length(); osxc++) {
                            Singer += o.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                        }
                        InfoHelper.Music m = new InfoHelper().new Music();
                        m.MusicName = o.getString("name");
                        m.MusicID = o.getString("mid");
                        m.GC = o.getString("mid");
                        m.Singer = Singer.substring(0, Singer.length() - 1);
                        m.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + o.getJSONObject("album").getString("mid") + ".jpg";
                        Message ms = new Message();
                        ms.obj = m;
                        handler.sendMessage(ms);
                    } catch (Exception e) { }
                }
            }, "https://c.y.qq.com/rcmusic2/fcgi-bin/fcg_guess_youlike_pc.fcg?g_tk=1206122277&loginUin=" + Settings.qq + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=703&uin=" + Settings.qq, null);
        } else {
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        JSONObject o = new JSONObject(msg.obj.toString()).getJSONObject("songlist").getJSONObject("data").getJSONArray("track_list").getJSONObject(0);
                        String Singer = "";
                        for (int osxc = 0; osxc != o.getJSONArray("singer").length(); osxc++) {
                            Singer += o.getJSONArray("singer").getJSONObject(osxc).getString("name") + "&";
                        }
                        InfoHelper.Music m = new InfoHelper().new Music();
                        m.MusicName = o.getString("name");
                        m.MusicID = o.getString("mid");
                        m.GC = o.getString("mid");
                        m.Singer = Singer.substring(0, Singer.length() - 1);
                        m.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + o.getJSONObject("album").getString("mid") + ".jpg";
                        Message ms = new Message();
                        ms.obj = m;
                        handler.sendMessage(ms);
                    } catch (Exception e) {
                    }
                }
            }, "https://u.y.qq.com/cgi-bin/musicu.fcg?g_tk=1206122277&loginUin=" + Settings.qq + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=" + URLEncoder.encode("{\"songlist\":{\"module\":\"pf.radiosvr\",\"method\":\"GetRadiosonglist\",\"param\":{\"id\":" + id + ",\"firstplay\":1,\"num\":10}},\"radiolist\":{\"module\":\"pf.radiosvr\",\"method\":\"GetRadiolist\",\"param\":{\"ct\":\"24\"}},\"comm\":{\"ct\":\"24\"}}"), null);
        }
    }
}
