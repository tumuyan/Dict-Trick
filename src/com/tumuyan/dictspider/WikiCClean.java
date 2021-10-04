package com.tumuyan.dictspider;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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


public class WikiCClean {

    public static class Dict {
        public Dict() {
            eng = new HashSet<>();
            chs = new HashSet<>();
            mix = new HashSet<>();
            chn = new HashSet<>();
            suffix = new HashSet<>();
        }

        public Dict(Dict dict) {
            eng = dict.eng;
            chs = dict.chs;
            mix = dict.mix;
            chn = dict.chn;
        }

        public void add(Dict dict) {
            eng.addAll(dict.eng);
            chs.addAll(dict.chs);
            mix.addAll(dict.mix);
            chn.addAll(dict.chn);
        }

        public void add(String str) {
//            不可切分的字符有：[-・·&%']
//            必须切分的字符有： ~ _(空格) 括号

            str = regular(str);

            String[] strs = str.split("[ |｜!\"#$)*+(_\\.,:;\\\\\\^`~=?@/×÷\u2016-\u206f\u3000-\u303f\\s、\uD83D\uDE00—\ud8f6\udc4f ｟-｣]");

            for (int i = 0; i < strs.length; i++) {
                String s = strs[i]
                        .replaceFirst("(?<![a-zA-Z])[Ⅰ-ⅻIVX&-]{2,8}$", "")
                        .replaceFirst("[①-⒛Ⅰ-ⅻ]+$", "")
                        .replaceFirst("(?<=[座])[\u0370-\u03FFa-zA-Z].*$", "")
                        .replaceFirst("^([①-⒛Ⅰ-ⅻIVX&-]{2,8})?[-・·&%'\\s∀-⋿⌀-⸩]+", "")
                        .replaceFirst("[-・·&'─\\s]+$", "");


//                wiki词库不保留单字。
                if (s.length() < 2)
                    continue;

//               屏蔽其中一字为数字/英语、特殊符号的双字词
                if (s.matches("(.[0-z⌀-⸩]|[0-z⌀-⸩].)"))
                    continue;

//                屏蔽 除数字外只剩余1个字的词 （包括 22娘 (兼处理 是○
                if (s.matches("[0-9○①-⒛]+.") || s.matches(".[0-9○①-⒛]+([娘姬]?)"))
                    continue;

//                剔除低质量带连字符的词 如水-Ⅱ
//                if(s.matches("^.{0,2}-.{0,2}$"))
//                    continue;
//                  剔除  波江座ο²
                if (s.matches(".{0,2}[³²□-].{0,2}"))
                    continue;

//             剔除低质量  在glog中匹配 ^.·.\s   手动维护 简·爱 等少部分，
                if (s.matches(".·."))
                    continue;

//                过滤其他语言
                if (s.matches(".*[ぁ-\u31BF].*"))
                    continue;

//                过滤序数编号
                if (s.matches(".*[-第][0-9○零一二三四五六七八九十〇百千Ⅰ-ⅻ].*"))
                    continue;

                if (s.matches(".*第[iI]+.*"))
                    continue;

                if (s.matches(".+(列表|一览)[0-9]?$"))
                    continue;

                if (s.matches(".{3,100}人物$"))
                    continue;

                if (s.matches(".*[0-9](年|公路|大厦|弄|馆|條站|快线|手槍|条站|手枪|步枪)"))
                    continue;

//                匹配 xxx3季 北京国安足球俱乐部1997赛季  类似词条
                if (s.matches(".+\\d.*(季|甲|线|線)"))
                    continue;
//                匹配 台5乙線
                if (s.matches(".+\\d+.?[甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥快短字干热号总度路](线|線)?"))
                    continue;

                if (s.matches(".*[0-9零一二三四五六七八九十〇○百千][年月日周号號张路线綫線台章所军軍師师团團厂厰區区期次版旅街型届屆回度集话首A-z].*"))
                    continue;
                char c = s.charAt(0);
//                不保留数字开头的词。
                if (c <= '9' && c >= '0')
                    continue;
/*

                if(s.matches(".+[Oo]{2,20}[^z-zA-Z].*")){
                    chs.add(s.replaceAll("(?![a-zA-Z])[Oo]{2,20}(?=[^z-zA-Z])","Oo")
                            .replaceAll("(?![a-zA-Z])[Xx]{2,20}(?=[^z-zA-Z])","Xx"));
                }
*/
                // 匹配萌娘百科的成句格式, 如果只有开头有OO，直接剔除
                if (s.matches("[Oo○]{2,20}[^a-zA-Z][^Oo○]*")) {
                    String ss = s.replaceFirst("^[Oo○]+[,，\\s]*", "");
                    if (ss.length() > 1)
                        chs.add(ss);
                    continue;
                }
                // 匹配萌娘百科的成句格式, 如果只有结尾有OO，直接剔除
                if (s.matches("[^Oo○]*[^a-zA-Z][Oo○]{2,20}")) {
                    String ss = s.replaceFirst("[,，\\s]*[Oo○]+$", "");
                    if (ss.length() > 1)
                        chs.add(ss);
                    continue;
                }
                // 匹配萌娘百科的成句格式
                if (s.matches("(.*[^a-zA-Z])?[Oo○]{2,20}[^a-zA-Z].*")) {
                    chs.add(s.replaceAll("[Oo○]{2,20}", "Oo")
                            .replaceAll("[Xx]{2,20}", "Xx"));
                    continue;
                }

                // 匹配萌娘百科的成句格式, 如果只有开头有OO，直接剔除
                if (s.matches("○[^a-zA-Z][^Oo○]*")) {
                    String ss = s.substring(1);
                    if (ss.length() > 1)
                        chs.add(ss);
                    continue;
                }

                if (s.length() == 2) {
                    char d = s.charAt(1);

                    if ((c < 0x4e00 || c > 0x9fff) && (d < 0x4e00 || d > 0x9fff))
                        continue;
                }

                if (s.matches("[0-z\\-·・&%'À-\u2DFF]+")) {
                    eng.add(s);
                    continue;
                } else if (c >= 'A' && c <= 'z') {


//                   去除 eng_eng汉字 中的第二个eng的影响
                    if (i > 0) {

                        if (strs[i - 1].matches("[A-z]+")) {
                            String ss = s.replaceFirst("^[A-z·・]+", "");
                            if (ss.length() < 2)
                                mix.add(s);
                            else
                                s = ss;
                        } else {
                            mix.add(s);
                            continue;
                        }
                    } else {
                        mix.add(s);
                        continue;
                    }
                }

                if (s.matches(".*[^0-9a-zA-Z]+[0-9a-zA-Z-]+")) {
                    chn.add(s);
                } else {
                    if (s.matches(".+-.+")) {
//                    处理 哈利·波特 哈利—波特 重复出现   to do
                    }
                    chs.add(s);
                }

            }
        }

