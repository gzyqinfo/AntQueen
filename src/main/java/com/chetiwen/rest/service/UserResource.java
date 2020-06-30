package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.UserRateCache;
import com.chetiwen.db.model.UserRate;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.UserCache;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
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
}
