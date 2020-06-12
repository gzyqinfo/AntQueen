package com.chetiwen.controll;

import com.chetiwen.cache.SaveOrderCache;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class RegularHouseKeep extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(RegularHouseKeep.class);

    private static int SAVE_ORDER_KEEP_DAYS = Integer.valueOf(PropertyUtil.readValue("saveOrder.keep.days"));

    @Override
    public void run() {
        logger.info("Start to housekeep save_order, keeping days : {}", SAVE_ORDER_KEEP_DAYS);
        try {
            SaveOrderCache.getInstance().houseKeepSaveOrder(SAVE_ORDER_KEEP_DAYS);
        } catch (DBAccessException e) {
            e.printStackTrace();
            logger.error("Error while housekeeping save_order. error: {}", e.getMessage());
        }
        logger.info("save_order housekeeping finished");
    }
}
