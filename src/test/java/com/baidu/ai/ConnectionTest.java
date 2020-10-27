package com.baidu.ai;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.baidu.util.Base64Util;
import com.chetiwen.baidu.util.FileUtil;
import com.chetiwen.baidu.util.GsonUtil;
import com.chetiwen.baidu.util.HttpUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

public class ConnectionTest {


    @Test
    public void testCar() throws Exception {
        String url = "http://39.100.117.169:8139/ai/image-classify/car";
        // 本地文件路径
        String filePath = "/Users/eltonlin/WechatIMG355.jpeg";
        byte[] imgData = FileUtil.readFileByBytes(filePath);
        String imgStr = Base64Util.encode(imgData);
        System.out.println("length is "+imgStr.length());

        JSONObject json = new JSONObject();

        json.put("image", imgStr);

        Client restClient;
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);

        WebResource webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(String.class));
    }

    @Test
    public void testDish() throws Exception {
        String url = "http://39.100.117.169:8139/ai/image-classify/dish";

        try {
            // 本地文件路径
            String filePath = "/Users/eltonlin/WechatIMG26.jpeg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            System.out.println("length is "+imgStr.length()/1024f+"Kb");

            JSONObject json = new JSONObject();

            json.put("image", imgStr);

            Client restClient;
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
            restClient = Client.create(config);

            WebResource webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
            System.out.println(response.getEntity(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testVehicleLicense() throws Exception {
        String url = "http://39.100.117.169:8139/ai/image-classify/license";

        try {
            // 本地文件路径
            String filePath = "/Users/eltonlin/WechatIMG30.jpeg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            System.out.println("length is "+imgStr.length()/1024f+"Kb");

            JSONObject json = new JSONObject();

            json.put("image", imgStr);

            Client restClient;
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
            restClient = Client.create(config);

            WebResource webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
            System.out.println(response.getEntity(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testVin() throws Exception {
        String url = "http://39.100.117.169:8139/ai/image-classify/vin";

        try {
            // 本地文件路径
            String filePath = "/Users/eltonlin/WechatIMG31.jpeg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            System.out.println("length is " + imgStr.length() / 1024f + "Kb");

            JSONObject json = new JSONObject();

            json.put("image", imgStr);

            Client restClient;
            ClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
            restClient = Client.create(config);

            WebResource webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
            System.out.println(response.getEntity(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

