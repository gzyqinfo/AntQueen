package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.VinBrandAccessor;
import com.chetiwen.db.model.VinBrand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VinBrandCache {
    private Logger logger = LoggerFactory.getLogger(VinBrandCache.class);

    private static VinBrandCache instance;

    private Map<String, VinBrand> idMap;

    private VinBrandCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized VinBrandCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new VinBrandCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<VinBrand> list = VinBrandAccessor.getInstance().getAllVinBrands();

        list.forEach(vinBrand->{
            idMap.put(vinBrand.getVin(), vinBrand);
        });

        logger.info("VinBrandCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public VinBrand getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, VinBrand> getVinBrand() {
        return idMap;
    }

    public void refreshCache() throws DBAccessException {
        if (idMap.size() == 0) {
            reload();
        }
    }


    public void addVinBrand(VinBrand vinBrand) throws DBAccessException {
        logger.info("add into VinBrand cache: {}", vinBrand);

        VinBrandAccessor.getInstance().addVinBrand(vinBrand);
        idMap.put(vinBrand.getVin(), vinBrand);
    }

}