        private Set<String> eng, chs, mix, chn, suffix;


        public Set<String> getChs() {

            suffix = new HashSet<>();

            Set<String> set = new HashSet<>();
            set.addAll(chs);

            if (chn.size() > 0) {

                Set<String> pre = new HashSet<>();
                Set<String> str = new HashSet<>();
                Set<String> suf = new HashSet<>();

                HashMap<String, ArrayList<String>> map = new HashMap<>();
                Map<String, Integer> counter = new HashMap<>();
                for (String s : chn) {

//               过滤词尾的数字。
                    String s1 = s.replaceFirst("(\\d+)$", "");
                    if (set.contains(s1))
                        continue;

//              字母需要谨慎处理，避免类似 卡拉OK 的词条过杀
                    String s11 = s.replaceFirst("[-・·&']?[0-9a-zA-Z-]+$", "");
                    String s12 = s.replace(s11, "");
                    int count = 1;
                    if (counter.containsKey(s12)) {
                        count = 1 + counter.get(s12);
                    }
                    counter.put(s12, count);

                    if (set.contains(s11)
//                            || pre.contains(s11)
                    ) {
                        continue;
                    }

                    if (s1 == s11)
                        set.add(s1);
                    else
                        str.add(s1);

                    pre.add(s11);

//                String s2 = s.replace(s1, "");
//                if (s2.replaceFirst("\\d", "").length() > 1)
//                    suffix.add(s2);



/*
                ArrayList<String> v;

                if (map.containsKey(s1)) {
                    v = map.get(s1);
                } else {
                    v = new ArrayList<>();
                }

                v.add(s);
                map.put(s1, v);*/
                }

/*            for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                String s = entry.getKey();
                ArrayList<String> v = entry.getValue();
                if (v.size() < 3) {
                    for (String s1 : v) {
                        set.add(s1);
                    }
                } else {
                    set.add(s);
                }
            }*/

                for (Map.Entry<String, Integer> entry : counter.entrySet()) {
                    String s = entry.getKey();
                    int v = entry.getValue();
                    suffix.add(s + "\t" + v);
                    if (v < 9) {
                        suf.add(s);
                    }
                }

//            这里有bug，应该。没有输出log
                for (String s : str) {
                    String s11 = s.replaceFirst("[-・·&']?[0-9a-zA-Z-]+$", "");
                    String s12 = s.replace(s11, "");
                    if (suf.contains(s12))
                        set.add(s);
                    else
                        suffix.add(s12 + "\t\t" + s);
                }

            }

            Set<String> cache = new HashSet<>();
            cache.addAll(set);
            for (String s : set) {
                if (s.matches(".+-.+")) {
                    cache.remove(s.replace("-", ""));
                    cache.remove(s.replace("-", "·"));
                } else if (s.matches(".+·.+")) {
                    cache.remove(s.replace("·", ""));
                }

                if (s.matches(".*[A-Z][a-z]+.*")) {
                    cache.remove(s.toLowerCase());
                    cache.remove(s.toUpperCase());
                } else if (s.matches(".*[A-Z]+.*")) {
                    cache.remove(s.toLowerCase());
                }
            }

            return cache;
        }

