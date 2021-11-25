package com.tumuyan.dictspider;


import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;

import com.tumuyan.dictspider.WikiCClean.Dict;

import static com.tumuyan.dictspider.WikiCClean.OutputWords;
import static com.tumuyan.dictspider.WikiCClean.WriteList;

// 从萌娘百科dump词库并预处理
//
/*
pinyin_simp_moe.dict.yaml

# Rime dictionary
# encoding: utf-8
#
# 萌娘百科词库
# by tumuyan
---
name: pinyin_simp_moe
version: "20210628"
sort: by_weight
use_preset_vocabulary: false
...

*/

public class DumpMoeGirl {
    private static boolean debug = true;

    public static void main(String[] args) {

        String path_w = "";
        boolean auto_delete = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-h") {
                System.out.println("help\n");
            } else if (args[i] == "-a") {
                auto_delete = true;
            }
            if (args[i] == "-o") {
                i++;
                if (args.length > i) {

                    path_w = args[i].trim().replaceFirst("[/\\\\]$", "");
                    File file = new File(path_w).getParentFile();
//                    File file=new File(path_w.replaceFirst("[^/\\\\]+$",""));
                    if (file.exists()) {
                        System.out.println("Output to: " + args[i]);
                    } else {
                        path_w = "";
                        System.out.println("[Err]Output folder not exist: " + file.getPath());
                    }

                } else {
                    System.out.println("[Err]Output arg not exist.");
                }
            }
        }

        if (debug) {
            if (path_w == "")
                path_w = "A:\\ProjectPython\\moegirl.";
            ;
        }

        Dict dict = new Dict();

        StringBuffer buffer = new StringBuffer();

        String next = null;

        while (true) {

            JSONObject json = query_wiki("", next, 0);

            if (json == null)
                System.out.println("Err: json=null next=" + next);
            else {
                JSONObject obj;
                if (json.has("query")) {
                    obj = json.getJSONObject("query");
                    JSONArray array;
                    array = obj.getJSONArray("allpages");

                    for (int i = 0; i < array.length(); i++) {

                        String title = array.getJSONObject(i).getString("title");
                        buffer.append('\n');
                        buffer.append(title);
                        dict.add(title);
                    }

/*                Iterator it = obj.keys();
                while(it.hasNext()){
                    String key = (String) it.next();
                    array = obj.getJSONArray(key);
                }*/
                } else {
                    System.out.println("Err: query no result; next=" + next);
                }

                if(json.has("continue")){
                    obj = json.getJSONObject("continue");
                    next = obj.getString("apcontinue");
                }else {
                    System.out.println("Done: accontinue not exit; next=" + next);
                    break;
                }

            }

            System.out.print(" > " + next);

        }

        try {
            Write(path_w+"txt",buffer , auto_delete);
//            WriteList(dict.getChs(), path_w + ".chs.dict.yaml", auto_delete, false);
//            WriteList(dict.getEng(), path_w + ".eng.dict.yaml", auto_delete, false);
//            WriteList(dict.getMix(), path_w + ".mix.dict.yaml", auto_delete, false);
//            WriteList(dict.getSuffix(), path_w + ".chs.suffix.yaml", auto_delete, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OutputWords( dict, path_w, auto_delete,true);
    }



    public static boolean Write(String path, StringBuffer content,boolean auto_delete) throws Exception {

        File file = new File(path);
        if(file.exists() && auto_delete){
            if(file.isFile())
                file.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }


    public static JSONObject query_wiki(String site, String s, int i) {

        site = "https://mzh.moegirl.org.cn";
        String url = site + "/api.php?action=query&list=allpages&format=json&aplimit=500";

        if (s != null)
            url = url + "&apcontinue=" + s;

        if (i > 3) {
            return null;
        }




        try {
            Thread.sleep(3000);
            if (i > 0) {
                Thread.sleep(60000 * i + 2000);
            }
            Connection.Response res = Jsoup.connect(url)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .timeout(10000).ignoreContentType(true).execute();//.get();
            String body = res.body();

            JSONObject json = new JSONObject(body);

            if (json.isEmpty())
                return query_wiki(site, s, i + 1);
            return json;

        } catch (Exception e) {
            System.out.print("Error: s=" + s);
            e.printStackTrace();
            return query_wiki(site, s, i + 1);
        }

    }


}
