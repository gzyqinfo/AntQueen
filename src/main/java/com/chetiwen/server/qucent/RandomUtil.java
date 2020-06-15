package com.chetiwen.server.qucent;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by liran on 2017/8/12.
 */
public class RandomUtil {
    public static String random(int n){
        String str = String.valueOf(System.currentTimeMillis());
        if(n <= str.length()) {
            return str.substring(0, n-1);
        } else {
            int count = n - str.length();
            int min = (int) Math.pow(10, count-1);
            int max = (int) Math.pow(10, count);
            Random random = new Random();
            str = str + String.valueOf(random.nextInt(max) % ( max - min + 1) + min);
            return str;
        }
    }
    //传入参数18，则生成18位随机数，可做身份证号码
    public static String RandomNo(int n){
            String s = "";
            Random random = new Random();
            s+=random.nextInt(9)+1;
            for (int i = 0; i < n-1; i++) {
                s+=random.nextInt(10);
            }
            BigInteger bigInteger=new BigInteger(s);
            System.out.println("证件号码" + bigInteger);
            return s;
    }

    public static String RandomHex(int n) {
        StringBuffer result = new StringBuffer();
        for(int i=0;i<n;i++) {
//            result.append(Integer.toHexString(new Random().nextInt(16)));
        }
        return result.toString().toUpperCase();
    }
}
