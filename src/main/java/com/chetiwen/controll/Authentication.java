package com.chetiwen.controll;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.UserCache;
import com.chetiwen.db.model.User;
import com.chetiwen.object.AntRequest;
import com.chetiwen.object.AntResponse;
import com.chetiwen.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class Authentication {
    private static Logger logger = LoggerFactory.getLogger(Authentication.class);

    public static boolean authenticateTime(int timestamp) {
        int currentTime = (int) (System.currentTimeMillis()/1000);
        if (currentTime - timestamp < 300) {   // 5 minutes valid
            return true;
        }
        logger.warn("authentication failed : Over time.");
        return false;
    }


    public static boolean authenticateSign(String sign, int timestamp, String appKey) {
        byte[] base64decodedBytes = Base64.getDecoder().decode(sign);
        try {
            String decodevalue = new String(base64decodedBytes, "utf-8");

            long signValue = Long.valueOf(decodevalue).longValue();

            int hidePassword = UserCache.getInstance().getByKey(appKey).getIsValid();

            if ((hidePassword * (long)7) == (signValue - timestamp)) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean jsonSign(Object clientRequest) {
        try {

            JSONObject jsonRequest =JSONObject.parseObject(JSONObject.toJSONString(clientRequest));

            int ts = Integer.parseInt(jsonRequest.get("ts").toString());
            String partnerId = jsonRequest.get("partnerId").toString();
            String clientSign = jsonRequest.get("sign").toString();

            if (!authenticateTime(ts)) {
                return false;
            }

            User user = UserCache.getInstance().getByKey(partnerId);

            if (user != null) {
                // for example clientRequest is {"sign":"35ccdaaf743be5fea0b06cf8668ed8ae","vin":"LBVKY9103KSR90425","partnerId":"12345678","ts":1591408116}
                // To remove sign field .
                int position = JSONObject.toJSONString(clientRequest).indexOf("sign");
                String prefix = JSONObject.toJSONString(clientRequest).substring(0, position);
                String suffix = JSONObject.toJSONString(clientRequest).substring(position+42); // MD5 must be 32

                String serverSign = EncryptUtil.getAntSign(prefix+suffix, user.getPartnerKey());
                if (serverSign.toLowerCase().equals(clientSign)) {
                    return true;
                }
            }

        } catch (Exception e) {

            return false;
        }
        return false;
    }

    public static boolean authenticateCallBackUrl(String url) {

        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            return true;
        }
        return false;
    }


    public static AntResponse genAntResponse(int code, String msg, Logger logger) {
        AntResponse response = new AntResponse();
        response.setCode(code);
        response.setMsg(msg);
        logger.info("return {}, {}", code, msg);
        return response;
    }

}
