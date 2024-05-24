package com.tumuyan.dictspider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserDBClean {
    /*
    用户词库同步后的整理工具
     */

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
        config.setDefault_path("A:\\ProjectPython\\pinyin_simp.userdb.txt");
        config.setDefault_blacklist((
                "A:\\ProjectPython\\pinyin_simp.v.userdb.blacklist.txt;"
        ).split(";"));

        config.setDefault_blacklist_regex(
                new String[]{
                        ".*_.+",
                        "[^\t]*[^a-zA-Z].+"
                }
        );

        config.setDefault_whitelist(new String[]{
                "A:\\ProjectPython\\pinyin_simp.v.userdb.whitelist.txt"
        });

        config.Parse(args);

        if (!config.verifyInputPath()) {
            return;
        }

        config.verifyBlacklist();


        UserDB refDB = new UserDB();
        for (String p : config.getRefer_files()) {
            System.out.println("Load refer: " + p);
            refDB.add(ReadFile(p, true, true));
        }

        UserDB whiteDB = new UserDB();
        for (String p : config.getWhitelist()) {
            System.out.println("Load whiteList: " + p);
            whiteDB.add(ReadFile(p, false, true));
        }

        UserDB blackDB = new UserDB();
        for (String p : config.getBlacklist()) {
            System.out.println("Load blackList: " + p);
            blackDB.add(ReadFile(p, false, true));
        }


        // 只处理一个文件
        UserDB inputDB = new UserDB(config.getCount_group());
        inputDB.addRefer(refDB.getC0());
        inputDB.addWhiteList(whiteDB.getC0());
        inputDB.addBlackList(blackDB.getC0());
        inputDB.addBlacklistRegex(config.getBlacklist_regex());

        List<String> inputfiles = config.getInput_files();
        if (!inputfiles.isEmpty()) {
            String inputpath = inputfiles.get(0);
            System.out.println("Load file: " + inputpath);
            try {
                FileInputStream fileInputStream = new FileInputStream(inputpath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.length() < 2)
                        continue;
                    inputDB.add(line, false, false);
                }
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            inputDB.WriteWordByCountGroup(config.getPath_w());
        }

    }


    public static UserDB ReadFile(String path, boolean isSchemaDict, boolean addToC0) {
        UserDB dict = new UserDB();
        return ReadFile(dict, path, isSchemaDict, addToC0);
    }


    public static UserDB ReadFile(UserDB dict, String path, boolean isSchemaDict, boolean addToC0) {
//        UserDB dict = new UserDB();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));

            String line;

            while ((line = bufferedReader.readLine()) != null) {

//              如果匹配到空行
                if (line.length() < 2)
                    continue;
                dict.add(line, isSchemaDict, addToC0);
            }

            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dict;
    }


}
