package com.tumuyan.dictspider;

import java.io.*;
import java.util.*;

import static com.tumuyan.dictspider.Utils.OpenCC_T2S;
import static com.tumuyan.dictspider.Utils.WriteList;

// 把dump的wiki词条，拆解为中文、英文、混合词条


/*
env.engine.context:commit_history().latest_text()

pinyin_simp_wiki.dict.yaml

# Rime dictionary
# encoding: utf-8
#
# 维基百科词库
# by tumuyan
---
name: pinyin_simp_wiki
version: "20210628"
sort: by_weight
use_preset_vocabulary: false
...



手动清理

^[^a-zA-Z][a-zA-Z]+\s

^.{1,2}[村县]$
*/


public class Clean {

    public static void main(String[] args) {
//        System.out.println("args.length="+args.length + ", class="+ Clean.class.getSimpleName());
//        for(String s :args){
//            System.out.println("  args:"+s);
//        }

        Config config = new Config();
        config.setDefault_path("A:\\ProjectPython\\zhwiki-20220220-all-titles-in-ns0");
        config.Parse(args);

        if (!config.verifyInputPath()) {
            return;
        }

        Dict dict = new Dict();
        for (String p : config.getInput_files()) {
            System.out.println("Load file: " + p);
            dict.add(ReadFile(p));
        }

        OutputWords(dict, config.getPath_w(), config.auto_delete, true);
    }


    public static void OutputWords(Dict dict, String path_w, boolean auto_delete, boolean t2s) {
        try {
            Set<String> chs = dict.getChs();

            if (t2s) {
                WriteList(chs, path_w + ".cn.dict.txt", auto_delete, false);

                OpenCC_T2S(path_w + ".cn.dict.txt", path_w + ".chs.dict.txt", "A:\\EBookTools\\OpenCC\\bin");
                chs = Utils.ReadWords(path_w + ".chs.dict.txt");
                chs.removeAll(ReadWords());
                WriteList(chs, path_w + ".chs2.dict.txt", auto_delete, false);

            } else {
                chs.removeAll(ReadWords());
                WriteList(chs, path_w + ".cn.dict.txt", auto_delete, false);
            }

            WriteList(dict.getEng(), path_w + ".eng.dict.txt", auto_delete, false);
            WriteList(dict.getMix(), path_w + ".mix.dict.txt", auto_delete, false);
            WriteList(dict.getSuffix(), path_w + ".chs.suffix.txt", auto_delete, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finish");
    }


    public static Set<String> ReadWords() {
        Set<String> words = new HashSet<>();

        words.addAll(Utils.ReadWords("A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词.txt"));
        words.addAll(Utils.ReadWords("A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-村县镇乡路村縣鎮鄉路.txt"));
        return words;
    }


    // path_w为空时，读取path每一行文本,如果包含tab，把第一个字到keys中；并返回key
//    path_w不为空时，把带拼音的写入path_w并返回key
    public static Dict ReadFile(String path) {
        Dict dict = new Dict();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line = null;

            StringBuffer buffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {

//              如果匹配到空行
                if (line.length() < 2)
                    continue;
                dict.add(line);
            }

            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dict;

    }


}
