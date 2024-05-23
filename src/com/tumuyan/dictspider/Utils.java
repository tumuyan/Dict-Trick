package com.tumuyan.dictspider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

    // 从文本文件读取配置
    public static Config ReadConfig(String path) {
        Config config = new Config();
        config.setDefault_opencc_path("A:\\EBookTools\\OpenCC\\bin");
        config.setDefault_opencc_config("C:\\prg\\WikiFilter\\scripts\\a2s.json");

        config.setDefault_blacklist((
                "A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-混合词条.txt;" +
                        "A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-村县镇乡路村縣鎮鄉路.txt;" +
                        "A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-村县镇乡路村縣鎮鄉路2.txt;"+
                        "A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-属.txt;"+
                        "A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词.txt;"
        ).split(";"));

        config.setDefault_blacklist_fix(new String[]{
                "A:\\ProjectOthers\\rime-pinyin-simp\\others\\修复-村县镇乡路村縣鎮鄉路.txt",
                "A:\\ProjectOthers\\rime-pinyin-simp\\others\\修复-混合词条.txt",
        });

        config.setDefault_blacklist_regex(
                new String[]{
                        ".*新干线.+",
                        ".+[路村县乡镇]$",
                        ".+街道$",
                        "^.{1,3}街$",
                        "[^0-9a-zA-Z]{0,2}[0-9a-zA-Z]+",
                        ".*[^相属云不波眷所莫归]属",
                        ".{2,}[表]$",
                        "[^新][娘]$",
                        "^[^a-zA-Z][a-zA-Z]+$",
                        ".+的反应$",
                        ".*[-]{2,}.*"
                }
        );

        if (path == null)
            return config;
        StringBuffer buffer = new StringBuffer();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
//              如果匹配到空行
                line = line.trim();
                if (line.length() < 1 || line.startsWith("#"))
                    continue;
                buffer.append(line);
                buffer.append('\n');

            }
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        config.Parse(buffer.toString().split("\n"));
        return config;
    }

    // 把文本文件读取为HashSet<String>，每行符合长度的文本为一个元素
    public static Set<String> ReadWords(String path, int wordLengthMin) {
        Set<String> words = new HashSet<>();
        if (path == null)
            return words;

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
//              如果匹配到空行
                if (line.length() < wordLengthMin)
                    continue;
                words.add(line.trim());
            }
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return words;
    }

    public static Set<String> ReadWords(String path) {
        return ReadWords(path, 2);
    }


    // 把Set<String>写入到指定文件中
    public static void WriteList(Set<String> keys, String path, boolean auto_delete, boolean show_log) throws Exception {
        File file = new File(path);

        if (keys.size() > 0) {
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
//                    System.out.println("[Done]Delete " + file.getPath());
                }
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        int counter = 0;

        for (String s : keys) {
            try {
                fileOutputStream.write('\n');
                fileOutputStream.write(s.getBytes());
                counter++;
                if (show_log)
                    System.out.println(Thread.currentThread().getName() + " " + counter + "/" + keys.size() + "  " + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileOutputStream.close();
        System.out.println(new Date() + " [Done] size=" + counter + " \t" + path);
    }

    // 把List<String>写入到指定文件中
    public static void WriteList(List<String> keys, String path, boolean auto_delete, boolean show_log) throws Exception {
        File file = new File(path);

        if (keys.size() > 0) {
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
//                    System.out.println("[Done]Delete " + file.getPath());
                }
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        int counter = 0;

        for (String s : keys) {
            try {
                fileOutputStream.write('\n');
                fileOutputStream.write(s.getBytes());
                counter++;
                if (show_log)
                    System.out.println(Thread.currentThread().getName() + " " + counter + "/" + keys.size() + "  " + s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileOutputStream.close();
        System.out.println(new Date() + " [Done] size=" + counter + " \t" + path);
    }


    //    把string写入到指定文件
    public static boolean Write(String path, StringBuffer content, boolean auto_delete) throws Exception {

        File file = new File(path);
        if (file.exists() && auto_delete) {
            if (file.isFile())
                file.delete();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }

    public static void Write2(String path, String str, boolean append_file) {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(path, append_file), StandardCharsets.UTF_8);
            oStreamWriter.append(str);
            oStreamWriter.append('\n');
            oStreamWriter.close();
//            System.out.println(new Date().toString() + " [Done] white2 \t" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // v2输出
    public static void Write2(String path, StringBuffer buffer, boolean append_file) {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(path, append_file), StandardCharsets.UTF_8);
            oStreamWriter.append(buffer);
            oStreamWriter.append('\n');
            oStreamWriter.close();
//            System.out.println(new Date().toString() + " [Done] white2 \t" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // v2输出
    public static void Write(String path, List<UserDict> list, boolean append_file) {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(new FileOutputStream(path, append_file), StandardCharsets.UTF_8);
            for (UserDict item : list) {
                oStreamWriter.append(item.full);
                oStreamWriter.append('\n');
            }
            oStreamWriter.close();
            System.out.println(new Date() + " [Done] size=" + list.size() + " \t" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean WriteFile(String path, StringBuffer content) throws Exception {

        File file = new File(path);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }

    public static synchronized void write(FileOutputStream fileOutputStream, String content) throws Exception {
        fileOutputStream.write(content.getBytes());
    }

    // 调用OpenCC完成文件简繁转换
    public static void OpenCC(String inputPath, String outputPath, String openccPath, String openccConfig) {
        String configPath = openccConfig;
        if(!new File(openccConfig).exists())
            configPath =  openccPath + File.separator + openccConfig;
        String command = (openccPath + File.separator + "opencc -i " + inputPath + " -o " + outputPath + " -c " +configPath);

        System.out.println(new Date() + " exec OpenCC, Command = " + command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedInputStream bis = new BufferedInputStream(
                    process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(bis));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
            if (process.exitValue() != 0) {
                System.out.println(new Date() + " exec OpenCC error!");
            }

            bis.close();
            br.close();

            System.out.println(new Date() + " exec OpenCC finish");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