        public Set<String> getSuffix() {
//            suffix在生成chs时才会生成
            return suffix;
        }

        public Set<String> getEng() {
            return eng;
        }

        public Set<String> getMix() {
            return mix;
        }

        public String regular(String input) {
            char[] c = input.trim().toCharArray();
            for (int i = 0; i < c.length; i++) {
//｢ ff62
                if (c[i] > 65280 && c[i] < 65375) {
                    //其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                    c[i] = (char) (c[i] - 65248);
                } else if (c[i] == 12288) {
                    //全角空格为12288，半角空格为32
                    c[i] = (char) 32;
                    continue;
                } else if (c[i] >= 0x2010 && c[i] <= 0x2015 || c[i]=='─') {
                    // 多个编码的'-'进行统一转换
                    c[i] = '-';
                } else if (c[i] == ',' || c[i] == '，') {
                    // 處理非中文之间的逗号
                    if (i != 0 && i < input.length() - 1) {
                        if (c[i - 1] >= 0x4E00 && c[i - 1] <= 0x9FFF && c[i + 1] >= 0x4E00 && c[i + 1] <= 0x9FFF) {
                            c[i] = '，';
                            continue;
                        }
                    }
                    c[i] = '_';
                }
            }
            return new String(c).replaceAll("([\u4e00-\u9fff])([a-zA-Z-]+[_\\s])", "$1_$2")
                    .replaceAll("([_\\s][a-zA-Z-]+)([\u4e00-\u9fff])", "$1_$2");
        }
    }


    private static boolean debug = true;

    public static void main(String[] args) {

        List<String> input_files = new ArrayList<>();

        String path_w = "";
        boolean auto_delete = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-h") {
                System.out.println("help\n");
            } else if (args[i] == "-a") {
                auto_delete = true;
            } else if (args[i] == "-e") {
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
                path = "A:\\ProjectPython\\zhwiki-20211001-all-titles-in-ns0";
                //   path = "A:\\ProjectPython\\wiki.test.char.txt";
//                path = "A:\\ProjectPython\\wikitest.2.txt";
//                path = "A:\\ProjectPython\\moegirl.txt";
                path_w = path.replace(".dict.txt", "");
            }
        } else if (path_w == "") {
            path = input_files.get(0).replace(".dict.txt", ".pinyin.dict.txt");
            path_w = path.replace(".dict.txt", "");
        }

        Dict dict = new Dict();
        if (input_files.size() < 2) {
            dict = ReadFile(path);
        } else {
            for (String p : input_files) {
                dict.add(ReadFile(p));
            }
        }

        OutputWords( dict, path_w, auto_delete,true);
    }


    public static void OutputWords(Dict dict,String path_w,boolean auto_delete,boolean t2s){
        try {
            Set<String> chs = dict.getChs();

            if(t2s){
                WriteList(chs, path_w + ".cn.dict.txt", auto_delete, false);

                OpenCC_T2S(path_w + ".cn.dict.txt",path_w + ".chs.dict.txt","A:\\EBookTools\\OpenCC\\bin");
                chs = ReadWords(path_w + ".chs.dict.txt");
                chs.removeAll(ReadWords());
                WriteList(chs, path_w + ".chs2.dict.txt", auto_delete, false);

            }else{
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



    public static Set<String> ReadWords(){
        Set<String> words = new HashSet<>();

        words.addAll(ReadWords("A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词.txt"));
        words.addAll(ReadWords("A:\\ProjectOthers\\rime-pinyin-simp\\others\\废词-村县镇乡路村縣鎮鄉路.txt"));
        return words;
    }


    public static Set<String> ReadWords(String path) {
        Set<String> words = new HashSet<>();
        if (path == null)
            return words;

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
//              如果匹配到空行
                if (line.length() < 2)
                    continue;
                words.add(line.trim());
            }
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public static void WriteList(Set<String> keys, String path, boolean auto_delete, boolean show_log) throws Exception {
        File file = new File(path);

        if (keys.size() > 0) {
            if (file.exists()) {
                if (auto_delete) {
                    file.delete();
                    System.out.println("[Done]Delete " + file.getPath());
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
        System.out.println("[Done] size=" + counter + " \t" + path);
    }


    public static void OpenCC_T2S(String input, String output, String opencc) {
        String command = (opencc + File.separator  + "opencc -i " + input + " -o " + output + " -c " + opencc+File.separator+ "t2s.json");

        System.out.println("exec OpenCC\nCommand = "+command);

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
                System.out.println("error!");
            }

            bis.close();
            br.close();

            System.out.println("finish OpenCC");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
