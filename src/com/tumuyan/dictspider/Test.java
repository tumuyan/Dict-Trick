package com.tumuyan.dictspider;

public class Test {
/*
qwertyuiopasdfghjklzxcvbnm1234567890
wertyuiopasdfghjklzxcvbnm1234567890
ertyuiopasdfghjklzxcvbnm1234567890
rtyuiopasdfghjklzxcvbnm1234567890
tyuiopasdfghjklzxcvbnm1234567890
* */
    public static void main(String[] args) {
        testAddDict();
    }

    public static  void testAddDict() {
        WikiCClean.Dict dict = new WikiCClean.Dict();
        dict.add("佐贺偶像是传奇LIVE~Fran Chou Chou大家一起喊出来~");
        dict.add("佐贺偶像是传奇LIVE~Fran Chou Chou大家一起欢呼~");
    }
}
