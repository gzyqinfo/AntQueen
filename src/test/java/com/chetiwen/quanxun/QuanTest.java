//package com.chetiwen.quanxun;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chetiwen.server.qucent.*;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//public class QuanTest {
//    public static String CUSTOMER_ID = "0f092952d9a2f7a0c0faea927e178396";
//    // 地址
//    public static String URL = "https://entapi.qucent.cn/api/v3";
//    // 公钥
//    public static String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
//            "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
//            "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
//            "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
//            "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
//            "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
//            "4wIDAQAB";
//    // 私钥
//    public static String pik = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDXOzLxWRGN7Hsa" +
//            "GGueGj4BwYYkoJG9bWwpaKBtyeKJJxbEmlOjEMJ9ltvBuXb0SvYZb9sCaEHwSRO6" +
//            "5Af7dbbuRXjIuhPd/Ko/DSxNhP8CQDZp+btGNWCJ8MccL/AA0rvevXDZlKmop9i+" +
//            "PDf+qyBy4saw5pWZzwtAetwVoN+tnatfT5JRC/bjgYOwUWZpzaBka2+gjy/b6TDb" +
//            "81CxKu+r1D2yM3KF6fWcythqWKOWLqYQirsjscicraD2a8tdRaEWQhD1thjxf4e+" +
//            "B7wfzjsqHANI02tt3tLOnfGYbrrOCwP4rbEIm5bQ74XZDSKWXSmfZkB8xlkxn2Hd" +
//            "LT6eZqpDAgMBAAECggEBAJDYlUMZZxf1Qy9fqeU/0eUKoUU7DnnGDxmbAQSB7kPR" +
//            "G6B6H7lJCSrOANzR/P0RCag6v9BR67ZS58VJuxl+sfqOpGep2r71UHmYWu1ciOWx" +
//            "4yzU1TS9rVeHw+fzVvim2apgIXc8diU7uEDmc+Ses/q9JWxd8eYOEYt2Y3Dm0EGc" +
//            "82niEve7B9D3vfmj0cjOEYKIxvbNPDq8Xi6ZQjNh5JGxlfPyUTF1UxUXZyBn9eoo" +
//            "8IaC+tBPxYl9phtIYmNhfst4Yqm7QEb7tAMKQlzJudjrnZdFWYandHgGWMSUiVCW" +
//            "le5pzFC+Fk6is9xqYNJuwk1XJUyNKfvziWZrPyqWGxECgYEA8ZF1WGlcBPQxN4bs" +
//            "Rt0Ib3DFevPIT8OiuHD0yX6CzZUxIDWfviCdek5AXS7ZMbpb3rpDvdtLtL1ZqeoG" +
//            "6yujlwVWllZ4QD82xHvurHAFERwRqunxQ6cx7R4rORbYXHv6XfZyhEvI8y/1Vck5" +
//            "fH/n6/eJAGxuuo/I2aAK1vdToqkCgYEA5BbxjIHxdJKpZPGoP3Ph1XFnObhJIs9a" +
//            "HNrV2MMtog8UO2YouWf4t1siLp3JsSAMXYEI7XbdZG9Zih5yzt0PCa7mrBd3mXlr" +
//            "WE8ghfePx2sI4qXacsqNgzLWcjXbRDZDS0aCUew8bbnBhGXnUXqmIduzPBtnEs+d" +
//            "HpgtaBDSZQsCgYAQ86svbB1X/6bghahZBLPN1jUVfrwE1O67ULns1eLp+Fk9MGYo" +
//            "WnOSnKEpqNr3AWPnCl0smpICefMr2E9p+2L8exRrcl/36je2rBfApA/G9phKzSXw" +
//            "IHCBekeANxkxzEVyiJPastLENg5aWced8//bcEB99h4DG4n1s6RvF2YYGQKBgQCZ" +
//            "RCmP5wle8eZN5GzQJohMKuXYTVMnxvmghhRIke6qBUPtHhqja5AfdWekt3Z+RTDJ" +
//            "7BkZqFPgV0ptm0Q+aSDfut1aKnK9eG9/abxLCS1eLThNRHFjzWQGEzUyjaoHTgcu" +
//            "H/UWI43/lWDKHMexYp8cBUuNSkSayVOk6VEpqpQWPQKBgQCgPSgjV6SvxKnkSgoP" +
//            "dFfEt5ovIn9qyFLrgSsH0gVUbqRwpIAjL8aGrF9tCN4MHBFAxi6eKqBXo1NZi1nW" +
//            "rm05RCOknVnkP7rMvUCwPKMynPwfttM2G7r3gQP6MRXYMaPlflFtLK9E419gQYs5" +
//            "RDrHXfQBPozbYKC0IAWTLkPPIw==";
//
//
//    @Test
//    public void demo() throws Exception {
//        // RSA加解�?
//        RSAUtil rsaUtil = new RSAUtil();
//
//        JSONObject encrypt = new JSONObject();
//        // 产品传入参数
//        encrypt.put("vin", "LBVKY9107LSX62249");
//
//        String encryptStr = JSONObject.toJSONString(encrypt);
//        // 数据转为json字符串并加密
//        String str = rsaUtil.encryptByPrivateKey(pik, encryptStr);
//
//        // 生成订单编号
//        String orderId = RandomUtil.random(23);
//        // 生成时间戳
//        String reqTime = System.currentTimeMillis() + "";
//
//        // 生成签名
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("customerId", CUSTOMER_ID);
//        paramMap.put("encrypt", str);
//        paramMap.put("userOrderId", orderId);
//        paramMap.put("reqTime", reqTime);
//        paramMap.put("encryptType", "true");
//        paramMap.put("productCode", "BA610030");// 产品编号
//        paramMap.put("version", "V001");// 版本号
//
//        String signStr = ParamUtil.sortParam(paramMap);
//        System.out.println(signStr);
//
//        // 添加数据
//        NameValuePair json1 = new BasicNameValuePair("customerId", CUSTOMER_ID);// 客户ID
//        NameValuePair json2 = new BasicNameValuePair("encrypt", str);// 加密后数据
//        NameValuePair json3 = new BasicNameValuePair("userOrderId", orderId);// 订单号
//        NameValuePair json4 = new BasicNameValuePair("reqTime", reqTime);// 时间戳
//        NameValuePair json5 = new BasicNameValuePair("sign", MD5Util.encrypt(signStr));// 签名
//        NameValuePair json6 = new BasicNameValuePair("encryptType", "true");// 是否加密
//        NameValuePair json7 = new BasicNameValuePair("productCode", "BA610030");// 产品编号
//        NameValuePair json8 = new BasicNameValuePair("version", "V001");// 版本号
//
//        List<NameValuePair> list = new ArrayList<NameValuePair>();
//        list.add(json1);
//        list.add(json2);
//        list.add(json3);
//        list.add(json4);
//        list.add(json5);
//        list.add(json6);
//        list.add(json7);
//        list.add(json8);
//
//        String response = HttpUtil.doPost(URL, list);
//        System.out.println(response);
//        JSONObject result = JSONObject.parseObject(response);
//        if (String.valueOf(result.get("encryptType")).equals("true")) {
//            System.out.println("解密数据:"
//                    + convertUnicode(rsaUtil.decryptByPublicKey(puk, String.valueOf(result.get("encrypt")))));
//        } else {
//            System.out.println("未加密数据：" + result.get("encrypt"));
//        }
//    }
//
//    public static String convertUnicode(String ori){
//        char aChar;
//        int len = ori.length();
//        StringBuffer outBuffer = new StringBuffer(len);
//        for (int x = 0; x < len;) {
//            aChar = ori.charAt(x++);
//            if (aChar == '\\') {
//                aChar = ori.charAt(x++);
//                if (aChar == 'u') {
//                    // Read the xxxx
//                    int value = 0;
//                    for (int i = 0; i < 4; i++) {
//                        aChar = ori.charAt(x++);
//                        switch (aChar) {
//                        case '0': case '1': case '2': case '3': case '4':case '5': case '6': case '7': case '8': case '9':
//                            value = (value << 4) + aChar - '0';
//                            break;
//                        case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
//                            value = (value << 4) + 10 + aChar - 'a';
//                            break;
//                            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
//                            value = (value << 4) + 10 + aChar - 'A';
//                            break;
//                            default:
//                            throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
//                        }
//                    }
//                    outBuffer.append((char) value);
//                } else {
//                    if (aChar == 't')
//                        aChar = '\t';
//                    else if (aChar == 'r')
//                        aChar = '\r';
//                    else if (aChar == 'n')
//                        aChar = '\n';
//                    else if (aChar == 'f')
//                        aChar = '\f';
//                    outBuffer.append(aChar);
//                }
//            } else
//                outBuffer.append(aChar);
//        }
//        return outBuffer.toString();
//    }
//}
