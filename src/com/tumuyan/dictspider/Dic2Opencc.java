package com.tumuyan.dictspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// 把制作的单字字典，转写为opencc格式。
// 多音字处理为  xx  读音&nbsp读音
public class Dic2Opencc {
    private static boolean debug = true;

    public static void main(String[] args) {

        List<String> input_files = new ArrayList<>();
        List<String> ref_files = new ArrayList<>();
        String path_w = "";
        boolean auto_delete = false;

        for (int i = 0; i < args.length; i++) {
            if (Objects.equals(args[i], "-h")) {
                System.out.println("help\n");
            } else if (Objects.equals(args[i], "-a")) {
                auto_delete = true;
            }
            if (Objects.equals(args[i], "-o")) {
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
            } else if (Objects.equals(args[i], "-r")) {
                // 未实现
                if (!ref_files.contains(args[i])) {
                    File file = new File(args[i]);
                    if (!file.exists())
                        System.out.println("[Err]Ref File not exist: " + args[i]);
                    else
                        ref_files.add(args[i]);
                }
            } else {
                if (!input_files.contains(args[i])) {
                    File file = new File(args[i]);
                    if (!file.exists())
                        System.out.println("[Err]File not exist: " + args[i]);
                    else
                        input_files.add(args[i]);
                }
            }
        }

        String path = "";

        if (input_files.size() < 1) {
            if (debug) {
                path = "A:\\ProjectPython\\liangfen.pinyin.dict.yaml";
//                path = "A:\\ProjectPython\\utf16test";
                if(path_w.equals(""))
                    path_w = path.replace(".dict.yaml", "")+".opencc.txt";
            }
        } else if (path_w.equals("")) {
            path = input_files.get(0);
            if(path_w.equals(""))
                path_w = path.replace(".dict.yaml", "")+".opencc.txt";
        }

        if (path_w.equals("")) {
            File file = new File(path_w);
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
                    System.out.println("[Done]Delete " + path_w);
                }
            }
        }

        ArrayList<String> keys=new ArrayList<>(),key_r=new ArrayList<>();

        for (String p : ref_files) {
            key_r = (ReadFile(p, "", key_r));
        }

        if (input_files.size() < 2) {
            keys = ReadFile(path, path_w, new ArrayList<String>());
        } else {
            for (String p : input_files) {
                keys = (ReadFile(p, path_w, keys));
            }
        }

        System.out.println("Finish");
    }

// path_w为空时，读取path每一行文本,如果包含tab，把第一个字到keys中；并返回key
//    path_w不为空时，把带拼音的写入path_w并返回key
    public static ArrayList<String> ReadFile(String path, String path_w, ArrayList<String> keys) {

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;
            String li = "";

            StringBuffer buffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
//              字 tab 编码 至少长度为3
                if (line.length() < 3)
                    continue;
                if(!line.contains("\t"))
                    continue;

//                当使用高代理-低代理时，占2byte；否则匹配字+tab
                if(li.contains(line.substring(0,2))){
                    li=li+line.replaceFirst(".+\t","&nbsp");
                }else{
                    buffer.append('\n');
                    buffer.append(li);
                    li=line;
                }
            }

            buffer.append('\n');
            buffer.append(li);

            fileInputStream.close();

                WriteFile(path_w, buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;

    }


    public static boolean WriteFile(String path, StringBuffer content) throws Exception {

        File file = new File(path);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write('\n');
        fileOutputStream.write(content.toString().getBytes());
        fileOutputStream.close();
        return true;
    }


}
