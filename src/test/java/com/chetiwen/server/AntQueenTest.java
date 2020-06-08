package com.chetiwen.server;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.object.AntRequest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.util.AntPack;
import com.chetiwen.util.EncryptUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;


import static org.junit.Assert.assertEquals;

public class AntQueenTest {

    private static Client restClient;
    private static WebResource webResource;

//    private static String urlPrefix = "https://my.51ruiheng.com";
//    private final String partnerId = "68920907";
//    private final String partnerKey = "A3883A3D6336F291292A0A4FBD3F74E5";

    private String urlPrefix = "http://localhost:8090";
    private final String partnerId = "12345678";
    private final String partnerKey = "Keykkjiwihjij";

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
        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString()), partnerKey));
        System.out.println(json);
        AntRequest antRequest = new AntRequest();
        antRequest.setTs(ts);
        antRequest.setPartnerId(partnerId);
        antRequest.setVin("LBVKY9103KSR90425");
        antRequest.setSign(EncryptUtil.getAntSign(antRequest, partnerKey));
        System.out.println(antRequest);
        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,antRequest);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

//    @Test
//    public void testQueryByVin() throws Exception {
//        String url = urlPrefix+"/api/queryByVin";
//
//        JSONObject json = new JSONObject();
//        int ts = (int)(System.currentTimeMillis()/1000);
//        json.put("ts", ts);
//        json.put("partnerId", partnerId);
//        json.put("vin", "LBVKY9103KSR90425");
//
//        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString(), AntPack.class), partnerKey));
//        System.out.println(json.toJSONString());
//
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
//        System.out.println(response.getEntity(Object.class));
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    public void testGetOrder() throws Exception {
//        String url = urlPrefix+"/api/getOrderInfo";
//
//        JSONObject json = new JSONObject();
//        int ts = (int)(System.currentTimeMillis()/1000);
//        json.put("ts", ts);
//        json.put("partnerId", partnerId);
//        json.put("orderId", "1464534047");
//
//
//        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString(), AntPack.class), partnerKey));
//
//        System.out.println(json.toJSONString());
//
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
//        System.out.println(response.getEntity(Object.class));
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    public void testGetBalance() throws Exception {
//        String url = urlPrefix+"/api/getBalance";
//
//        JSONObject json = new JSONObject();
//        int ts = (int)(System.currentTimeMillis()/1000);
//        json.put("ts", ts);
//        json.put("partnerId", partnerId);
////        json.put("vin", "LSVXJ25L8H2029141");
//
//        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString(), AntPack.class), partnerKey));
//        System.out.println(json.toJSONString());
//
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
//        System.out.println(response.getEntity(Object.class));
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    public void testGetChannelList() throws Exception {
//        String url = urlPrefix+"/api/getChannelList";
//
//        JSONObject json = new JSONObject();
//        int ts = (int)(System.currentTimeMillis()/1000);
//        json.put("ts", ts);
//        json.put("partnerId", partnerId);
//        json.put("vin", "LBVKY9103KSR90425");
//
//        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString(), AntPack.class), partnerKey));
//        System.out.println(json.toJSONString());
//
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
//        System.out.println(response.getEntity(Object.class));
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    public void testReportDetectData() throws Exception {
//        String url = urlPrefix+"/api/getReportDetectData";
//
//        JSONObject json = new JSONObject();
//        int ts = (int)(System.currentTimeMillis()/1000);
//        json.put("ts", ts);
//        json.put("partnerId", partnerId);
//        json.put("orderId", "1464534047");
//
//        json.put("sign", EncryptUtil.getAntSign(JSONObject.parseObject(json.toJSONString(), AntPack.class), partnerKey));
//        System.out.println(json.toJSONString());
//
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
//        System.out.println(response.getEntity(Object.class));
//        assertEquals(200, response.getStatus());
//    }
}
