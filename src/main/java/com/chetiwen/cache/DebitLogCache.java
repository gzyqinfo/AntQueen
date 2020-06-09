package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.model.DebitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebitLogCache {
    private Logger logger = LoggerFactory.getLogger(DebitLogCache.class);

    private static DebitLogCache instance;

    private Map<String, DebitLog> keyMap;

    private DebitLogCache() throws DBAccessException {
        keyMap = new HashMap<>();
        reload();
    }

    public static synchronized DebitLogCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new DebitLogCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        keyMap.clear();

        List<DebitLog> list = DebitLogAccessor.getInstance().getDebitLogs();

        list.forEach(debitLog->{
            keyMap.put(debitLog.getPartnerId()+"/"+debitLog.getOrderNo(), debitLog);
        });

        logger.info("DebitLogCache reloaded. {} record(s) are refreshed.", keyMap.size());
    }

    public DebitLog getByKey(String key) {
        return keyMap.get(key);
    }


    public Map<String, DebitLog> getDebitLogMap() {
        return keyMap;
    }

    public void refreshCache() throws DBAccessException {
        if (keyMap.size() == 0) {
            reload();
        }
    }


    public void addDebitLog(DebitLog debitLog) throws DBAccessException {
        logger.info("add into debitLog cache: {}", debitLog);

        DebitLogAccessor.getInstance().addLog(debitLog);
        keyMap.put(debitLog.getPartnerId()+"/"+debitLog.getOrderNo(), debitLog);
    }


}
