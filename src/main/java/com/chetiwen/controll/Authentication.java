package com.chetiwen.controll;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.UserCache;
import com.chetiwen.db.model.User;
import com.chetiwen.object.AntRequest;
import com.chetiwen.object.AntResponse;
import com.chetiwen.object.CarResponse;
import com.chetiwen.util.AntPack;
import com.chetiwen.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class Authentication {
    private static Logger logger = LoggerFactory.getLogger(Authentication.class);

    public static boolean authenticateTime(int timestamp) {
        int currentTime = (int) (System.currentTimeMillis()/1000);
        if (currentTime - timestamp < 600) {
            return true;
        }
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

    public static boolean authenticateMD5Sign(Object clientRequest) {
        try {
            AntRequest antRequest = JSONObject.parseObject(JSONObject.toJSONString(clientRequest), AntRequest.class);

            User user = UserCache.getInstance().getByKey(antRequest.getPartnerId());

            if (user != null) {
                // for example clientRequest is {"sign":"35ccdaaf743be5fea0b06cf8668ed8ae","vin":"LBVKY9103KSR90425","partnerId":"12345678","ts":1591408116}
                // To remove sign field .
                int position = clientRequest.toString().indexOf("\"sign");
                String prefix = clientRequest.toString().substring(0, position);
                String suffix = clientRequest.toString().substring(position+42); // MD5 must be 32

                String serverSign = EncryptUtil.getAntSign(prefix+suffix, user.getPartnerKey());
                if (serverSign.toLowerCase().equals(antRequest.getSign())) {
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

    public static CarResponse genCarResponse(String code, String msg, Logger logger) {
        CarResponse response = new CarResponse();
        response.setCode(code);
        response.setMsg(msg);
        logger.info("return {}, {}", code, msg);
        return response;
    }

    public static AntResponse genAntResponse(int code, String msg, Logger logger) {
        AntResponse response = new AntResponse();
        response.setCode(code);
        response.setMsg(msg);
        logger.info("return {}, {}", code, msg);
        return response;
    }

}
