package com.tumuyan.dictspider;

import java.io.UnsupportedEncodingException;

public class StringTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
//        第一个char型变量的范围称为“高代理部分”（high-surrogates range,从"uD800到"uDBFF，共1024个码位）,
//        第二个char型变量的范围称为low-surrogates range（从"uDC00到"uDFFF，共1024个码位）

//        （注：BMP内的字符编码，不包含从U+D800到U+DFFF的预留码位。这些预留码位就恰好用于扩展字符编码）
//        如果U<0x10000，U的UTF-16编码就是U对应的16位无符号整数。
//        如果U≥0x10000，
//        我们先计算U’=U-0x10000，
//        然后将U’写成二进制形式：yyyy yyyy yyxx xxxx xxxx，
//        U的UTF-16编码（二进制）就是：110110yyyyyyyyyy 110111xxxxxxxxxx。
//        这两个字符就称为surrogate pair（代理对）。第一个代理字符为16位编码，范围为U+D800到U+DFFF，第二个代理字符也是一个16位编码，范围为U+DC00 to U+DFFF。
        // 😂 0x1f602
        String str = "\uD83D\uDE02\uD83D\uDE02";
        byte[] b= str.getBytes("Unicode");
        System.out.println(b.length);

        for(int i=0;i<str.length();i++){
           System.out.println(Integer.toBinaryString(str.charAt(i)));

        }
/*
        utf16高低代理->unicode
        if(n>=0xD800 && n<=0xDBFF){
            m=(n-0xd800)<<10;
        }else if(n>=0xDC00 && n<=0xDFFF){
            set.add(m+n-0xDC00+0x10000);
            newline=false;
        }*/
    }
}
