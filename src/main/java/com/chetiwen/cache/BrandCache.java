package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.BrandAccessor;
import com.chetiwen.db.model.Brand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrandCache {
    private Logger logger = LoggerFactory.getLogger(BrandCache.class);

    private static BrandCache instance;

    private Map<String, Brand> idMap;
    private Map<String, Brand> nameMap;

    private BrandCache() throws DBAccessException {
        idMap = new HashMap<>();
        nameMap = new HashMap<>();
        reload();
    }

    public static synchronized BrandCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new BrandCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();
        nameMap.clear();

        List<Brand> list = BrandAccessor.getInstance().getAllBrands();

        list.forEach(brand->{
            idMap.put(brand.getBrandId(), brand);
            nameMap.put(brand.getBrandName(), brand);
        });

        logger.info("BrandCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public Brand getById(String key) {
        return idMap.get(key);
    }
    public Brand getByName(String key) {
        return nameMap.get(key);
    }

    public Map<String, Brand> getBrandMap() {
        return idMap;
    }

}
