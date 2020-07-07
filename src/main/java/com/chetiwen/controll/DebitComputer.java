package com.chetiwen.controll;

import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.Brand;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.User;
import com.chetiwen.db.model.VinBrand;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class DebitComputer {
    private static Logger logger = LoggerFactory.getLogger(DebitComputer.class);

    public static final float DEFAULT_FEE = Float.valueOf(PropertyUtil.readValue("brand.default.fee"));

    public static float getDebitFee(String partnerId, String vin) throws DBAccessException {
        float debitFee = 0f;

        VinBrand vinBrand = VinBrandCache.getInstance().getByKey(vin);
        if (vinBrand != null) {
            Brand brand = BrandCache.getInstance().getById(vinBrand.getBrandId());
            String brandId = vinBrand.getBrandId();
            if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId + "/" + brandId)) {
                debitFee = UserRateCache.getInstance().getByKey(partnerId + "/" + brandId).getPrice();
            } else if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId + "/" + "0")
                    && (brand == null || "N".equalsIgnoreCase(brand.getIsSpecial()))) {  //非特殊品牌的普通品牌
                debitFee = UserRateCache.getInstance().getByKey(partnerId + "/" + "0").getPrice();
            } else if (brand != null) {
                debitFee = BrandCache.getInstance().getById(brandId).getPrice();
            }
        } else { //Qucent currently
            if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId+"/"+"0")) {
                debitFee = UserRateCache.getInstance().getByKey(partnerId+"/"+"0").getPrice();
            }
        }

        //当找不到计费规则或出现0计费时
        if (debitFee - 0 < 0.00000001) {
            debitFee = DEFAULT_FEE;
        }
        logger.info("Debit fee is {} ", debitFee);
        return debitFee;
    }

    public static void debit(AntRequest request, String orderId, float debitFee, String feeType) throws DBAccessException {
        User updatedUser = UserCache.getInstance().getByKey(request.getPartnerId());
        DebitLog debitLog = new DebitLog();
        debitLog.setFeeType(feeType);
        debitLog.setPartnerId(request.getPartnerId());
        debitLog.setBalanceBeforeDebit(updatedUser.getBalance());
        debitLog.setDebitFee(debitFee);
        debitLog.setOrderNo(orderId);
        debitLog.setVin(request.getVin());
        debitLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        VinBrand vinBrand = VinBrandCache.getInstance().getByKey(request.getVin());
        if (vinBrand!=null) {
            debitLog.setBrandId(vinBrand.getBrandId());
            debitLog.setBrandName(vinBrand.getBrandName());
        } else {
            debitLog.setBrandId("0");
            debitLog.setBrandName("普通品牌");
        }
        DebitLogCache.getInstance().addDebitLog(debitLog);

        if (ConstData.FEE_TYPE_TRUE.equals(feeType)) {

            updatedUser.setBalance(updatedUser.getBalance() - debitFee);
            UserCache.getInstance().updateUser(updatedUser);
        }
    }
}
