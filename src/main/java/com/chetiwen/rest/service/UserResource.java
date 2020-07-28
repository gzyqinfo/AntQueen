package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.DebitLogCache;
import com.chetiwen.cache.UserRateCache;
import com.chetiwen.common.ConstData;
import com.chetiwen.db.accesser.DebitLogAuditAccessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.accesser.UserAuditAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.user.BillDetail;
import com.chetiwen.object.user.Stat;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.UserCache;
import com.chetiwen.db.DBAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Path("/user")
public class UserResource {
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GET
    @Path("/rate/{partnerId}/{brand_id}/{brand_name}/{price}")
    @Produces("application/json;charset=UTF-8")
    public Response userRate(@PathParam("partnerId") String partnerId,
                             @PathParam("brand_id") String brandId,
                             @PathParam("brand_name") String brandName,
                             @PathParam("price") float price) {
        logger.info("Received User request with partnerId {}, brandId:{}, brandName:{}, price:{} ", partnerId, brandId, brandName, price);

        UserRate userRate = new UserRate();
        userRate.setPrice(price);
        userRate.setBrandName(brandName);
        userRate.setBrandId(brandId);
        userRate.setPartnerId(partnerId);

        try {
            if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId+"/"+brandId)) {
                UserRateCache.getInstance().updateUserRate(userRate);
            } else {
                UserRateCache.getInstance().addUserRate(userRate);
            }

