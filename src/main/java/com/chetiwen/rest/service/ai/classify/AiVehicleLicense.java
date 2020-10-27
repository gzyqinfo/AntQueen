package com.chetiwen.rest.service.ai.classify;


import com.alibaba.fastjson.JSONObject;
import com.chetiwen.baidu.AuthService;
import com.chetiwen.baidu.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;

@Path("/ai")
public class AiVehicleLicense {
    private static Logger logger = LoggerFactory.getLogger(AiVehicleLicense.class);
    // 请求url
    private String REQUEST_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/vehicle_license";

    private String ACCESS_KEY = "ZWGPzrQVVMtdseIs54IwRpkI";
    private String SECRET_KEY = "Nw4ptQHAperpqehZaWN3E5xIafn4l3Gt";

    private static String ACCESS_TOKEN = null;

    @POST
    @Path("/image-classify/license")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Vehicle License Classify request.");
        long timeStart = System.currentTimeMillis();
        String result;
        try {
            JSONObject inputJson = JSONObject.parseObject(JSONObject.toJSONString(requestObject));
            String imgStr = inputJson.getString("image");

            logger.info("image size is "+imgStr.length()/1024f+"Kb");

            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            if (ACCESS_TOKEN == null) { //TODO : cached access_token
                ACCESS_TOKEN = AuthService.getAuth(ACCESS_KEY, SECRET_KEY);
                logger.info("got token: {}", ACCESS_TOKEN);
            }

            result = HttpUtil.post(REQUEST_URL, ACCESS_TOKEN, param);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error msg: ", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        logger.info("Returned : {}", result);
        logger.info("Time used: {} ms", (System.currentTimeMillis() - timeStart));
        logger.info("###################################################################################################");
        return Response.status(Response.Status.OK).entity(result).build();

    }
}
