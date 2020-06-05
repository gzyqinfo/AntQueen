package com.chetiwen.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chetiwen.object.PackBody;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

/**
 * @version 1.0
 * @author: zwg.BlueOcean
 * @date 2018/3/1 18:02
 * @description
 */
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
     * 对对象属性进行排序，输出排序后的字符串
     * @param val
     * @return
     */
    public static String getSortBody(Object val){
        String bodyString=null;
        //排序
        if(val!=null){
            Object obj1;
            if(val instanceof JSONObject){
                obj1=val;
            }else{
                System.out.println(val.toString());
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
                String hand="{";
                for(int j=0;j<arrs.length;j++){
                    if(obj.get(arrs[j]) instanceof String){
                        hand+="\""+arrs[j]+"\":\""+obj.get(arrs[j])+"\",";
                    }else{
                        hand+="\""+arrs[j]+"\":"+obj.get(arrs[j])+",";
                    }

                }
                if(hand.length()>1){
                    hand=hand.substring(0,hand.length()-1)+"}";
                }else{
                    hand="{}";
                }

                bodyString=hand;
            }
        }
        return bodyString;
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


    /**
     * 计算sign
     * @param packBody
     * @return
     * @throws Exception
     */
    public static String getSignString(PackBody packBody) throws Exception{
        if(packBody==null){
            throw new Exception("请求对象为空！");
        }
        if(packBody.getAppSecret()==null || packBody.getAppSecret()==""){
            throw new Exception("appSecret设置不合法");
        }
        String sortData = getSortBody(packBody.getBody());
        sortData = sortData.replace("/", "\\/");
//        // 中文字符转为unicode
        sortData = chinaToUnicode(sortData);
        String bodyString=String.format("appKey=%s&appSecret=%s&body=%s&ticket=%s&timestamp=%s&version=%s",
                packBody.getAppKey(),
                packBody.getAppSecret(),
                sortData,
                packBody.getTicket(),
                packBody.getTimestamp(),
                packBody.getVersion());
        return getMD5(bodyString).toUpperCase();
    }

    /**
     * 计算sign，并赋值
     * @param packBody
     * @throws Exception
     */
    public static void setSignString(PackBody packBody) throws Exception{
        String sign=getSignString(packBody);
        packBody.setSign(sign);
    }

    /**
     *
     * @author 王欣宇
     * @throws Exception
     * @time 2018年3月21日 下午3:20:12
     * @todo 计算签名
     * @remark
     */
    public static void main(String[] args) throws Exception {
        JSONObject json = new JSONObject();
        json.fluentPut("version", 1);
        json.fluentPut("timestamp", 1521616563);
        json.fluentPut("ticket", "bc045d92-51be-43a1-8810-20c5d7d5254e");
        json.fluentPut("appKey", "pFXf1cxdZGEyiCYV");
        json.fluentPut("appSecret", "DThsKpk0EPlljHOajLMWYu3mbRit6gDp");
        JSONObject body = new JSONObject();
        body.fluentPut("vin", "JTEES42A882102414");
        json.fluentPut("body", body);
        System.out.println(EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));
    }


    public static String getBase64Sign(String appKey, int timestamp) throws Exception {
        byte[] base64decodedBytes = Base64.getDecoder().decode(appKey);
        int hidePassword = Integer.valueOf(new String(base64decodedBytes, "utf-8")).intValue();

        long signValue = (long)hidePassword * 7 + (long)timestamp;

        return Base64.getEncoder().encodeToString(String.valueOf(signValue).getBytes("utf-8"));

    }


    public static String getAntSign(AntPack packBody, String partnerKey) throws Exception{
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
//        System.out.println(bodyString);
        return bodyString;
    }

}
