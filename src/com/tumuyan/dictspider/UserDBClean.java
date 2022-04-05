package com.tumuyan.dictspider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.tumuyan.dictspider.Utils.WriteList;

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
                "A:\\ProjectPython\\pinyin_simp.userdb.c2.txt;" +
                        "A:\\ProjectPython\\pinyin_simp.userdb.b.txt;"
        ).split(";"));

        config.setDefault_blacklist_regex(
                new String[]{
                        ".*_.+",
                        "[^\t]*[^a-zA-Z].+"
                }
        );

        config.Parse(args);

        if (!config.verifyInputPath()) {
            return;
        }


        UserDB refDB = new UserDB();
        for (String p : config.getRefer_files()) {
            System.out.println("Load refer: " + p);
            refDB.add(ReadFile(p,true));
        }


        // 只处理一个文件
        UserDB inputDB = new UserDB(config.getCount_group());
        inputDB.addRefer(refDB.getC0());

        List<String> inputfiles = config.getInput_files();
        if (inputfiles.size() > 0) {
            String inputpath = inputfiles.get(0);
            System.out.println("Load file: " + inputpath);
            try {
                FileInputStream fileInputStream = new FileInputStream(inputpath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.length() < 2)
                        continue;
                    inputDB.add(line,false);
                }
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            inputDB.WriteWordByCountGroup(config.getPath_w());
        }

    }


    public static UserDB ReadFile(String path,boolean swap) {

        UserDB dict = new UserDB();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {

//              如果匹配到空行
                if (line.length() < 2)
                    continue;
                dict.add(line,swap);
            }

            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dict;
    }


}
