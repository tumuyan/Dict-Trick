package com.tumuyan.dictspider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

//放弃
public class UTF32Spider {
    private static boolean debug = true;

    public static void main(String[] args) {

        List<String> input_files = new ArrayList<>();
        List<String> ref_files = new ArrayList<>();

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
            } else if (args[i] == "-r") {
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
                path = "A:\\ProjectPython\\liangfen.dict.yaml";
//                path = "A:\\ProjectPython\\utf16test";
                if(path_w=="")
                path_w = path.replace(".dict.yaml", "")+".pinyin.dict.yaml";
            }
        } else if (path_w == "") {
            path = input_files.get(0).replace(".dict.yaml", ".pinyin.dict.yaml");
            if(path_w=="")
                path_w = path.replace(".dict.yaml", "")+".pinyin.dict.yaml";
        }

        if (path_w == "") {
            File file = new File(path_w);
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
                    System.out.println("[Done]Delete " + path_w);
                }
            }
        }

//        ArrayList<String> keys=new ArrayList<>(),key_r=new ArrayList<>();

        Set<Integer> set = new HashSet<>(),set_r = new HashSet<>();

        for (String p : ref_files) {
            set_r = (ReadFileSteam(p, set_r));
        }

        if (input_files.size() < 2) {
            set = ReadFileSteam(path, new HashSet<>());
        } else {
            for (String p : input_files) {
                set = (ReadFileSteam(p, set));
            }
        }


        try {
            process(set, path_w);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finish");
    }

// path_w为空时，读取path每一行文本,如果包含tab，把第一个字到keys中；并返回key
//    path_w不为空时，把带拼音的写入path_w并返回key


    public static Set<Integer> ReadFileSteam(String path, Set<Integer> set){

        try{
                File file = new File(path);
//                FileInputStream fis = new FileInputStream(file);
//                InputStreamReader inReader = new InputStreamReader(fis, "gbk");
//                FileOutputStream fos = new FileOutputStream(file);
//                OutputStreamWriter outReader = new OutputStreamWriter(fos, "utf-8");
            //建立链接
            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuffer sBuffer = new StringBuffer();

            boolean newline = true;

            int m = 0;
            int j = 0;

            String str="";
            for (int i=0; i<str.length() ; i++)
            {
                char n = str.charAt(i);
                if(newline){
//                    新行，对第一个字进行识别。
                    if(n!='\n'){
                        if(n>=0xD800 && n<=0xDBFF){
                            m=(n-0xd800)<<10;
                        }else if(n>=0xDC00 && n<=0xDFFF){
                            System.out.println("code:"+	(m+n-0xDC00+0x10000));
                            newline=false;
                        }else if(n>0x2e80){
                            System.out.println("code:"+	n);
                            newline=false;
                        }else{
                            newline=false;
                        }
                    }
                }else if(n=='\n')
                    newline = true;
            }


            int n = 0;
            while (n != -1) //当n不等于-1,则代表未到末尾
            {
                n = fileInputStream.read();//读取文件的一个字节(8个二进制位),并将其由二进制转成十进制的整数返回

                if(newline){
//                    新行，对第一个字进行识别。
                    if(n!='\n'){
                        if(n>=0xD800 && n<=0xDBFF){
                            m=(n-0xd800)<<10;
                        }else if(n>=0xDC00 && n<=0xDFFF){
                            set.add(m+n-0xDC00+0x10000);
                            newline=false;
                        }else if(n>0x2e80){
                            set.add(n);
                            newline=false;
                        }else{
                            newline=false;
                        }
                    }
                }else if(n=='\n')
                    newline = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return set;
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


    public static void process(Set<Integer> keys, String path) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(1);

        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        AtomicInteger counter = new AtomicInteger();

        for (int i : keys) {
            executor.execute(() -> {

                try {
                     char[] chars = Character.toChars(i);
                     String s = new String(chars);

/*
   byte[] b = s.getBytes(StandardCharsets.UTF_8);
                    StringBuilder buf = new StringBuilder(bytes.length * 2);
                    for(byte b : bytes) { // 使用String的format方法进行转换
                        buf.append(String.format("%02x", new Integer(b & 0xff)));
                    }
*/
//                    String str = query_zdict(s, 0);
                    String str = query_gxds(s,0);
                    write(fileOutputStream, str);
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

            String str = "";

            for (Element e : py) {
                str = str + string + e.text().trim();
            }

            return str;

        } catch (Exception e) {
            e.printStackTrace();
            return query_zdict(s, i + 1);
        }

    }



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

            String str = "";
            String blk = "";

            for (Element e : py) {
                if(e.text().contains("拼音：")){
                    blk = e.text().replace("拼音[：:]","");
                    break;
                }
                str = str + string + e.text().trim();
            }

            String[] p = blk.split(",，");

            for(String ps:p){
                str = str + string + ps.trim();
            }

            return str;

        } catch (Exception e) {
            System.out.print("Error: s="+s);
            e.printStackTrace();
            return query_gxds(s, i + 1);
        }

    }


}
