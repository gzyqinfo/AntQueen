package com.chetiwen.quanxun;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chetiwen.server.qucent.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.util.*;


public class QuanTest {
    public static String CUSTOMER_ID = "0f092952d9a2f7a0c0faea927e178396";
    // 地址
    public static String URL = "https://entapi.qucent.cn/api/v3";
    // 公钥
    public static String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
            "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
            "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
            "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
            "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
            "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
            "4wIDAQAB";
    // 私钥
    public static String pik = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDXOzLxWRGN7Hsa" +
            "GGueGj4BwYYkoJG9bWwpaKBtyeKJJxbEmlOjEMJ9ltvBuXb0SvYZb9sCaEHwSRO6" +
            "5Af7dbbuRXjIuhPd/Ko/DSxNhP8CQDZp+btGNWCJ8MccL/AA0rvevXDZlKmop9i+" +
            "PDf+qyBy4saw5pWZzwtAetwVoN+tnatfT5JRC/bjgYOwUWZpzaBka2+gjy/b6TDb" +
            "81CxKu+r1D2yM3KF6fWcythqWKOWLqYQirsjscicraD2a8tdRaEWQhD1thjxf4e+" +
            "B7wfzjsqHANI02tt3tLOnfGYbrrOCwP4rbEIm5bQ74XZDSKWXSmfZkB8xlkxn2Hd" +
            "LT6eZqpDAgMBAAECggEBAJDYlUMZZxf1Qy9fqeU/0eUKoUU7DnnGDxmbAQSB7kPR" +
            "G6B6H7lJCSrOANzR/P0RCag6v9BR67ZS58VJuxl+sfqOpGep2r71UHmYWu1ciOWx" +
            "4yzU1TS9rVeHw+fzVvim2apgIXc8diU7uEDmc+Ses/q9JWxd8eYOEYt2Y3Dm0EGc" +
            "82niEve7B9D3vfmj0cjOEYKIxvbNPDq8Xi6ZQjNh5JGxlfPyUTF1UxUXZyBn9eoo" +
            "8IaC+tBPxYl9phtIYmNhfst4Yqm7QEb7tAMKQlzJudjrnZdFWYandHgGWMSUiVCW" +
            "le5pzFC+Fk6is9xqYNJuwk1XJUyNKfvziWZrPyqWGxECgYEA8ZF1WGlcBPQxN4bs" +
            "Rt0Ib3DFevPIT8OiuHD0yX6CzZUxIDWfviCdek5AXS7ZMbpb3rpDvdtLtL1ZqeoG" +
            "6yujlwVWllZ4QD82xHvurHAFERwRqunxQ6cx7R4rORbYXHv6XfZyhEvI8y/1Vck5" +
            "fH/n6/eJAGxuuo/I2aAK1vdToqkCgYEA5BbxjIHxdJKpZPGoP3Ph1XFnObhJIs9a" +
            "HNrV2MMtog8UO2YouWf4t1siLp3JsSAMXYEI7XbdZG9Zih5yzt0PCa7mrBd3mXlr" +
            "WE8ghfePx2sI4qXacsqNgzLWcjXbRDZDS0aCUew8bbnBhGXnUXqmIduzPBtnEs+d" +
            "HpgtaBDSZQsCgYAQ86svbB1X/6bghahZBLPN1jUVfrwE1O67ULns1eLp+Fk9MGYo" +
            "WnOSnKEpqNr3AWPnCl0smpICefMr2E9p+2L8exRrcl/36je2rBfApA/G9phKzSXw" +
            "IHCBekeANxkxzEVyiJPastLENg5aWced8//bcEB99h4DG4n1s6RvF2YYGQKBgQCZ" +
            "RCmP5wle8eZN5GzQJohMKuXYTVMnxvmghhRIke6qBUPtHhqja5AfdWekt3Z+RTDJ" +
            "7BkZqFPgV0ptm0Q+aSDfut1aKnK9eG9/abxLCS1eLThNRHFjzWQGEzUyjaoHTgcu" +
            "H/UWI43/lWDKHMexYp8cBUuNSkSayVOk6VEpqpQWPQKBgQCgPSgjV6SvxKnkSgoP" +
            "dFfEt5ovIn9qyFLrgSsH0gVUbqRwpIAjL8aGrF9tCN4MHBFAxi6eKqBXo1NZi1nW" +
            "rm05RCOknVnkP7rMvUCwPKMynPwfttM2G7r3gQP6MRXYMaPlflFtLK9E419gQYs5" +
            "RDrHXfQBPozbYKC0IAWTLkPPIw==";


