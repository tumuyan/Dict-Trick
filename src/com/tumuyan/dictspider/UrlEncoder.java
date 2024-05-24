package com.tumuyan.dictspider;


import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;


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
        for (String pair : pairs) {
            int pos = pair.indexOf("=");
            if (pos == -1) continue;
            String argname = pair.substring(0, pos);
            String value = pair.substring(pos + 1);
            value = URLEncoder.encode(value, StandardCharsets.UTF_8);
            map.put(argname, value);
        }
        return map;
    }

    //将map转化为指定的String类型
    public static String TransMapToString(Map map){
        Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (Entry)iterator.next();
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
            map.put(items.nextToken(), items.hasMoreTokens() ? items.nextToken() : null))
            items = new StringTokenizer(entrys.nextToken(), "=");
        return map;
    }
}