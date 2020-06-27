package com.chetiwen.server;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.util.EncryptUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;

public class DemoTest {

    private static Client restClient;
    private static WebResource webResource;

    private String myUrlPrefix = "http://www.chetiwen.com:8090";
    private final String myPartnerId = "test";
    private final String myPartnerKey = "chetiwen";

    @BeforeClass
    public static void setUp() {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @Test
    public void testAuthentication() throws Exception {
        String url = myUrlPrefix+"/api/authentication";

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
    public void testMyCheckVin() throws Exception {
        String url = myUrlPrefix+"/api/checkVin";
        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("vin", "LVSHCFDBXDE195142");
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json);

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMyQueryByVin() throws Exception {
        String url = myUrlPrefix+"/api/queryByVin";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("vin", "YV1CT985881450469");
//        json.put("callbackUrl", URLEncoder.encode("http://example.xxxx.com:port/api/callback/antqueen", "utf-8"));
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
//        json.put("callbackUrl", ("http://example.xxxx.com:port/api/callback/antqueen"));
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
        json.put("orderId", 1464652064);
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
    public void testMyGetOrderReport() throws Exception {
        String url = myUrlPrefix+"/api/getOrderReport";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("orderId", 1464661425);
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }
}
