package com.tumuyan.dictspider;


import java.util.*;

// 词条过滤和处理
public class Dict {
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

        String[] strs;
        if (str.matches(".*([\u4e00-\u9fff])[·・ᐧ･]([\u4e00-\u9fff]).*")) {
            strs = str.replaceAll("[·・ᐧ･]", "·")
                    .split("[ |｜!\"#$)*+(_— ｟-｣\\.,:;\\\\\\^`~=?@/×÷\u2016-\u206f\u3000-\u303f\\s、\uD83D\uDE00-\uD83E\uDFFF]");
        } else
            strs = str
                    .split("[ |｜!\"#$)*+(_— ｟-｣\\.,:;\\\\\\^`~=?@/×÷\u2016-\u206f\u3000-\u303f\\s、\uD83D\uDE00-\uD83E\uDFFF]");

        for (int i = 0; i < strs.length; i++) {

//              去除词条首末的编号、符号、序数等
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

//             剔除低质量  在glog中匹配 ^.[・·].\s   手动维护 简·爱 等少部分，
            if (s.matches(".[・·]."))
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

            if (s.matches(".*[0-9](年|公路|大厦|弄|馆|條站|快线|手槍|条站|手枪|步枪|洞)"))
                continue;

//                匹配 xxx3季 北京国安足球俱乐部1997赛季  类似词条
            if (s.matches(".*[^A-Za-z](\\d.*|[A-Za-z])(季|甲|线|線)"))
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
            // 举例： ○二代
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

            if (s.matches("[0-z\\-・·&%'À-\u2DFF]+")) {
                eng.add(s);
                continue;
            } else if (c >= 'A' && c <= 'z') {


//                   去除 eng_eng汉字 中的第二个eng的影响
                if (i > 0) {

                    if (strs[i - 1].matches("[A-z]+")) {
                        String ss = s.replaceFirst("^[A-z・·]+", "");
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
            } else if (c[i] >= 0x2010 && c[i] <= 0x2015 || c[i] == '─') {
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
                .replaceAll("([_\\s][a-zA-Z-]+)([\u4e00-\u9fff])", "$1_$2")
                .replace("\u200B","");
    }
}

