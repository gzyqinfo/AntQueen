package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.VinBrand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VinBrandAccessor {

    private Logger logger = LoggerFactory.getLogger(VinBrandAccessor.class);

    private static VinBrandAccessor instance;

    private VinBrandAccessor() {
    }

    public static synchronized VinBrandAccessor getInstance() {
        if (instance == null) {
            instance = new VinBrandAccessor();
        }
        return instance;
    }

    public List<VinBrand> getAllVinBrands() throws DBAccessException{
        logger.info("Get all VinBrands request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from vin_brand");
            List<VinBrand> list = new ArrayList<>();
            while(rs.next()){
                VinBrand vinBrand = new VinBrand();
                vinBrand.setVin(rs.getString("vin"));
                vinBrand.setBrandId(rs.getString("brand_id"));
                vinBrand.setBrandName(rs.getString("brand_name"));
                list.add(vinBrand);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addVinBrand(VinBrand vinBrand) throws DBAccessException {
        logger.info("Received add vinBrand data request. message type: {}", vinBrand.toString());

        String sql = "insert into vin_brand(vin, brand_id, brand_name) " +
                "values (?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, vinBrand.getVin());
            preparedStatement.setString(2, vinBrand.getBrandId());
            preparedStatement.setString(3, vinBrand.getBrandName());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted VinBrand record");
    }

}
