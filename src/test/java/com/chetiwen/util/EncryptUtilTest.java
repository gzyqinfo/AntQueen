package com.chetiwen.util;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;


public class EncryptUtilTest {
    @Test
    public void testEncript() throws UnsupportedEncodingException {

        // 使用基本编码
        String base64encodedString = Base64.getEncoder().encodeToString("CheTiWen531".getBytes("utf-8"));
        System.out.println("Base64 编码字符串 (基本) :" + base64encodedString);

        // 解码
        byte[] base64decodedBytes = Base64.getDecoder().decode("Y2hldGl3ZW4=");

        System.out.println("原始字符串: " + new String(base64decodedBytes, "utf-8"));

        int i=(int)(Math.random()*900000000)+100000000;
        //int i= new java.util.Random().nextInt(900)+100;也可以
        System.out.println(i);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);

    }


}