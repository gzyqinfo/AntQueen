package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.Brand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
                brand.setPrice(rs.getFloat("common_price"));
                brand.setBrandName(rs.getString("brand_name"));
                brand.setIsSpecial(rs.getString("is_special"));
                brand.setPingyinName(rs.getString("pingyin_name"));
                brand.setIsEngine(rs.getString("is_engine"));
                list.add(brand);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addBrand(Brand brand) throws DBAccessException {
        logger.info("Received add brand data request. message type: {}", brand.toString());

        String sql = "insert into brand(brand_id, brand_name, common_price, is_special,pingyin_name, is_engine) " +
                "values (?,?,?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, brand.getBrandId());
            preparedStatement.setString(2, brand.getBrandName());
            preparedStatement.setFloat(3, brand.getPrice());
            preparedStatement.setString(4, brand.getIsSpecial());
            preparedStatement.setString(5, brand.getPingyinName());
            preparedStatement.setString(6, brand.getIsEngine());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted Brand record");
    }

}
