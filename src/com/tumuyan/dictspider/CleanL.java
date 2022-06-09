package com.tumuyan.dictspider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.tumuyan.dictspider.Clean.*;
import static com.tumuyan.dictspider.Utils.OpenCC;
import static com.tumuyan.dictspider.Utils.WriteList;

// 把dump的wiki词条，滤除由常见汉字组成的词条，拆解为中文、英文、混合词条


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


public class CleanL {

    public static void main(String[] args) {

        String config_file = null;
        List<String> arg_list = Arrays.asList(args);
        int index = arg_list.indexOf("-c");
        if (index < 0)
            index = arg_list.indexOf("-config");

        if (index >= 0 && index < args.length - 1) {
            config_file = args[index + 1];
        }
        Config config = Utils.ReadConfig(config_file);
        config.setDefault_path("A:\\ProjectPython\\zhwiki-20220601-all-titles-in-ns0");
        config.Parse(args);

        if (!config.verifyInputPath()) {
            return;
        }

        Dict dict = new Dict();

        // 使用过滤器去除普通中文词条
        String[] filters = {"[一-龟]+"};

        for (String p : config.getInput_files()) {
            System.out.println("Load file: " + p);
            dict.add(ReadFile(p,filters ));
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



    // 读取文件，并且使用filter作为正则对词条进行过滤，符合规则的不加载
    public static Dict ReadFile(String path, String[] filters) {
        Dict dict = new Dict();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;

            l:
            while ((line = bufferedReader.readLine()) != null) {

//              如果匹配到空行
                if (line.length() < 2)
                    continue;

                for(String filter:filters){
                    if(line.matches(filter))
                        continue l;
                }

                dict.add(line);
            }

            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dict;

    }

}
