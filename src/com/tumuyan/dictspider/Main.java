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

import static com.tumuyan.dictspider.Utils.WriteFile;

// 用于读取两分词库每一行的第一个字，并从在线字典获取拼音，转写为大字集拼音。
// 已经为多音字添加&，需要用正则转换 \t([^\s]+\&[^\s]+) 替换为 \t\[$1\]
public class Main {
    private static final boolean debug = true;

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

        if (input_files.isEmpty()) {
            if (debug) {
                path = "A:\\ProjectPython\\liangfen.dict.yaml";
//                path = "A:\\ProjectPython\\utf16test";
                if(path_w.isEmpty())
                    path_w = path.replace(".dict.yaml", "")+".pinyin.dict.yaml";
            }
        } else if (path_w.isEmpty()) {
            path = input_files.get(0).replace(".dict.yaml", ".pinyin.dict.yaml");
            path_w = path.replace(".dict.yaml", "")+".pinyin.dict.yaml";
        }

        if (path_w.isEmpty()) {
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
            keys = ReadFile(path, "", new ArrayList<>());
        } else {
            for (String p : input_files) {
                keys = (ReadFile(p, "", keys));
            }
        }


        try {
            process(keys, path_w);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finish");
    }

// path_w为空时，读取path每一行文本,如果包含tab，把第一个字到keys中；并返回key
//    path_w不为空时，把带拼音的写入path_w并返回key
    public static ArrayList<String> ReadFile(String path, String path_w, ArrayList<String> keys) {
        boolean only_read = path_w.isEmpty();

        try {
            FileInputStream fileInputStream = new FileInputStream(path);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String line;

            StringBuffer buffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
//              如果匹配到空行
                if (line.isEmpty())
                    continue;

//                依据叶典列出的unicode范围 (http://yedict.com/zsts.htm)
//                大致判断字符归属汉字、部首及扩展A-E （抛弃扩展F/G）
                char c = line.charAt(0);
                String s = String.valueOf(c);
                if (c >= '\ud800' && c <= '\udbff') {
                    s = line.substring(0, 2);
                }

                if (!keys.contains(s)) {
                    keys.add(s);
                    System.out.println(s + " " + keys.size());
                }

/*            if ((c >= 0x2E80 && c <= 0xFAD9) || (c >= 0x20000 && c <= 0x2CEA1)) {
                String s = String.valueOf(c);
                if (!keys.contains(s)) {
                    keys.add(s);
                }
            }*/
            }

            fileInputStream.close();

            if (!only_read)
                WriteFile(path_w, buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;

    }





    public static void process(ArrayList<String> keys, String path) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(1);

        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        AtomicInteger counter = new AtomicInteger();

        for (String s : keys) {
            executor.execute(() -> {

                try {
//                    String str = query_zdict(s, 0);
                    String str = query_gxds(s,0);
                    Utils.write(fileOutputStream, str);
                    counter.getAndIncrement();
                    System.out.println(Thread.currentThread().getName() + " " + counter + "/" + keys.size() + "  " + str);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        executor.shutdown();
//        fileOutputStream.close();
        System.out.println("all thread complete");
    }

// 从zdict查询读音
    public static String query_zdict(String s, int i) throws Exception {
        String string = "\n" + s + "\t";
        if (i > 3) {
            return string;
        }
        if (i > 0) {
            Thread.sleep(20000 * i);
        }
        try {
            Document doc = Jsoup.connect("https://www.zdic.net/hans/" + s).get();
            Elements py = doc.select("td.z_py span.song");

            StringBuilder str = new StringBuilder();

            for (Element e : py) {
                str.append(string).append(e.text().trim());
            }

            return str.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return query_zdict(s, i + 1);
        }

    }

// 从国学大师网查询读音
    public static String query_gxds(String s, int i) throws Exception {
//        String url = "http://www.guoxuedashi.net/zidian/z42675l.html"
        char c = s.charAt(0);
        if(c<0x1000)
            return "";

        String url="http://www.guoxuedashi.net/zidian/"+Integer.toHexString(c)+ ".html";

        if (c >= '\ud800' && c <= '\udbff') {
            url = "http://www.guoxuedashi.net/zidian/so.php?kz=1&sokeyzi="+s;
        }

        String string = "\n" + s + "\t";
        if (i > 3) {
            return string;
        }
        if (i > 0) {
            Thread.sleep(20000 * i);
        }
        try {
            Document doc = Jsoup.connect(url).get();

            if(doc.head().html().matches(".{1,20}location.href=(.{10,120})")){
                url = doc.head().html().replaceFirst("(.*location.href=['\"])(.+)(['\"].+)","$2");
                if(url.charAt(0)=='/'){
                    url = "http://www.guoxuedashi.net/" + url;
                    doc = Jsoup.connect(url).get();
                }
            }

            Elements py = doc.select("table.zui  tbody tr td table tbody tr td");

            StringBuilder str = new StringBuilder();
            String blk = "";

            for (Element e : py) {
                if(e.text().contains("拼音：")){
                    blk = e.text().replace("拼音：","");
                    break;
                }
                str.append(string).append(e.text().trim());
            }

            String[] p = blk.split("[,，]");

            for(String ps:p){
                str.append(string).append(ps.trim());
            }

            return str.toString();

        } catch (Exception e) {
            System.out.print("Error: s="+s);
            e.printStackTrace();
            return query_gxds(s, i + 1);
        }

    }


}
