package com.chetiwen.server;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.util.EncryptUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;


import static org.junit.Assert.assertEquals;

public class AntQueenTest {

    private static Client restClient;
    private static WebResource webResource;

    private static String urlPrefix = "https://my.51ruiheng.com";
    private final String partnerId = "68920907";
    private final String partnerKey = "A3883A3D6336F291292A0A4FBD3F74E5";

    private String myUrlPrefix = "http://localhost:8090";
    private final String myPartnerId = "12345678";
    private final String myPartnerKey = "Keykkjiwihjij";

    @BeforeClass
    public static void setUp() {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @Test
    public void testCheckVin() throws Exception {
        String url = urlPrefix+"/api/checkVin";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("vin", "LBVKY9103KSR90425");
        json.put("sign", EncryptUtil.sign(JSONObject.parseObject(json.toJSONString()), partnerKey));
        System.out.println(json);

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyCheckVin() throws Exception {
        String url = myUrlPrefix+"/api/checkVin";
        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("vin", "LBVKY9103KSR90425");
        json.put("sign", EncryptUtil.sign(JSONObject.parseObject(json.toJSONString()), myPartnerKey));
        System.out.println(json);

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }


    @Test
    public void testQueryByVin() throws Exception {
        String url = urlPrefix+"/api/queryByVin";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("vin", "WBACR6102L9D22001");
//        json.put("callbackUrl", URLEncoder.encode("http://39.100.117.169:8139/callback/order/get", "utf-8"));
        json.put("sign", EncryptUtil.sign(JSONObject.parseObject(json.toJSONString()), partnerKey));
//        json.put("callbackUrl", ("http://39.100.117.169:8139/callback/order/get"));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyQueryByVin() throws Exception {
        String url = myUrlPrefix+"/api/saveOrder";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
//        json.put("vin", "LBVKY9103KSR90425");
        json.put("vin", "WBACR6102L9D22000");
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetOrder() throws Exception {
        String url = urlPrefix+"/api/getOrderInfo";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("orderId", 1464605749);

        json.put("sign", EncryptUtil.sign(json, partnerKey));

        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyGetOrder() throws Exception {
        String url = myUrlPrefix+"/api/getOrder";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("orderId", 1464620323);

        json.put("sign", EncryptUtil.sign(json, myPartnerKey));

        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyGetBrands() throws Exception {
        String url = myUrlPrefix+"/api/brand/list";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyGetBalance() throws Exception {
        String url = myUrlPrefix+"/api/get/balance";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetBalance() throws Exception {
        String url = urlPrefix+"/api/getBalance";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("sign", EncryptUtil.sign(json, partnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetChannelList() throws Exception {
        String url = urlPrefix+"/api/getChannelList";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("vin", "LBVKY9103KSR90425");

        json.put("sign", EncryptUtil.sign(json, partnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testReportDetectData() throws Exception {
        String url = urlPrefix+"/api/getReportDetectData";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", partnerId);
        json.put("orderId", "1464605777");

        json.put("sign", EncryptUtil.sign(json, partnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyHistoricalOrders() throws Exception {
        String url = myUrlPrefix+"/api/historical/orders";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

}