            return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(userRate)).build();
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/list/{partnerId}")
    @Produces("application/json;charset=UTF-8")
    public Response listUser(@PathParam("partnerId") String partnerId) {
        logger.info("Received User request with partnerId {} ", partnerId);
        User user;
        try {
            user = UserCache.getInstance().getByKey(partnerId)==null?UserCache.getInstance().getByName(partnerId):UserCache.getInstance().getByKey(partnerId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No such user!").build();
            }

            return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(user)).build();
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/charge/{partner_id}/{charge}")
    @Produces("application/json;charset=UTF-8")
    public Response userCharge(@PathParam("partner_id") String partnerId,
                               @PathParam("charge") float chargeMoney) {
        logger.info("Received charge user data request. partner_id: {}, chargeMoney {}",partnerId,  chargeMoney);

        User user;
        try {
            user = UserCache.getInstance().getByKey(partnerId)==null?UserCache.getInstance().getByName(partnerId):UserCache.getInstance().getByKey(partnerId);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No such user。").build();
            }

            user.setBalance(user.getBalance() + chargeMoney);

            UserCache.getInstance().updateUser(user);
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase . error: {}" , e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).entity("User: "+user.getUserName()+" balance now is ： "+user.getBalance()).build();
    }

    @GET
    @Path("/source/{partner_id}/{new_source}")
    @Produces("application/json;charset=UTF-8")
    public Response changeSource(@PathParam("partner_id") String partnerId,
                                 @PathParam("new_source") String newSource) {
        logger.info("Received change user data source request.  partner_id: {}, new_source {}",partnerId,  newSource);

        User user;
        try {
            user = UserCache.getInstance().getByKey(partnerId)==null?UserCache.getInstance().getByName(partnerId):UserCache.getInstance().getByKey(partnerId);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No such user!").build();
            }

            user.setDataSource(newSource);

            UserCache.getInstance().updateUser(user);
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase . error: {}" , e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).entity(user.toString()).build();
    }

    @GET
    @Path("/create/{user_name}/{user_id}/{user_key}/{balance}/{data_source}")
    @Produces("application/json;charset=UTF-8")
    public Response userCharge(@PathParam("user_name") String userName,
                               @PathParam("user_id") String userId,
                               @PathParam("user_key") String userKey,
                               @PathParam("balance") float balance,
                               @PathParam("data_source") String dataSource) {
        logger.info("Received create user request. user_name:{}, user_id:{}, user_key:{}, balance:{},data_source:{}",userName,  userId, userKey, balance, dataSource);

        User user = new User();
        user.setUserName(userName);
        user.setPartnerId(userId);
        user.setPartnerKey(userKey);
        user.setBalance(balance);
        user.setIsValid(1);
        user.setDataSource(dataSource);
        try {
            UserCache.getInstance().addUser(user);
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase . error: {}" , e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).entity(user.toString()).build();
    }

    @POST
    @Path("/post/new")
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    public Response postUser(User user) throws Exception {
        logger.info("Received post user data request. user: {}", user);

        UserCache.getInstance().addUser(user);
        return Response.status(Response.Status.OK).entity(user.toString()).build();
    }

    public static void userOperation(String urlPrefix) throws Exception {
        Client restClient;
        WebResource webResource;

        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);

        //get input from keyboard
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        boolean flag = true;

        while(flag) {
            System.out.println();
            System.out.println("***********************************");
            System.out.println("*   1. Create New User            *");
            System.out.println("*   2. User Charge                *");
            System.out.println("*   3. Show User Info             *");
            System.out.println("***********************************");
            System.out.println();
            System.out.print("- Please choose your option(0 to quit): ");
            String str = input.readLine();
            if ("0".equals(str)) {
                flag = false;
            } else if ("1".equals(str)) {
                String url = urlPrefix + "/post/new";
                System.out.println();

                User user = new User();
                System.out.print("Input User Name: ");
                String userName = input.readLine();
                user.setUserName(userName);
                System.out.print("Input partner ID: ");
                String partnerId = input.readLine();
                user.setPartnerId(partnerId);
                System.out.print("Input partner Key: ");
                String partnerKey = input.readLine();
                user.setPartnerKey(partnerKey);
                System.out.print("Input User Balance: ");
                float balance = Float.valueOf(input.readLine());
                user.setBalance(balance);
                System.out.print("Input Data Source: ");
                String dataSource = input.readLine();
                user.setDataSource(dataSource);

                System.out.println("The user to create is : ");
                System.out.println("Name: " + userName);
                System.out.println("Partner ID: " + partnerId);
                System.out.println("Partner Key: " + partnerKey);
                System.out.println("Balance: " + balance);
                System.out.println("DataSource: " + dataSource);
                System.out.println();
                System.out.print("Is it correct ? (Y or N):");
                String isCorrect = input.readLine();
                if ("Y".equalsIgnoreCase(isCorrect)) {
                    webResource = restClient.resource(url);
                    ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, user);
                    System.out.println(response.getEntity(String.class));
                } else {
                    System.out.println("Please choose again.");
                }

            } else if ("2".equals(str)) {
                System.out.println();
                System.out.print("Input User Name: ");
                String userName = input.readLine();
                System.out.print("Input Charge Money: ");
                float charge = Float.valueOf(input.readLine());

                System.out.println("The user to charge is : ");
                System.out.println("Name: " + userName);
                System.out.println("charge: " + charge);
                System.out.println();
                System.out.print("Is it correct ? (Y or N):");
                String isCorrect = input.readLine();
                if ("Y".equalsIgnoreCase(isCorrect)) {
                    String url = urlPrefix + "/charge/"+userName+"/"+charge;
                    webResource = restClient.resource(url);
                    ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                    System.out.println(response.getEntity(String.class));
                } else {
                    System.out.println("Please choose again.");
                }
            } else if ("3".equals(str)) {
                System.out.println();
                System.out.print("Input User Name: ");
                String userName = input.readLine();
                String url = urlPrefix + "/list/" + userName;
                webResource = restClient.resource(url);
                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                System.out.println(response.getEntity(String.class));
            }
        }
        System.out.println("谢谢使用！");
    }

    @GET
    @Path("/stat/{partner_id}")
    @Produces("application/json;charset=UTF-8")
    public Response userStatistic(@PathParam("partner_id") String partnerId) throws DBAccessException {
        logger.info("Received get user statistic data request.  partner_id: {}",partnerId);
        Stat userStat = new Stat();

        List<UserAudit> userAuditList = UserAuditAccessor.getInstance().getUserAudit(partnerId);
        String userName = UserCache.getInstance().getByKey(partnerId).getUserName();
        float totalChargeAmount = 0;
        int chargeCount = 0;
        float maxChargeAmount = 0;
        for (UserAudit userAudit : userAuditList) {
            if (ConstData.SQL_ACTION_INSERT.equalsIgnoreCase(userAudit.getAction())) {
                chargeCount++;
                float chargeAmount = Float.valueOf(userAudit.getBalance());
                if (maxChargeAmount < chargeAmount) {
                    maxChargeAmount = chargeAmount;
                }
                totalChargeAmount += chargeAmount;
            } else if (ConstData.SQL_ACTION_UPDATE.equalsIgnoreCase(userAudit.getAction())) {
                float before = Float.valueOf(userAudit.getBalance().split("->")[0]);
                float after = Float.valueOf(userAudit.getBalance().split("->")[1]);
                if (after - before > 0.000001) {
                    chargeCount++;
                    float chargeAmount = (float)Math.round((after-before)*100)/100;
                    if (maxChargeAmount < chargeAmount) {
                        maxChargeAmount = chargeAmount;
                    }
                    totalChargeAmount += chargeAmount;
                }
            }
        }
        userStat.setTotalChargeAmount(totalChargeAmount);
        userStat.setTotalChargeCount(chargeCount);
        userStat.setAvgChargeAmount(Math.round((totalChargeAmount/chargeCount)*100F)/100F);
        userStat.setMaxChargeAmount(maxChargeAmount);

        List<DebitLog> dataList = DebitLogCache.getInstance().getDebitLogMap().values()
                .stream().filter(debitLog->debitLog.getPartnerId().equals(partnerId))
                .collect(Collectors.toList());
        userStat.setQuerySuccessTimes(dataList.size());
        List<DebitLogAudit> auditList = DebitLogAuditAccessor.getInstance().getDebitLogAudit(partnerId);
        for (DebitLogAudit audit: auditList) {
            if (ConstData.SQL_ACTION_DELETE.equalsIgnoreCase(audit.getAction())) {
                DebitLog debitLog = new DebitLog();
                debitLog.setBrandName(audit.getBrandName());
                debitLog.setFeeType(ConstData.FEE_TYPE_BACK);
                debitLog.setVin(audit.getVin());
                debitLog.setTimeUsedSec(audit.getTimeUsedSec()==null?0:Integer.valueOf(audit.getTimeUsedSec()));
                dataList.add(debitLog);
            }
        }

        userStat.setTotalPlaceOrder(TransLogAccessor.getInstance().getTotalPlaceOrderTimes(partnerId));
        userStat.setQueryFailTimes(dataList.size()-userStat.getQuerySuccessTimes());
        userStat.setSuccessPlaceOrder(userStat.getQuerySuccessTimes()+userStat.getQueryFailTimes());
        userStat.setFailPlaceOrder(userStat.getTotalPlaceOrder()-userStat.getSuccessPlaceOrder());

        if (userStat.getTotalPlaceOrder() == 0) {
            userStat.setTotalSuccessRatio("N/A");
            userStat.setSuccessPlaceRatio("N/A");
        } else {
            float successPlaceRatio = userStat.getSuccessPlaceOrder()*1F/userStat.getTotalPlaceOrder()*1F;
            userStat.setSuccessPlaceRatio(String.valueOf((float)Math.round((successPlaceRatio)*10000)/100));
            float totalSuccessRatio = userStat.getQuerySuccessTimes()*1F/userStat.getTotalPlaceOrder()*1F;
            userStat.setTotalSuccessRatio(String.valueOf((float)Math.round((totalSuccessRatio)*10000)/100));
        }

        if (userStat.getSuccessPlaceOrder() == 0) {
            userStat.setQuerySuccessRatio("N/A");
        } else {
            float querySuccessRatio = userStat.getQuerySuccessTimes()*1F/userStat.getSuccessPlaceOrder()*1F;
            userStat.setQuerySuccessRatio(String.valueOf((float)Math.round((querySuccessRatio)*10000)/100));
        }

        int meaningfulTimeCount = 0;
        int meaningfulTimeUsed = 0;
        int maxQueryTime = Integer.MIN_VALUE;
        int minQueryTime = Integer.MAX_VALUE;
        String maxQueryBrand = null;
        String minQueryBrand = null;
        String maxQueryVin = null;
        String minQueryVin = null;
        int cancelCount = 0;
        int cancelTimeUsed = 0;
        int maxCancelTime = Integer.MIN_VALUE;
        int minCancelTime = Integer.MAX_VALUE;
        Map<String, Integer> brandMap = new HashMap<>();
        for (DebitLog debitLog: dataList) {
            if (!debitLog.getFeeType().equals(ConstData.FEE_TYPE_BACK) && debitLog.getTimeUsedSec()>0) {
                meaningfulTimeCount ++;
                meaningfulTimeUsed += debitLog.getTimeUsedSec();
                if (debitLog.getTimeUsedSec() > maxQueryTime) {
                    maxQueryTime = debitLog.getTimeUsedSec();
                    maxQueryBrand = debitLog.getBrandName();
                    maxQueryVin = debitLog.getVin();
                }
                if (debitLog.getTimeUsedSec() < minQueryTime) {
                    minQueryTime = debitLog.getTimeUsedSec();
                    minQueryBrand = debitLog.getBrandName();
                    minQueryVin = debitLog.getVin();
                }
            } else if (debitLog.getFeeType().equals(ConstData.FEE_TYPE_BACK) && debitLog.getTimeUsedSec()>0) {
                cancelCount ++;
                cancelTimeUsed += debitLog.getTimeUsedSec();
                if (debitLog.getTimeUsedSec() > maxCancelTime) {
                    maxCancelTime = debitLog.getTimeUsedSec();
                }
                if (debitLog.getTimeUsedSec() < minCancelTime) {
                    minCancelTime = debitLog.getTimeUsedSec();
                }
            }
            if (!brandMap.containsKey(debitLog.getBrandName())) {
                brandMap.put(debitLog.getBrandName(),1);
            } else {
                brandMap.put(debitLog.getBrandName(),brandMap.get(debitLog.getBrandName()) + 1);
            }
        }

        if (meaningfulTimeCount == 0) {
            userStat.setAvgQueryTime("N/A");
            userStat.setMaxQueryTime("N/A");
            userStat.setMinQueryTime("N/A");
            userStat.setMaxQueryBrand("N/A");
            userStat.setMaxQueryVin("N/A");
            userStat.setMinQueryBrand("N/A");
            userStat.setMinQueryVin("N/A");
        } else {
            userStat.setAvgQueryTime(String.valueOf(meaningfulTimeUsed/meaningfulTimeCount));
            userStat.setMaxQueryTime(String.valueOf(maxQueryTime));
            userStat.setMinQueryTime(String.valueOf(minQueryTime));
            userStat.setMaxQueryBrand(maxQueryBrand);
            userStat.setMaxQueryVin(maxQueryVin);
            userStat.setMinQueryBrand(minQueryBrand);
            userStat.setMinQueryVin(minQueryVin);
        }

        if (cancelCount == 0) {
            userStat.setAvgCancelTime("N/A");
            userStat.setMaxCancelTime("N/A");
            userStat.setMinCancelTime("N/A");
        } else {
            userStat.setAvgCancelTime(String.valueOf(cancelTimeUsed/cancelCount));
            userStat.setMaxCancelTime(String.valueOf(maxCancelTime));
            userStat.setMinCancelTime(String.valueOf(minCancelTime));
        }

        if (brandMap.size() == 0 || (brandMap.size()==1 && brandMap.containsKey("普通品牌"))) {
            userStat.setMostFrequentQueryBrandName("N/A");
        } else {
            int maxBrand = Integer.MIN_VALUE;
            for(Map.Entry<String, Integer> entry : brandMap.entrySet()){
                String mapKey = entry.getKey();
                Integer mapValue = entry.getValue();
                if (mapKey != null && !mapKey.equals("普通品牌")){
                    if (maxBrand < mapValue) {
                        maxBrand = mapValue;
                        userStat.setMostFrequentQueryBrandName(mapKey);
                        userStat.setMostFrequentQueryBrandTimes(maxBrand);
                    }
                }
            }
        }

        List<UserRate> userRateList = UserRateCache.getInstance().getUserRateMap().values()
                .stream().filter(userRate->userRate.getPartnerId().equals(partnerId))
                .collect(Collectors.toList());
        StringBuilder userRateString = new StringBuilder("品牌费率");
        if (userRateList == null || userRateList.size() == 0) {
            userRateString.append(": \t系统设定").append("\n");
        } else {
            userRateString.append("\n\n");
            for (UserRate userRate : userRateList) {
                userRateString.append(userRate.getBrandName()).append(": \t").append(userRate.getPrice()).append("元\n");
            }
        }
        userRateString.append("------------------------\n\n");

        StringBuilder result = new StringBuilder();
        result.append("----客户行为统计分析----\n\n")
                .append("客户名： ").append(userName).append("\n")
                .append("\n")
                .append("总充值金额: ").append("\t¥").append(userStat.getTotalChargeAmount()).append("\n")
                .append("总充值次数: ").append("\t").append(userStat.getTotalChargeCount()).append("次\n")
                .append("平均每次充值: ").append("\t¥").append(userStat.getAvgChargeAmount()).append("\n")
                .append("单笔最大充值: ").append("\t¥").append(userStat.getMaxChargeAmount()).append("\n")
                .append("------------------------\n\n")
                .append(userRateString)
                .append("总下单次数: ").append("\t").append(userStat.getTotalPlaceOrder()).append("次").append("\n")
                .append("成功下单数: ").append("\t").append(userStat.getSuccessPlaceOrder()).append("次").append("\n")
                .append("未成功下单数: ").append("\t").append(userStat.getFailPlaceOrder()).append("次").append("\n")
                .append("下单成功率: ").append("\t").append(userStat.getSuccessPlaceRatio()).append("%").append("\n")
                .append("\n")
                .append("查询有数据: ").append("\t").append(userStat.getQuerySuccessTimes()).append("次").append("\n")
                .append("查询无数据: ").append("\t").append(userStat.getQueryFailTimes()).append("次").append("\n")
                .append("取数成功率: ").append("\t").append(userStat.getQuerySuccessRatio()).append("%").append("\n")
                .append("\n")
                .append("总查询成功率: ").append("\t").append(userStat.getTotalSuccessRatio()).append("%").append("\n")
                .append("\n")
                .append("最慢出单时长: ").append("\t").append(userStat.getMaxQueryTime()).append("秒").append("\n")
                .append("最慢时长单的品牌: ").append("\t").append(userStat.getMaxQueryBrand()).append("\n")
                .append("最慢时长单的VIN: ").append("\t").append(userStat.getMaxQueryVin()).append("\n")
                .append("\n")
                .append("最快出单时长: ").append("\t").append(userStat.getMinQueryTime()).append("秒").append("\n")
                .append("最快时长单的品牌: ").append("\t").append(userStat.getMinQueryBrand()).append("\n")
                .append("最快时长单的VIN: ").append("\t").append(userStat.getMinQueryVin()).append("\n")
                .append("\n")
                .append("出单平均时长: ").append("\t").append(userStat.getAvgQueryTime()).append("秒").append("\n")
                .append("\n")
                .append("退单平均时长: ").append("\t").append(userStat.getAvgCancelTime()).append("秒").append("\n")
                .append("退单最长时间: ").append("\t").append(userStat.getMaxCancelTime()).append("秒").append("\n")
                .append("退单最短时间: ").append("\t").append(userStat.getMinCancelTime()).append("秒").append("\n")
                .append("\n")
                .append("最常查询品牌名称: ").append("\t").append(userStat.getMostFrequentQueryBrandName()).append("\n")
                .append("最常查询品牌次数: ").append("\t").append(userStat.getMostFrequentQueryBrandTimes()).append("次").append("\n")
        ;

        return Response.status(Response.Status.OK).entity(result.toString()).build();
    }
}
