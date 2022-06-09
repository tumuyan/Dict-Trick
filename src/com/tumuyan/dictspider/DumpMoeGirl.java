package com.tumuyan.dictspider;


import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.List;

import static com.tumuyan.dictspider.Clean.OutputWords;
import static com.tumuyan.dictspider.Utils.Write;

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

    public static void main(String[] args) {

//        System.out.println("args.length=" + args.length + ", class=" + Clean.class.getSimpleName());
//        for (String s : args) {
//            System.out.println("  args:" + s);
//        }


        String config_file = null;
        List<String> arg_list = Arrays.asList(args);
        int index = arg_list.indexOf("-c");
        if (index < 0)
            index = arg_list.indexOf("-config");

        if (index >= 0 && index < args.length - 1) {
            config_file = args[index + 1];
        }
        Config config = Utils.ReadConfig(config_file);

        config.setDefault_path_w("A:\\ProjectPython\\moegirl.txt");

        config.Parse(args);

        if (!config.verifyOutputPath()) {
            return;
        }

        Dict dict = new Dict();

        StringBuffer buffer = new StringBuffer();

        String next = null;

        int page = 0;
        while (page < config.getPageLimit()) {
            page++;
            JSONObject json = query_wiki(next, 0);

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

                } else {
                    System.out.println("Err: query no result; next=" + next);
                }

                if (json.has("continue")) {
                    obj = json.getJSONObject("continue");
                    next = obj.getString("apcontinue");
                } else {
                    System.out.println("Done: accontinue not exit; next=" + next);
                    break;
                }

            }

            System.out.print(" > " + next);

        }


        System.out.println("Dump finish, " + page);

        try {
            Write(config.getPath_w() + "txt", buffer, config.isAuto_delete());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OutputWords(dict, config);
    }


    public static JSONObject query_wiki(String s, int i) {

        String site = "https://mzh.moegirl.org.cn";
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
            Connection.Response res = Jsoup.connect(UrlEncoder.GetRealUrl(url))
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .timeout(10000).ignoreContentType(true).execute();//.get();
            String body = res.body();

            JSONObject json = new JSONObject(body);

            if (json.isEmpty())
                return query_wiki(s, i + 1);
            return json;

        } catch (Exception e) {
            System.out.println("Error: s=" + s + ", url=" + url);
            e.printStackTrace();
            return query_wiki(s, i + 1);
        }
    }


}
