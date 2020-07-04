package com.chetiwen.server;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.qucent.QucentOrderResponse;
import com.chetiwen.server.qucent.RSAUtil;
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
    public void errorDebug() throws Exception {

        // 公钥
        String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
                "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
                "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
                "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
                "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
                "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
                "4wIDAQAB";

        String input = "{\"encrypt\":\"AYBeEYVS/igkqzFVafPgpFEvepU5jQTAG/NDQ4GOVWgL0lTbDR2zDZNjMwzo4S3epybps6TdMsWTzNuHYuqciVzFwe8rM+qQGlKKqY/r\n" +
                "fyUVAFcAl5XIqF5RCHL08XbN59J7p+DN+e1k/RxWxQq89HML+Nk8E17Q0ogmEAQZj02xNyXZtvCcYG1DOb/t7428PIR73JBN5vWlbAhxT4b7axFuaVQIdyUhKLNyRjZlxE2v1elr5ZaVZ4dCO22db4Px2EfpuVmXbnEygcuL6yMhxOifo+UIoculDiae0SDrkMA3xEj15l\n" +
                "PTkrcMaXSCeXqaTjGIq7eaDUgknTzNLQRaTXjzIMcX8ezmKBd3n5VSLfUGoRjkU2BxFenOyGGsHydZdbOS0Kvt8rqns6z/Bf4oY7xF8PdOvKRCWVERzUUSDMNVaIpJz+ufHMrw/2uboM1beIRY0MobeZmgi5BqolRIvEDQWxx3DHVfwrX3SXah8mw1YuwKn6HdUm/39iz7\n" +
                "hvevmls/iGEATahG5maXU/ZP/q2b8DQtjLuSgQMZdqbxBV7Z6tIw31LM29XNncWQWXi3UPv1ZvZCHgt4XRZ2y1Zh8dyP0bhzrZevqrx2suJWtromLzyFFad65A9Gh5/H9R/jq4aklAB39eTAjAO5TA4TFEG64nCPyVQqKKH419mTXZGg3v1ocLd+1FC6QJa/LUUQYEpYbI\n" +
                "JziZQTgjkAXQuWae5mbUYwH91kyev0x8rF3hfjUu8HJQ0s7PjzdFlGmrMQOY4LPRQ4onn9d7q3jtFjydweDt78WuXImbjnHfirBW8RtXV5TD0PqORhzl9e9uFOJDAuw0/Wt00HOmGhcODR2j1U93Bje8MSaeTvbobgIdMQYi7yxU9Pt4nx3I8NN7gpR8rxavygafMk+l78\n" +
                "LMQs/C/Tj/6/5zT2ZaTUilKz8i4cT0eIRCkUhY0IBQlSe5tXClK7qShNi5eIdEDQRMrrOxN4/qt3x0vYtzTR6GXeuo6FDEL9OSbe5mq1LpTZljEVB0MR0X2OG7/c76EBTzLCtmJQQPiiPO535uCYky1cYC9NelppqzepTvXbb0gG1nwCOkToBiVfblnRaZgQNm80UkBphd\n" +
                "WNnQ5kXj8I38XQUOvLRsBxTqUAgD/puHzAbkKCrTf3xXMC+fY3hUeiLBM5KtMgW4Pjzb+P3URSPqjEFxj0htW8ukZptJDbb0ibG2Nz4xW3Vc9YxH0WC7aLvqB9I1EIQFL+9wBFVPNuda3PjoOv8LxautkWAj/Euf8gFUP+AhNL9nT9HnuGgUFxxQR0QrDs6D4PpKDsAAIC\n" +
                "tO/7xopuR7AExlGdnceA7LCNem9U/oqhuXnB70sNbtzrmMUnfpRH88uvrdkdn3x8k+SicuPcEmRRmM1u0iGXRTCugF4jEf8JlZQFkfqM2Vv2yklRfQmPpFY9dm9HczKe/1eWQgz3GBNgl95nvNNHCgHW+z4s7hcxZVRiHm4h613ynzt98BqaIYW9DXd4aeV6NpElHBpfzj\n" +
                "S10ClWwS/JfSRCCfo5PY+aE85H3/ws0OeefnfQAMv3RV7N0saK25YbzlFe56f/9lozBebV3i/MayT+CGuFntReOcvnD6khZZl4p2Kx8QZTYDNsKLcvS7il+0JNuluY//q42Re7jWCXuqOV5/NGvILaVv9ucrrLTezx/7ouIxRSDA05ZH4VfjQsa73CVrSz4rv5a7NFFta+\n" +
                "JLVYh7QFHHFLkxTB1NVpQGqwMpyzo03ouYj1ejy9kia99Ms5yN8iMmo/9hOV0HYr/CbfBz1hCSbIsrBV0LdM3myqd5csydhXMaFbh6MilxETaho7IEbqpBO/PaLJtnsI2uKMSNrCWCCe+L9nMvo7xU0rmzAxEsRoBdztwyox8uDmT6rCzNZeSCXdaDGOMhblIDue1G8xHT\n" +
                "Jipa9njjy6RWwliWliBvtdGpehMrI9+WOoRQI7i1UmNpONqABaZu1bD228IQ59fGt66DCi4S22AeF1mL9oJJZ4RklQQ1LHCNSFy1VGSaBmBl/BWdyIvlRZ6LmQbsfXjXR4bJCFz3WiOn+nZS2CoyVqrU+cw0JphInmOOJWpkrF2ugXJdUK+isQwlorNTyBpcycAo+2VrRI\n" +
                "jqonBqRQA9tdbtpOxMYnE1q3/esQXBpsIInncYyV2i9oe05tNAIaT4n55i4BKX0X9L9IaZb3GuwHW4qoUgS3wv4LFZrAAw5b7w9R9B6ukBPNIGPnWTMBsoYjj9JQ7VDtRrJ9TCuWsyfyVAWmJ72TEka9gJPilcYal21sfuZU88FZ3jm2FckTwDtcRM9IwnWaijSeFX2UE9\n" +
                "9PsxmGiJvO32tle/TD1Wt5bswqlhW01HwxI3mnsX61IFivLqxNA02f3QUFBLhGJ4EHsdbqqFBzJ6DCqdxPvDObNHiONm7tUKgkzz7VVw7hUk8oJJnfwONIr6eqVQAiSJEVvj8MP+/aOMU9wTNAgKSDGEyzSIQiyACr2ShB/9IZO28eGOhdgZ4KSd3YUF98aiyuUmxn2dBX\n" +
                "4ngTJ2j+Hf9pM42RvcftsZ75bXVu3b+AtYCs4JDSUVKaI+m2GAErhDY/bn7/P73m6PTBlHsdDjSgoNI7LH8VipMMVVqROjP4uU4UVIA4At1I5g8uXvv7QgVnt68XMJPQ32/1guxzHDT1JUe+Zf5mAQ/r7oKgsgZ8hfxVS/j82/lzXfhmbAe8+JmnSJye4E10UA8A+L5EYW\n" +
                "sbb9X6YwUX+rl+WcjSPsRnyB77DXVNG0wePg4nG0M/XdPjv4aK1hjSswNnjCX8zo7h2Sof3mbzeI5FlFJsKGqIyx5F7PS45avLF3wmtue39h7GJscqO983EsRLzaO/EuzLGExQDB6YFEfPeWsax6c1WJCsNhE3J+DzvLEKau+ZqhSWAqbDuPj7AKFDqIfs5JmkTnA08sPm\n" +
                "cEw8SKrMT+CawFyKir267aQ73CPlhy6bkgPk/ETgPJVu7WGGWA58TvFSEhV5rE8VL6Wc3UjjFSn5zZPcp/ePdg8ee10zFVY797AJynr4+yMqxd7J0ikCzkIc6Y4xpn7aT5AaDz7ffDCFeSeRrQEHtk3sAS9Ezn1BhrEZvEKd/RWlnZOD6fuAZtn0Xsk0lW9VSxOEIxdcWc\n" +
                "7U46qLrU0sxl3wbuaVaUciQxRn5P6baXvrfoPFcpxcuM6H/OefV+PjyqK1MoBu+kTgmtghlQJ8ZrhwBEo2/43Ofe/0PKR/THu325Q0MRh+AMZC4R0GlkFRgXh7xgoVdsshsaoWAJdohjNjCDkhPkjpC/hz4Vbmr6XL1p7cfcq13JG+XGKckQQeuZNAdq3P3VS1iW10VnSN\n" +
                "VuQOqvRW3VLJHpV+QBlCO0fRc4rvVQtEG3URc6lPKXpTVsy8T+NPhPFj4QNYN1Zhk0ko9mJlyRU+0RNf//SLBiRjCLbaIoi5fGyHXM7dFlrUF1Jgwr4dXhb7dImbWK3ZxgkR0xMrQmDaJolADwnIm7uwzTw1Ddh68PKUGvwhDqToeNVSk/d1BKYsAbiPVMXu0iLsdFkznB\n" +
                "UKgszdRl7mbUsyQBxzNBvWWoXNJFsOY+0fCEMdNbeaZifQAs4jqHZCA/qWKPdgEiRzAGZscuNxm1Uyaa39Olux1s8PZa1O3/Ko5NHWT1tmoMr7lpzpVIxv0fCNaXMTJ1N/SEqlqolGeCw4d43AJeYn6lQ/g2Uzq67Mz8kW7LAiiOTmPg1nLagHg0yuPJB91CN9sGtHayGR\n" +
                "pF92CbJXfCiSikhKmZ/iJSUlK4azmScKPSDct3mIPQmvfSQFrWgTF/coPU3+SdymUoO17CYC99qWTuzImuZkINIPm3OUTC3I6gWpcKHfn0TChL+RQF9DR4vQymFptSdia1tNC9f/tPBQsq1bhHGiQAUW0TfzwPSPMQxt5qysEYyU6Xd9Shvq73lkldSuei6l8nZvUm2LwQ\n" +
                "xyq/yWxTzdQHQoeZwOuCTPRzVUKmkjQnnZMew3z7lIfj+CbLChnE4I6F/BHWAVEy6qtLV1KPXUm/HhU6C4UcFYOdKYyJ7uCwS121fUoZ8uKpRlXbqYD48PTNk3D+msRaagoXv4/XJPqcc9sbploC7EZP55OV4XmsF2aJW+eiN+xQaZ2fabJndO9xakuuKPqIBXFS0h4R/7\n" +
                "6sdqupz52mKeuOtxFqElu+DOUxmWVgsB8Ks36WjLw9FuotnzSx1uOYkpy+yTNV6/ywd07kDkFstoSFILomXb4LUKNh44DjnLWki94YdT8XR/e6TK18B0yuxUn3Kck66YSFKq+nSW3JpBC57QvjEN4qWFRfnIus8W0djSGfv3FGX7p1ZuHpwz/oVBNOVbc1r9iUUDAcwTTd\n" +
                "5RK7gD9tJlSqen5mjLq7uez+EFs0dIrHjsyN3RVRhmvlTYmV3AV/nqJtdhqrDIXF2cKU5Js330NLtH1m3ZAoxcJh7PConyy4WOf00uZGviRfGYXfAm3QFWxNvNW3HD87/c9CQrZO2kvw7qIJX6gFvj+CdQiB+WIY6Cjcwrnv+511c3+a5Igg8mqQ6IUNCVhfJQnP2DzfDc\n" +
                "wP+kvuFMFiFzgFZJv05orVO895OxFEMxU+OJXQ4aPWjjxLv1+mkh6hzHbpteTfpYG1Dvy2L6IQ7o27QYVCLMLfPcpRnPQfxMhKNhJmmFf4OiDuHBm9O0lOxkwIcmtvVa786EpmkKiAjgqhoXwoTDLbaTYWWC5nos9qAVTV7aiB3rubhpf5ONMLyfJn//AI+fcDpz1n3yrk\n" +
                "IEPE+wQUJiJZwD4pg0RB1rRjiwyQiDucX/aaEo93zcdFuNOIgao9hO0QVb0J3q+UxnUN1QUJuOgD8/15XIjhGiXshmgH4vFqZrlAahHyvK71Xil2IBlMo/vxVNlxuS6fE27ctwDImfw6P/EG6Hq4hV8x9vaE+en+56zm0eINZhTskNb+Pfi8dRI5ruhXD9qymcjkQw8A10\n" +
                "GJCZ5KEWixpDw1K/CpwO//HDBaujkOCf2qQoLhFiqIp9YuSsapXbyOJxG6eF3QWvD5xKJ38vcN2oSL9YqWrehatZE7yt2P9eQF1F9nMIBiHnupAbblpA1Waf536Pj6UAlqcpIyPKltMSM1jtARqdRvVRsSdCyfPJWOrsouuxK1J+rJWPpsLyWVjS1mpAmrYisyPJ4Sgwnj\n" +
                "XzJh8OXtpGOksjuiNG0RUAps1YO5V8lOvULnUrTIcuUAJgvKH1bGN62z+wSE/v54OGaBNNd55C9aXDkxom5tFU+MhYMiTuTGN4iKacLo0amQc/RhyYepH2tsTUFj7M5XDhEfEW2cu8eP2H0Goj0LZmVOoxQLQZvZywgReS2MzEnTwIjXpsnHZfNvpgUYRmYo086MNJWdMT\n" +
                "53jozQiO6QTusMa+tXRNqPjIrohrlR8ZQEAimk1wpQQdy9gubq3pC1Up63sY2ELgHBKIaxGwPVn59WWdw2CxvqWaHG/kJUdD+vnq/u6fUyBqLIdb86THx7NIXxNewEgbv3nD79dNHyuZF5rUmifWwhJtE0dM0LGFY9OPTii9Xm4GTxoJIo1/dt6lqsRqRD7VtXVwcEOOqX\n" +
                "Hu0g3uwY6vm4WOa50p8sGagA/GEyxWRv03dXLfD+el09nnJmHUEeem3qPKmvHPp8ESVMq//ENzj7p5S0SbFsq7L3NuFspi7+LlBSv3o0Xo+KSJL8A41zLf6tyaSFI8J2y2rNgN+CUQ15n/UxWBW1fEyxoQSzbEJtrekmf64VQ9LZULKcdBuufG6mdOXOLk7AkGNgL4GViS\n" +
                "Bue5LUuA9zE3VsPqhyNcGL0rvWlXtCC1SdiEAvQQcfw=\",\"sign\":\"f51c9ee3b314b75803c0063d9488b0ec\",\"encryptType\":\"true\"}";

        JSONObject result = JSONObject.parseObject(input);
        String content = result.get("encrypt").toString();
        String encryptType = result.get("encryptType").toString();

        String decrpted = DataConvertor.convertUnicode(new RSAUtil().decryptByPublicKey(puk, content));

        QucentOrderResponse qucentOrderResponse = JSONObject.parseObject(decrpted, QucentOrderResponse.class);


        System.out.println(decrpted);
        System.out.println(qucentOrderResponse);

    }
}
