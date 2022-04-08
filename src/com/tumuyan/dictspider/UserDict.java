package com.tumuyan.dictspider;


public class UserDict {
    public String word;
    public String code;
    public int count;
    public String full;

    public UserDict(String str) {
        this(str, false);
    }

    public UserDict(String str, boolean isSchemaDict) {
        if (isSchemaDict) {

//  解析rime词库，词条在前，编码在后（且只需要词条和编码
            String[] strings = str.split("\t");
            if (strings.length >= 2) {
                word = strings[0];
                code = strings[1].trim();
            }
        } else {

//  解析用户同步后的词条
            full = str;
            if (str.startsWith("#"))
                return;

            String[] strings = str.split("\t");
            if (strings.length == 3) {
                word = strings[1];
                code = strings[0].trim();
                count = Integer.parseInt(strings[2].replaceFirst("^c=([-0-9]+).+", "$1"));
            }
        }

    }
}
