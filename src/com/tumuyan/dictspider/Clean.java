package com.tumuyan.dictspider;

import java.io.*;
import java.util.*;

import static com.tumuyan.dictspider.Utils.*;

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
        config.setDefault_path("A:\\ProjectPython\\zhwiki-20220920-all-titles-in-ns0");
        config.Parse(args);

        if (!config.verifyInputPath()) {
            return;
        }


        Dict dict = new Dict();
        for (String p : config.getInput_files()) {
            System.out.println("Load file: " + p);
            dict.add(ReadFile(p));
        }

        OutputWords(dict, config);
    }

    public static void OutputWords(Dict dict, Config config) {
        try {
            Set<String> chs = dict.getChs();
            String path_w = config.getPath_w();
            boolean auto_delete = config.isAuto_delete();


            if (config.verifyOpencc()) {
                String opencc_path = config.getOpencc_path();
                String opencc_config = config.getOpencc_config();
                WriteList(chs, path_w + ".cn.dict.txt", auto_delete, false);

                OpenCC(path_w + ".cn.dict.txt", path_w + ".chs.dict.txt", opencc_path, opencc_config);
                chs = Utils.ReadWords(path_w + ".chs.dict.txt");

                if (config.isLess_output()) {
                    File file = new File(path_w + ".cn.dict.txt");
                    file.delete();
                }
            }

            if (config.verifyBlacklist()) {
                chs.removeAll(ReadBlackWords(config.getBlacklist()));
                WriteGrayWords(chs, path_w, config.getBlacklist_fix(), config.getBlacklist_regex());
            }

            WriteList(chs, path_w + ".dict.txt", auto_delete, false);
            if (!config.isLess_output()) {
                WriteList(dict.getEng(), path_w + ".eng.dict.txt", auto_delete, false);
                WriteList(dict.getMix(), path_w + ".mix.dict.txt", auto_delete, false);
                WriteList(dict.getSuffix(), path_w + ".chs.suffix.txt", auto_delete, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finish");
    }

    //   废词列表
    public static Set<String> ReadBlackWords(List<String> list) {

        Set<String> words = new HashSet<>();

        for (String str : list) {
            words.addAll(Utils.ReadWords(str));
        }
        return words;
    }

    //    部分词条不在废词列表内，但是也不在修复列表中。这些词条大概率后续会列入废词列表中
    public static void WriteGrayWords(Set<String> chs, String path_w, List<String> black_fix, List<String> black_regix) throws Exception {
        System.out.println(new Date() + " WriteGrayWords...");
        Set<String> grayWords = new HashSet<>();
        Set<String> words = new HashSet<>();
        for (String rule : black_fix) {
            words.addAll(Utils.ReadWords(rule));
        }

        for (String rule : black_regix) {
            for (String str : chs) {
                if (str.matches(rule)) {
                    if (!words.contains(str))
                        grayWords.add(str);
                }
            }
        }
        WriteList(grayWords, path_w + ".gray.dict.txt", true, false);
    }


    // path_w为空时，读取path每一行文本,如果包含tab，把第一个字到keys中；并返回key
//    path_w不为空时，把带拼音的写入path_w并返回key
    public static Dict ReadFile(String path) {
        Dict dict = new Dict();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;

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
