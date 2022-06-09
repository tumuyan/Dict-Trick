package com.tumuyan.dictspider;


import java.io.CharArrayWriter;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import java.nio.charset.IllegalCharsetNameException;

import java.nio.charset.UnsupportedCharsetException;

import java.security.AccessController;

import java.util.*;


public class UrlEncoder {


    //对url中的参数进行url编码
    public static String GetRealUrl(String str) {
        try {
            int index = str.indexOf("?");
            if (index < 0) return str;
            String query = str.substring(0, index);
            String params = str.substring(index + 1);
            Map map = GetArgs(params);
            //Map map=TransStringToMap(params);
            String encodeParams = TransMapToString(map);
            return query + "?" + encodeParams;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }

    //将url参数格式转化为map
    public static Map GetArgs(String params) throws Exception{
        Map map=new HashMap();
        String[] pairs=params.split("&");
        for(int i=0;i<pairs.length;i++){
            int pos=pairs[i].indexOf("=");
            if(pos==-1) continue;
            String argname=pairs[i].substring(0,pos);
            String value=pairs[i].substring(pos+1);
            value= URLEncoder.encode(value,"utf-8");
            map.put(argname,value);
        }
        return map;
    }

    //将map转化为指定的String类型
    public static String TransMapToString(Map map){
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (java.util.Map.Entry)iterator.next();
            sb.append(entry.getKey().toString()).append( "=" ).append(null==entry.getValue()?"":
                    entry.getValue().toString()).append (iterator.hasNext() ? "&" : "");
        }
        return sb.toString();
    }

    //将String类型按一定规则转换为Map
    public static Map TransStringToMap(String mapString){
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, "&"); entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "=");
        return map;
    }
}