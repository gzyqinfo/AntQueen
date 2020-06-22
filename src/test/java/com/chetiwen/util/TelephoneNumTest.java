package com.chetiwen.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
         

public class TelephoneNumTest {
    public static void main(String[] args){
        //要提前号码的字符串
        String str="nafdsf咽；叶：adsg313977777777s18911111111你好15988888888hha025-77777877sss0775-6678111";
        //提取手机号码
        str = checkCellphone(str);
        //提取固定电话号码
        checkTelephone(str);

        System.out.println(str);
        System.out.println(checkTelephone(str));
    }
     
    /**
      * 查询符合的手机号码
      * @param str
      */
    public static String checkCellphone(String str){
        String replacement = str;
            // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
                //查找到符合的即输出
            System.out.println("查询到一个符合的手机号码："+matcher.group());
            replacement = replacement.replace(matcher.group(), "***********");
        }
        return  replacement;
    }
     
    /**
      * 查询符合的固定电话
      * @param str
      */
    public static String checkTelephone(String str){
        String replacement = str;
         // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        while(matcher.find()){
                 //查找到符合的即输出
            System.out.println("查询到一个符合的固定号码："+matcher.group());
            replacement = replacement.replace(matcher.group(), "***********");
        }
        return  replacement;
    }
}
