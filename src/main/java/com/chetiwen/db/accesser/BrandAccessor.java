package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.Brand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandAccessor {

    private Logger logger = LoggerFactory.getLogger(BrandAccessor.class);

    private static BrandAccessor instance;

    private BrandAccessor() {
    }

    public static synchronized BrandAccessor getInstance() {
        if (instance == null) {
            instance = new BrandAccessor();
        }
        return instance;
    }

    public List<Brand> getAllBrands() throws DBAccessException{
        logger.info("Get all Brands request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from brand");
            List<Brand> list = new ArrayList<>();
            while(rs.next()){
                Brand brand = new Brand();
                brand.setBrandId(rs.getString("brand_id"));
                brand.setPrice(rs.getFloat("price"));
                brand.setBrandName(rs.getString("brand_name"));
                brand.setIsSpecial(rs.getString("is_special"));

                list.add(brand);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

}