    @Test
    public void demo() throws Exception {
        // RSA加解�?
        RSAUtil rsaUtil = new RSAUtil();

        JSONObject encrypt = new JSONObject();
        // 产品传入参数
        encrypt.put("vin", "LBVKY9107LSX62249");

        String encryptStr = JSONObject.toJSONString(encrypt);
        // 数据转为json字符串并加密
        String str = rsaUtil.encryptByPrivateKey(pik, encryptStr);

        // 生成订单编号
        String orderId = RandomUtil.random(23);
        // 生成时间戳
        String reqTime = System.currentTimeMillis() + "";

        // 生成签名
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("customerId", CUSTOMER_ID);
        paramMap.put("encrypt", str);
        paramMap.put("userOrderId", orderId);
        paramMap.put("reqTime", reqTime);
        paramMap.put("encryptType", "true");
        paramMap.put("productCode", "BA610030");// 产品编号
        paramMap.put("version", "V001");// 版本号

        String signStr = ParamUtil.sortParam(paramMap);
        System.out.println(signStr);

        // 添加数据
        NameValuePair json1 = new BasicNameValuePair("customerId", CUSTOMER_ID);// 客户ID
        NameValuePair json2 = new BasicNameValuePair("encrypt", str);// 加密后数据
        NameValuePair json3 = new BasicNameValuePair("userOrderId", orderId);// 订单号
        NameValuePair json4 = new BasicNameValuePair("reqTime", reqTime);// 时间戳
        NameValuePair json5 = new BasicNameValuePair("sign", MD5Util.encrypt(signStr));// 签名
        NameValuePair json6 = new BasicNameValuePair("encryptType", "true");// 是否加密
        NameValuePair json7 = new BasicNameValuePair("productCode", "BA610030");// 产品编号
        NameValuePair json8 = new BasicNameValuePair("version", "V001");// 版本号

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(json1);
        list.add(json2);
        list.add(json3);
        list.add(json4);
        list.add(json5);
        list.add(json6);
        list.add(json7);
        list.add(json8);

        String response = HttpUtil.doPost(URL, list);
        System.out.println(response);
        JSONObject result = JSONObject.parseObject(response);
        if (String.valueOf(result.get("encryptType")).equals("true")) {
            System.out.println("解密数据:"
                    + convertUnicode(rsaUtil.decryptByPublicKey(puk, String.valueOf(result.get("encrypt")))));
        } else {
            System.out.println("未加密数据：" + result.get("encrypt"));
        }
    }



    @Test
    public void str() throws Exception {
        RSAUtil rsaUtil = new RSAUtil();
        String myString = "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";

        myString ="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";
        System.out.println("解密数据:"
                + convertUnicode(rsaUtil.decryptByPublicKey(puk, myString)));

        String jsonStr = "{encrypt=blabla, encryptType=true, sign=9f049c09d40530f0ef731d110b252de3}";

        JSONObject result = null;
        try{
            result = JSONObject.parseObject(jsonStr);
            System.out.printf("result : "+result.toJSONString());
        }catch (JSONException jsone) {
            String leftReplace = jsonStr.replace("{","");
            String rightReplace = leftReplace.replace("}", "");
            System.out.println("result : "+rightReplace);

            String[] abc = rightReplace.split(",");
            for (int i=0;i<abc.length;i++) {
                if (abc[i].contains("encryptType=")) {
                    System.out.println(abc[i].split("=")[1]);
                }
            }
        }


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
}
