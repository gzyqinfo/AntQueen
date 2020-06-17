package com.chetiwen.rest.service.antqueen;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.*;
import com.chetiwen.util.EncryptUtil;
import com.chetiwen.util.PropertyUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api")
public class CheckVinInterface {
    private static Logger logger = LoggerFactory.getLogger(CheckVinInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/checkVin")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Check Vin request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original checkVin request");

            AntRequest antRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            antRequest.setSign(null);
            antRequest.setPartnerId(PropertyUtil.readValue("app.key"));
            antRequest.setSign(EncryptUtil.sign(antRequest, PropertyUtil.readValue("app.secret")));

            logger.info("Request to source with: {}", antRequest.toString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, antRequest.toString(), "source checkVin request");

            String url = PropertyUtil.readValue("source.url") + "/api/checkVin";
            webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,antRequest);

            JSONObject sourceResponse = response.getEntity(JSONObject.class);
            logger.info("Got response: {}", sourceResponse.toJSONString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, sourceResponse.toJSONString(), "source checkVin response");

            overwriteBrandPrice(originalRequest, sourceResponse);

            logger.info("return {}", sourceResponse);
            return Response.status(Response.Status.OK).entity(sourceResponse.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private void overwriteBrandPrice(AntRequest originalRequest, JSONObject sourceResponse) throws DBAccessException {
        if ("1106".equals(sourceResponse.get("code").toString())){
            JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(sourceResponse.get("data")));
            String brandId = data.get("brandId").toString();
            Brand brand = BrandCache.getInstance().getById(brandId);
            if (brand != null) {
                if (UserRateCache.getInstance().getByKey(originalRequest.getPartnerId()+"/"+brandId)!=null) {
                    data.put("price", UserRateCache.getInstance().getByKey(originalRequest.getPartnerId()+"/"+brandId).getPrice());
                } else if (UserRateCache.getInstance().getByKey(originalRequest.getPartnerId()+"/"+"0")!=null) {
                    data.put("price", UserRateCache.getInstance().getByKey(originalRequest.getPartnerId()+"/"+"0").getPrice());
                } else {
                    data.put("price", brand.getPrice());
                }
                sourceResponse.put("data", data);
            } else {
                logger.error("Cannot find brand {} in cache",brandId);
                throw new RuntimeException();
            }

        }
    }


}
