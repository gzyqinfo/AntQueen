package com.chetiwen.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EncryptUtil {

    /**
     * 计算MD5
     * @param input
     * @return
     */
    public static String getMD5(String input) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        byte[] btInput = input.getBytes();
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst;
        try {
            mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }



    /**
     * 对汉字进行转码
     * @param str
     * @return
     */
    public static String chinaToUnicode(String str){
        String result="";
        for (int i = 0; i < str.length(); i++){
            int chr1 = (char) str.charAt(i);
            //汉字范围 \u4e00-\u9fa5 (中文)
            if(chr1>=19968&&chr1<=171941){
                result+="\\u" + Integer.toHexString(chr1);
            }else{
                result+=str.charAt(i);
            }
        }
        return result;
    }


    public static String sign(Object packBody, String partnerKey) throws Exception{
        if(packBody==null){
            throw new Exception("请求对象为空！");
        }
        if(packBody==null || partnerKey==""){
            throw new Exception("partnerKey设置不合法");
        }
        String sortData = getSortString(packBody);
        sortData = sortData.replace("/", "\\/");
//        // 中文字符转为unicode
        sortData = chinaToUnicode(sortData);
        String signString = sortData+"&partnerKey="+partnerKey;
        return getMD5(signString).toLowerCase();
    }

    /**
     * 对对象属性进行排序，输出排序后的字符串
     * @param val
     * @return
     */
    public static String getSortString(Object val){
        String bodyString=null;
        //排序
        if(val!=null){
            Object obj1;
            if(val instanceof JSONObject){
                obj1=val;
            }else{
//                System.out.println(val.toString());
                //消除值为null的项
                obj1= JSON.toJSON(val);
                obj1= JSON.parseObject(obj1.toString());
            }

            if(obj1 instanceof JSONObject){
                JSONObject obj=(JSONObject) obj1;
                String[] arrs=new String[obj.size()];
                int i=0;
                for (Map.Entry<String, Object> item : obj.entrySet()) {
                    arrs[i]=item.getKey();
                    i++;
                }
                Arrays.sort(arrs);
                String hand="";
                for(int j=0;j<arrs.length;j++){

                        hand+=arrs[j]+"="+obj.get(arrs[j])+"&";

                }
                if(hand.length()>1){
                    hand=hand.substring(0,hand.length()-1);
                }else{
                    hand="";
                }

                bodyString=hand;
            }
        }
        return bodyString;
    }

    public static String convertUnicode(String ori){
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0': case '1': case '2': case '3': case '4':case '5': case '6': case '7': case '8': case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 查询符合的号码
     * @param str
     */
    public static String replacePhoneNumber(String str){
        String replacement = str;
        // 查询符合的手机号码
        Pattern pattern = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的
            replacement = replacement.replace(matcher.group(), "***********");
        }
        //查询符合的固定电话
        pattern = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
            //查找到符合的即输出
            replacement = replacement.replace(matcher.group(), "***********");
        }
        return  replacement;
    }

}
