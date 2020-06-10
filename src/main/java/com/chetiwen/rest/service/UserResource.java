package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
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
    @Path("/list")
    @Produces("application/json;charset=UTF-8")
    public Response listAll() {
        logger.info("Received User list request ");
        try {
            List<User> dataList = UserCache.getInstance().getUserMap().values()
                    .stream().collect(Collectors.toList());

            logger.info("returned {} row(s) data", dataList.size());
            return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(dataList)).build();
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


    @PUT
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity("User: "+user.getUserName()+" balance now is ： "+user.getBalance()).build();
    }


    @POST
    @Path("/post/new")
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    public Response postUser(User user) throws Exception {
        logger.info("Received post user data request. user: {}", user);

        String partnerKey;
        long partnerId;

        do {
            partnerId = (long)(Math.random()*900000000)+100000000;
            partnerKey = Base64.getEncoder().encodeToString(String.valueOf(partnerId*1234).getBytes("utf-8"));
        } while (UserCache.getInstance().getUserMap().containsKey(String.valueOf(partnerId)));

        user.setPartnerId(String.valueOf(partnerId));
        user.setPartnerKey(partnerKey);

        UserCache.getInstance().addUser(user);

        return Response.status(Response.Status.OK).entity("User: "+user.getUserName()+" created successfully. Partner ID: "+partnerId+"; Partner Key: "+partnerKey +" .Balance："+user.getBalance()).build();
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
                System.out.print("Input User Balance: ");
                float balance = Float.valueOf(input.readLine());
                user.setBalance(balance);


                System.out.println("The user to create is : ");
                System.out.println("Name: " + userName);
                System.out.println("Balance: " + balance);
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
                    ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).put(ClientResponse.class);
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
