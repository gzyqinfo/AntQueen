package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.UserRateAccessor;
import com.chetiwen.db.model.UserRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRateCache {
    private Logger logger = LoggerFactory.getLogger(UserRateCache.class);

    private static UserRateCache instance;

    private Map<String, UserRate> keyMap;

    private UserRateCache() throws DBAccessException {
        keyMap = new HashMap<>();
        reload();
    }

    public static synchronized UserRateCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new UserRateCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        keyMap.clear();

        List<UserRate> list = UserRateAccessor.getInstance().getAllUserRates();

        list.forEach(userRate->{
            keyMap.put(userRate.getPartnerId()+"/"+userRate.getBrandId(), userRate);
        });

        logger.info("UserRateCache reloaded. {} record(s) are refreshed.", keyMap.size());
    }

    public UserRate getByKey(String key) {
        return keyMap.get(key);
    }


    public Map<String, UserRate> getUserRateMap() {
        return keyMap;
    }

    public void refreshCache() throws DBAccessException {
        if (keyMap.size() == 0) {
            reload();
        }
    }


    public void addUserRate(UserRate userRate) throws DBAccessException {
        logger.info("add into userRate cache: {}", userRate);

        UserRateAccessor.getInstance().addUserRate(userRate);
        keyMap.put(userRate.getPartnerId()+"/"+userRate.getBrandId(), userRate);
    }

    public void updateUserRate(UserRate userRate) throws DBAccessException {
        logger.info("update userRate cache for user: {}/{}", userRate.getPartnerId(), userRate);

        UserRateAccessor.getInstance().updateUserRate(userRate);
        keyMap.put(userRate.getPartnerId()+"/"+userRate.getBrandId(), userRate);
    }

}
