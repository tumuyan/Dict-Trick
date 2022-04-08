package com.tumuyan.dictspider;

import java.util.*;

import static com.tumuyan.dictspider.Utils.Write;

// 考虑到重复读写Map<Integer,List>存在效率问题,使用了穷举，并限制了分组数量。
public class UserDB {

    private List<UserDict>
            c10 = new ArrayList<>(),
            c9 = new ArrayList<>(),
            c8 = new ArrayList<>(),
            c7 = new ArrayList<>(),
            c6 = new ArrayList<>(),
            c5 = new ArrayList<>(),
            c4 = new ArrayList<>(),
            c3 = new ArrayList<>(),
            c2 = new ArrayList<>(),
            c1 = new ArrayList<>(),
            c0 = new ArrayList<>(),
            a = new ArrayList<>(),
            b = new ArrayList<>(),
            d = new ArrayList<>(),
            e = new ArrayList<>();

    private List<Integer> countGroup;
    private Map<Integer, Integer> groupMap;
    private Map<String, List<String>> wordMap;
    private Set<String> passbySet;
    private int groupSize;
    private int countMax = Integer.MIN_VALUE;
    private List<String> blacklistRegex = new ArrayList<>();

    public List<UserDict> getC0() {
        return c0;
    }

    public UserDB() {
        this.countGroup = new ArrayList<>();
        countGroup.add(Integer.MAX_VALUE);
        groupMap = new HashMap<>();
        wordMap = new HashMap<>();
        passbySet = new HashSet<>();
        groupSize = 0;
    }

    public UserDB(List<Integer> countGroup) {
        this.countGroup = countGroup;
        groupSize = countGroup.size();
        countGroup.add(Integer.MAX_VALUE);
        if (groupSize > 0)
            countMax = countGroup.get(groupSize - 1);
        groupMap = new HashMap<>();
        wordMap = new HashMap<>();
        passbySet = new HashSet<>();
    }

    public void add(UserDB db) {
        // 只合并第一组
        c0.addAll(db.c0);
    }

    public void addRefer(List<UserDict> list) {
        for (UserDict item : list) {
            String word = item.word;
            List<String> l;
            if (wordMap.containsKey(word)) {
                l = wordMap.get(word);
            } else {
                l = new ArrayList<>();
            }
            l.add(item.code);
            wordMap.put(word, l);
        }
    }

    public void addWhiteList(List<UserDict> list) {
        for (UserDict item : list) {
//            passbySet.add(item.word+'\t'+item.full);
            passbySet.add(item.word);
        }
    }

    public void addBlacklistRegex(List<String> blacklistRegex) {
        this.blacklistRegex.addAll(blacklistRegex);
    }


    public void add(String record) {
        add(record, false, false);
    }

    public void add(String record, boolean isSchemaDict, boolean addToC0) {
        UserDict o = new UserDict(record, isSchemaDict);
        if (isSchemaDict || addToC0) {
            c0.add(o);
            return;
        }

        if (passbySet.contains(o.word)) {
            e.add(o);
            return;
        }

        int index;
        if (o.count != 0) {
            if (wordMap.containsKey(o.word)) {
                List<String> list = wordMap.get(o.word);
                for (String s : list) {
                    if (s.equals(o.code)) {
                        a.add(o);
                        return;
                    }
                }
                b.add(o);
                return;
            }

            for (String s : blacklistRegex) {
                if (o.full.matches(s)) {
                    d.add(o);
                    return;
                }
            }

            if (o.count > countMax) {
                if (groupSize == 0) c0.add(o);
                else if (groupSize == 1) c1.add(o);
                else if (groupSize == 2) c2.add(o);
                else if (groupSize == 3) c3.add(o);
                else if (groupSize == 4) c4.add(o);
                else if (groupSize == 5) c5.add(o);
                else if (groupSize == 6) c6.add(o);
                else if (groupSize == 7) c7.add(o);
                else if (groupSize == 8) c8.add(o);
                else if (groupSize == 9) c9.add(o);
                else if (groupSize == 10) c10.add(o);
                return;
            }

            if (groupMap.containsKey(o.count)) {
                index = groupMap.get(o.count);
            } else {
                for (index = 0; index < groupSize; index++) {
                    if (countGroup.get(index) > o.count) {
                        break;
                    }
                }
                groupMap.put(o.count, index);
            }

            if (index == 0) c0.add(o);
            else if (index == 1) c1.add(o);
            else if (index == 2) c2.add(o);
            else if (index == 3) c3.add(o);
            else if (index == 4) c4.add(o);
            else if (index == 5) c5.add(o);
            else if (index == 6) c6.add(o);
            else if (index == 7) c7.add(o);
            else if (index == 8) c8.add(o);
            else if (index == 9) c9.add(o);
            else if (index == 10) c10.add(o);
        }
    }


