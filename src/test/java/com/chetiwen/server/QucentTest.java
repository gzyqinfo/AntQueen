package com.chetiwen.server;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.object.antqueen.AntOrderResponse;
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

import static org.junit.Assert.assertEquals;

public class QucentTest {

    private static Client restClient;
    private static WebResource webResource;

//    private String myUrlPrefix = "http://localhost:8090";
    private String myUrlPrefix = "http://www.chetiwen.com:8139";
    private final String myPartnerId = "test";
    private final String myPartnerKey = "chetiwen";

    @BeforeClass
    public static void setUp() {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }


    @Test
    public void testMyCheckVin() throws Exception {
        String url = myUrlPrefix+"/api/checkVin";
        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("vin", "ls4ase2a2hj105826");
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json);

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testMySaveOrder() throws Exception {
        String url = myUrlPrefix+"/api/queryByVin";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("vin", "ls4ase2e5hj150813");
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
    public void testQucentGetOrder() throws Exception {
        String url = myUrlPrefix+"/api/ctw/getOrder";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("orderId", "A124D53720ED41778A6398CFB1F569C2");
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
    public void testQucentGetOrderReport() throws Exception {
        String url = myUrlPrefix+"/api/getOrderReport";

        JSONObject json = new JSONObject();
        int ts = (int)(System.currentTimeMillis()/1000);
        json.put("ts", ts);
        json.put("partnerId", myPartnerId);
        json.put("orderId", "200FD7C53A1942C3A8788988449D52D4");
        json.put("sign", EncryptUtil.sign(json, myPartnerKey));
        System.out.println(json.toJSONString());

        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,json);
        System.out.println(response.getEntity(Object.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void errorDebut() {

        String input = "{\"reqTime\":\"1593608382938\",\"gid\":\"1042stbRfK1593607670HykvzbqQNnMw\",\"userOrderId\":\"44C16AB46C05468EA09E8F471FF94CA3\",\"data\":{\"resume\":{\"sc\":\"0\",\"mm\":\"49909\",\"ma\":\"0\",\"sab\":\"0\",\"od\":\"0\",\"bw\":\"0\",\"mt\":7,\"en\":\"0\",\"wgj\":\"\",\"fr\":\"0\",\"lastdate\":\"2019-01-16\",\"tc\":\"0\"},\"mc\":[{\"mn\":\"49909\",\"st\":1,\"rd\":\"2019-01-16\",\"pt\":\"更换机油机滤空滤;更换自动变速箱油;更换防冻液;免费\"+1\"服务活动发动机仓清洁;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器滤芯;自动变速箱油(ATF SP-IV);\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"41726\",\"st\":1,\"rd\":\"2018-07-19\",\"pt\":\"前减振器支柱总成, 一侧, R&R;\",\"material\":\"左前减震器;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"18128\",\"st\":1,\"rd\":\"2017-07-24\",\"pt\":\"更换机油机滤空滤;免费\"+1\"服务活动发动机仓清洁;2017年“惠”享盛夏服务活动;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"18128\",\"st\":1,\"rd\":\"2017-07-24\",\"pt\":\"更换机油机滤空滤;免费\"+1\"服务活动发动机仓清洁;2017年“惠”享盛夏服务活动;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"}],\"basic\":{\"year\":\"\",\"vin\":\"LJDJAA149G0462946\",\"model\":\"\",\"displacement\":\"\",\"gearbox\":\"\",\"brand\":\"起亚\",\"es\":\"\"},\"time\":\"\"},\"msg\":\"成功\",\"code\":0,\"customerId\":\"0f092952d9a2f7a0c0faea927e178396\",\"charge\":\"true\",\"productCode\":\"BA610030\",\"version\":\"V001\",\"state\":null}";
        input = "{\"reqTime\":\"1593608380933\",\"gid\":\"1042stbRfK1593607670HykvzbqQNnMw\",\"userOrderId\":\"44C16AB46C05468EA09E8F471FF94CA3\",\"data\":{\"resume\":{\"sc\":\"0\",\"mm\":\"49909\",\"ma\":\"0\",\"sab\":\"0\",\"od\":\"0\",\"bw\":\"0\",\"mt\":7,\"en\":\"0\",\"wgj\":\"\",\"fr\":\"0\",\"lastdate\":\"2019-01-16\",\"tc\":\"0\"},\"mc\":[{\"mn\":\"49909\",\"st\":1,\"rd\":\"2019-01-16\",\"pt\":\"更换机油机滤空滤;更换自动变速箱油;更换防冻液;免费\"+1\"服务活动发动机仓清洁;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器滤芯;自动变速箱油(ATF SP-IV);\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"41726\",\"st\":1,\"rd\":\"2018-07-19\",\"pt\":\"前减振器支柱总成, 一侧, R&R;\",\"material\":\"左前减震器;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"18128\",\"st\":1,\"rd\":\"2017-07-24\",\"pt\":\"更换机油机滤空滤;免费\"+1\"服务活动发动机仓清洁;2017年“惠”享盛夏服务活动;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"18128\",\"st\":1,\"rd\":\"2017-07-24\",\"pt\":\"更换机油机滤空滤;免费\"+1\"服务活动发动机仓清洁;2017年“惠”享盛夏服务活动;\",\"material\":\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\",\"remark\":\"一般维修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"},{\"mn\":\"4812\",\"st\":1,\"rd\":\"2016-10-16\",\"pt\":\"免费\"+1\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\",\"material\":\"发动机油;发动机机油滤清器总成;\",\"remark\":\"保修\",\"ie\":0,\"type\":\"\"}],\"basic\":{\"year\":\"\",\"vin\":\"LJDJAA149G0462946\",\"model\":\"\",\"displacement\":\"\",\"gearbox\":\"\",\"brand\":\"起亚\",\"es\":\"\"},\"time\":\"\"},\"msg\":\"成功\",\"code\":0,\"customerId\":\"0f092952d9a2f7a0c0faea927e178396\",\"charge\":\"true\",\"productCode\":\"BA610030\",\"version\":\"V001\",\"state\":null}";
        input = "{\"data\":{\"orderNo\":\"2020070120475076600838\",\"data\":\"{\\\"resume\\\":{\\\"sc\\\":\\\"0\\\",\\\"mm\\\":\\\"49909\\\",\\\"ma\\\":\\\"0\\\",\\\"sab\\\":\\\"0\\\",\\\"od\\\":\\\"0\\\",\\\"bw\\\":\\\"0\\\",\\\"mt\\\":7,\\\"en\\\":\\\"0\\\",\\\"wgj\\\":\\\"\\\",\\\"fr\\\":\\\"0\\\",\\\"lastdate\\\":\\\"2019-01-16\\\",\\\"tc\\\":\\\"0\\\"},\\\"mc\\\":[{\\\"mn\\\":\\\"49909\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2019-01-16\\\",\\\"pt\\\":\\\"更换机油机滤空滤;更换自动变速箱油;更换防冻液;免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;空气滤清器滤芯;自动变速箱油(ATF SP-IV);\\\",\\\"remark\\\":\\\"一般维修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"41726\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2018-07-19\\\",\\\"pt\\\":\\\"前减振器支柱总成, 一侧, R&R;\\\",\\\"material\\\":\\\"左前减震器;\\\",\\\"remark\\\":\\\"保修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"18128\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2017-07-24\\\",\\\"pt\\\":\\\"更换机油机滤空滤;免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;2017年\\\\u201C惠\\\\u201D享盛夏服务活动;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\\\",\\\"remark\\\":\\\"一般维修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"18128\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2017-07-24\\\",\\\"pt\\\":\\\"更换机油机滤空滤;免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;2017年\\\\u201C惠\\\\u201D享盛夏服务活动;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;空气滤清器过滤器;\\\",\\\"remark\\\":\\\"一般维修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"4812\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2016-10-16\\\",\\\"pt\\\":\\\"免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;\\\",\\\"remark\\\":\\\"保修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"4812\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2016-10-16\\\",\\\"pt\\\":\\\"免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;\\\",\\\"remark\\\":\\\"保修\\\",\\\"ie\\\":0},{\\\"mn\\\":\\\"4812\\\",\\\"st\\\":1,\\\"rd\\\":\\\"2016-10-16\\\",\\\"pt\\\":\\\"免费\\\\\\\"+1\\\\\\\"服务活动发动机仓清洁;16年惠动金秋服务月活动;智跑2.0L免费首保;\\\",\\\"material\\\":\\\"发动机油;发动机机油滤清器总成;\\\",\\\"remark\\\":\\\"保修\\\",\\\"ie\\\":0}],\\\"basic\\\":{\\\"year\\\":\\\"\\\",\\\"vin\\\":\\\"LJDJAA149G0462946\\\",\\\"model\\\":\\\"\\\",\\\"displacement\\\":\\\"\\\",\\\"gearbox\\\":\\\"\\\",\\\"brand\\\":\\\"起亚\\\",\\\"es\\\":\\\"\\\"}}\",\"time\":\"2020-07-01 20:48:50\"},\"retCod\":\"1000\",\"retMsg\":\"查询成功\"}";
        System.out.println(input);
        JSONObject result = JSONObject.parseObject(input);

//        AntOrderResponse orderResponse = DataConvertor.convertToAntQueenOrder(result)
        System.out.printf(result.toJSONString());

    }
}
