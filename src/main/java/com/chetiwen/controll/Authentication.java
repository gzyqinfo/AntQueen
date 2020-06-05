package com.chetiwen.controll;

import com.chetiwen.cache.UserCache;
import com.chetiwen.object.CarResponse;
import org.slf4j.Logger;

import java.util.Base64;

public class Authentication {

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

            int hidePassword = UserCache.getInstance().getByKey(appKey).getHidePassword();

            if ((hidePassword * (long)7) == (signValue - timestamp)) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean authenticateCallBackUrl(String url) {

        if (url != null && url.startsWith("http://")) {
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

}