    //    部分词条不在废词列表内，但是也不在修复列表中。这些词条大概率后续会列入废词列表中
    public void WriteWordByCountGroup(String path_w) {
        System.out.println(new Date() + " WriteWordByCountGroup...");
/*

        if (c0.size() > 0) Write(path_w + ".c0.txt", c0, false);
        if (c1.size() > 0) Write(path_w + ".c1.txt", c1, false);
        if (c2.size() > 0) Write(path_w + ".c2.txt", c2, false);
        if (c3.size() > 0) Write(path_w + ".c3.txt", c3, false);
        if (c4.size() > 0) Write(path_w + ".c4.txt", c4, false);
        if (c5.size() > 0) Write(path_w + ".c5.txt", c5, false);
        if (c6.size() > 0) Write(path_w + ".c6.txt", c6, false);
        if (c7.size() > 0) Write(path_w + ".c7.txt", c7, false);
        if (c8.size() > 0) Write(path_w + ".c8.txt", c8, false);
        if (c9.size() > 0) Write(path_w + ".c9.txt", c9, false);
        if (c10.size() > 0) Write(path_w + ".c10.txt", c10, false);
*/

        if (a.size() > 0) Write(path_w + ".match_dict.txt", a, false);
        if (b.size() > 0) Write(path_w + ".different_code.txt", b, false);
        if (d.size() > 0) Write(path_w + ".match_regex.txt", d, false);
        if (e.size() > 0) Write(path_w + ".match_whitelist.txt", e, false);
        if (c0.size() > 0) Write(path_w + ".c0." + "-" + countGroup.get(0) + ".txt", c0, false);
        if (c1.size() > 0) Write(path_w + ".c1." + countGroup.get(0) + "-" + countGroup.get(1) + ".txt", c1, false);
        if (c2.size() > 0) Write(path_w + ".c2." + countGroup.get(1) + "-" + countGroup.get(2) + ".txt", c2, false);
        if (c3.size() > 0) Write(path_w + ".c3." + countGroup.get(2) + "-" + countGroup.get(3) + ".txt", c3, false);
        if (c4.size() > 0) Write(path_w + ".c4." + countGroup.get(3) + "-" + countGroup.get(4) + ".txt", c4, false);
        if (c5.size() > 0) Write(path_w + ".c5." + countGroup.get(4) + "-" + countGroup.get(5) + ".txt", c5, false);
        if (c6.size() > 0) Write(path_w + ".c6." + countGroup.get(5) + "-" + countGroup.get(6) + ".txt", c6, false);
        if (c7.size() > 0) Write(path_w + ".c7." + countGroup.get(6) + "-" + countGroup.get(7) + ".txt", c7, false);
        if (c8.size() > 0) Write(path_w + ".c8." + countGroup.get(7) + "-" + countGroup.get(8) + ".txt", c8, false);
        if (c9.size() > 0) Write(path_w + ".c9." + countGroup.get(8) + "-" + countGroup.get(9) + ".txt", c9, false);
        if (c10.size() > 0) Write(path_w + ".c10." + countGroup.get(9) + "-" + countGroup.get(10) + ".txt", c10, false);
//        if (0.size() > 0) Write(path_w + "c" + countGroup.get(10) + "-.txt", c10, false);


    }
}
