package tk.twilightlemon.lemonapp.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcEntry;
import tk.twilightlemon.lemonapp.Helpers.Lrc.LrcView;
import tk.twilightlemon.lemonapp.layouts.MainActivity;
import tk.twilightlemon.lemonapp.layouts.MusicListPage;

public class MusicLib {
    public static void GetMusicLyric(String ID, final LrcView lrc) {
        lrc.reset();
        lrc.initEntryList();
        lrc.initNextTime();
        HashMap<String, String> data = new HashMap<>();
        data.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        data.put("Accept", "*/*");
        data.put("Referer", "https://y.qq.com/portal/player.html");
        data.put("Cookie", "yqq_stat=0; pgv_info=ssid=s5197017565; pgv_pvid=507491580; ts_uid=5941690444; pgv_pvi=1047845888; pgv_si=s4656565248; yq_index=0; player_exist=1; yq_playschange=0; yq_playdata=; qqmusic_fromtag=66; ts_last=y.qq.com/portal/player.html; yplayer_open=1");
        data.put("Host", "c.y.qq.com");
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
                                              lrc.initNextTime();
                                          } catch (Exception e) {
                                          }
                                      } else {
// SendMessageBox(lyricdata +transdata);
                                          ArrayList<String> datatimes = new ArrayList<String>();
                                          ArrayList<String> datatexs = new ArrayList<String>();
                                          HashMap<String, String> gcdata = new HashMap<String, String>();
                                          String[] dt = lyricdata.split("[\n]");
                                          for (String x : dt) {
                                              parserLine(x, datatimes, datatexs, gcdata);
                                          }
//sdm("d1");
                                          ArrayList<String> dataatimes = new ArrayList<String>();
                                          ArrayList<String> dataatexs = new ArrayList<String>();
                                          HashMap<String, String> fydata = new HashMap<String, String>();
                                          String[] dta = transdata.split("[\n]");
                                          for (String x : dta) {
                                              parserLine(x, dataatimes, dataatexs, fydata);
                                          }
//  sdm("d2");
                                          ArrayList<String> KEY = new ArrayList<String>();
                                          HashMap<String, String> gcfydata = new HashMap<String, String>();
                                          List<LrcEntry> list = new ArrayList<>();
                                          for (String x : datatimes) {
                                              KEY.add(x);
                                              gcfydata.put(x, "");
                                          }
//sdm("d3");
                                          for (String x : dataatimes) {
                                              if (!KEY.contains(x)) {
                                                  KEY.add(x);
                                                  gcfydata.put(x, "");
                                              }
                                          }
//sdm("d4");
                                          for (int i = 0; i != gcfydata.size(); i++) {
                                              try {
                                                  gcfydata.put(KEY.get(i), gcdata.get(KEY.get(i)) + "^" + fydata.get(KEY.get(i)));
                                              } catch (Exception e) {
                                              }
                                          }
//sdm("d5   "+dataatexs.size()+"   "+dataatimes.size()+"   "+datatexs.size()+"   "+datatimes.size()+"   "+KEY.size());
                                          for (int i = 0; i != KEY.size(); i++) {
                                              try {
                                                  long key = strToTime(KEY.get(i));
                                                  String value = gcfydata.get(KEY.get(i));
                                                  list.add(new LrcEntry(key, value.replace("^", "\n").replace("//", "").replace("null", "")));
                                              } catch (Exception e) {
                                              }
                                          }
                                          lrc.reset();
                                          lrc.onLrcLoaded(list);
                                          lrc.initEntryList();
                                          lrc.initNextTime();
                                      }
                                  } catch (Exception e) {
                                  }
                              }
                          }
                , "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1532863605625&songmid=" + ID + "&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", data);
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
            String TimeData = MainActivity.FindByAb(str, "[", "]");
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

    public static void Search(final Activity activity, final String text) {
        try {
            String url = "http://59.37.96.220/soso/fcgi-bin/client_search_cp?format=json&t=0&inCharset=GB2312&outCharset=utf-8&qqmusic_ver=1302&catZhida=0&p=1&n=20&w=" + URLEncoder.encode(text, "utf-8") + "&flag_qc=0&remoteplace=sizer.newclient.song&new_json=1&lossless=0&aggr=1&cr=1&sem=0&force_zonghe=0";
            HttpHelper.GetWeb(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        String json = msg.obj.toString().replace("<em>", "").replace("</em>", "");
                        JSONObject jo = new JSONObject(json);
                        InfoHelper.MusicGData Data = new InfoHelper().new MusicGData();
                        Data.name = "搜索:" + text;
                        for (int i = 0; i < jo.getJSONObject("data").getJSONObject("song").getJSONArray("list").length(); ++i) {
                            InfoHelper.Music dt = new InfoHelper().new Music();
                            JSONObject jos = jo.getJSONObject("data").getJSONObject("song").getJSONArray("list").getJSONObject(i);
                            dt.MusicName = jos.getString("name");
                            String isx = "";
                            for (int ix = 0; ix != jos.getJSONArray("singer").length(); ix++) {
                                isx += jos.getJSONArray("singer").getJSONObject(ix).getString("name") + "&";
                            }
                            dt.Singer = isx.substring(0, isx.lastIndexOf("&"));
                            dt.MusicID = jos.getString("mid");
                            dt.ImageUrl = "http://y.gtimg.cn/music/photo_new/T002R300x300M000" + jos.getJSONObject("album").getString("mid") + ".jpg";
                            dt.GC = jos.getJSONObject("action").getString("alert");
                            Data.Data.add(dt);
                        }
                        if(Data.Data.size()!=0){
                        Settings.ListData = Data;
                        Intent intent = new Intent(activity, MusicListPage.class);
                        activity.startActivityForResult(intent, 1000);}else{
                            MainActivity.SendMessageBox("什么都没有搜索到哦o(≧口≦)o",activity);
                        }
                    } catch (Exception e) {
                    }
                }
            }, url, null);
        } catch (Exception e) {
        }
    }

    public static void GetUrl(final String Musicid, final Handler handler) {
//通过Musicid获取mid
        final HashMap<String, String> hdata = new HashMap<String, String>();
        hdata.put("Connection", "keep-alive");
        hdata.put("CacheControl", "max-age=0");
        hdata.put("Upgrade", "1");
        hdata.put("UserAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        hdata.put("Accept", "*/*");
        hdata.put("Referer", "https://y.qq.com/portal/player.html");
        hdata.put("Host", "c.y.qq.com");
        hdata.put("AcceptLanguage", "zh-CN,zh;q=0.8");
        hdata.put("Cookie", "pgv_pvi=1693112320; RK=DKOGai2+wu; pgv_pvid=1804673584; ptcz=3a23e0a915ddf05c5addbede97812033b60be2a192f7c3ecb41aa0d60912ff26; pgv_si=s4366031872; _qpsvr_localtk=0.3782697029073365; ptisp=ctc; luin=o2728578956; lskey=00010000863c7a430b79e2cf0263ff24a1e97b0694ad14fcee720a1dc16ccba0717d728d32fcadda6c1109ff; pt2gguin=o2728578956; uin=o2728578956; skey=@PjlklcXgw; p_uin=o2728578956; p_skey=ROnI4JEkWgKYtgppi3CnVTETY3aHAIes-2eDPfGQcVg_; pt4_token=wC-2b7WFwI*8aKZBjbBb7f4Am4rskj11MmN7bvuacJQ_; p_luin=o2728578956; p_lskey=00040000e56d131f47948fb5a2bec49de6174d7938c2eb45cb224af316b053543412fd9393f83ee26a451e15; ts_refer=ui.ptlogin2.qq.com/cgi-bin/login; ts_last=y.qq.com/n/yqq/playlist/2591355982.html; ts_uid=1420532256; yqq_stat=0");
        HttpHelper.GetWeb(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    final String mid = new JSONObject(msg.obj.toString()).getJSONArray("data").getJSONObject(0).getJSONObject("file").getString("media_mid");
                    final String guid = "365305415";
                    HttpHelper.GetWeb(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            try {
                                final int[] scr = {0};
                                final ArrayList<String[]> MData = new ArrayList<>();
                                MData.add(new String[]{"M800", "mp3"});
                                MData.add(new String[]{"C600", "m4a"});
                                MData.add(new String[]{"M500", "mp3"});
                                MData.add(new String[]{"C400", "m4a"});
                                MData.add(new String[]{"M200", "m4a"});
                                MData.add(new String[]{"M100", "m4a"});
                                final String key = new JSONObject(msg.obj.toString()).getString("key");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                String uri = "https://dl.stream.qqmusic.qq.com/" + MData.get(scr[0])[0] + mid + "." + MData.get(scr[0])[1] + "?vkey=" + key + "&guid=" + guid + "&uid=0&fromtag=30";
                                                HttpGet httpGet = new HttpGet(uri);
                                                HttpResponse response = new DefaultHttpClient().execute(httpGet);
                                                if (response.getStatusLine().getStatusCode() == 200) {
                                                    Message ms = new Message();
                                                    ms.obj = uri;
                                                    handler.sendMessage(ms);
                                                    break;
                                                } else ++scr[0];
                                            }
                                            MData.clear();
                                        } catch (Exception e) {
                                        }
                                    }
                                }).start();
                            } catch (Exception e) {
                            }
                        }
                    }, "https://c.y.qq.com/base/fcgi-bin/fcg_musicexpress.fcg?json=3&guid=" + guid + "&format=json", hdata);
                } catch (Exception e) {
                }
            }
        }, "https://c.y.qq.com/v8/fcg-bin/fcg_play_single_song.fcg?songmid=" + Musicid + "&platform=yqq&format=json", hdata);
    }

    public static void GetGDbyID(InfoHelper.MusicGData gData, final Activity act) {
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
                                } catch (Exception e) {
                                }
                                i++;
                            }
                            Intent intent = new Intent(act, MusicListPage.class);
                            act.startActivityForResult(intent, 1000);
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
                        md.pic = jo.getString("imgurl");
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
                    } catch (Exception e) {
                    }
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
