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

        String input = "{\"encrypt\":\"CuAmtOmQ6Xjqwblx5acXEP7FbvAXWDpTK6GNPxrqiw5cFzfNvcWpaf2IMblNtLXTSkhLzHdxDSv7+UwWAlCGmuwdLY93rLZUaBJnnxLm" +
                "W0pi3voLKT/I3nxNUvsOHICMl/y4h8eWOTDYcE9nJe+2CHMNtrLMx8svS6BK4c4wonE4nWow99Mj8ou9L9v9OZg4GssuAkuaxF9PJrDNwO0JDLK1nuu947BDvKj45aVpkcsLTU8i27Px/yu0iX6l1I6zLtSFX4MpqU0PiN0pcKYnUbtw3W4w1VG0E7+DLUwnTjy+K8KCw9" +
                "dhSFog35DgjoZibCbpCYFwF1ql1U/YbPMLLWRnE5air4keqb/z1mjOnSmfuceeDb34IVJvVZaCj5H+3+lOqaSQE7hwZyWYchfk2u8zRW+PplT2peynojjRx+slfF9akWWVpq8QtPv7lwShtMmwyJcfuvCHMHMDx5L9p9IXhLoe1VnOK5YDXKifaLMCzvUjTwxE12gw0pe9" +
                "BraLZzNZi+H6btyDbiklTRdL6BWLozV2qtDlGM/OKceC3HjhU+oTVM6GpGAYLuEmCzy1a05e/HpLYPLB0Crh7yVu/CSaORLHfTv0DHdku8zAqsSHxscCK9gE7nDHpjpJ5NHMX/kPyALRWiW7x4LFHmRPaG+it13GMKTovLq9DHegNvdoRknP0W2ye9ZUivn5akFNOLyQVK" +
                "0UXgTPGZCmJ0V+ZCi3LrKXO9J7ENnjDlJMxcbYJNoLLebYrx4kw6cfv9NCYLNZ7aLP1V3Alt8jnPeNhEoSJc4AaNz7vR6ekbnbZ4HpGDpiVAumMr8RxYYWH8WIEEI6uOQt7M7//DQVuy4mUBfC52lykArvJLzNv75PzhePfc8yOdQbzizo3W1nCMd+PUiThFnt+4ER55J6" +
                "TzHe+78ENgY2h7UVe7XBy6CMjtto+eXwHsS6kILrDZrZywue+6LHoodd/LR/RfsKmHupB/s43xYnzA/LMhO5QqXjEZv0VaVthdR3dLUic89Ro14AsXOOSGTg1BtHlwawUZCBVKKG7rDKO66lZzWU7gnSc+VcjSuuJMYnVnfax/JhSClZUcfdC4BMcXKBRuWGaaa7xeebgo" +
                "EjYZhe9impQSnt3T0dsmZal0o+jek/TxmBMzfylCd0wve1avN44tNLfS0VJ2t53vW8jMuCmQtZcTNeFmfsXFEesEX8H2itcnlBXMRAEKiyRm1xCmc0S3zK5GRx81DU2nkbhQCk4g4wbf1m66Ff60eznuR7MF56mHCAb1U4srcQJgbHfRX0D4GlDLTd9GXZhrg3GFLzE+JG" +
                "YTcKeecmlUfLRVPaiCiwJ6QGAjN6+wHvYEIqeyS5Z8W3GMcHaG3VnOtOozMntADvcNzzMyL3hqBiE/wrbSqA6Tr8ts6JKwTfCzkmulXD3pjGmm70OxPA5XfCVuKFxnc6F1+/7E5L8muH5i5yihoAJHSOyiuJgl7daf85Bc5dcbC1nDOixbJkVBXi8HU/1kIPMnlDldyUxn" +
                "RKa2HdPAC1e3lJiF/4CXrG6YufjRfLNeyVihJ+7PQD4Q6hlrZBeB62HnyEAOX+Ro/qJPv7oRwSz0mB29QImPNHsTPNbHh58jCWDYLCK8qhRqNt2SdhaaJf1OneqaMyL542IHxipWLWYKeY7hhFxRSH7xv/pWMHlO8EojrpQZhufRsrLU9DwBQREndaQjV9WBESPIB3vPAp" +
                "f7ec7IxSkQvdA2x2KXffdZsJCVZepJpQR5I7voYIG9Fl+Go1mEQEFYCfXjDRrRy2XQkgQgyiwCrjTThKZiWk4SiHTREMZLH7If5O9CjItVNu7+GRqc0fPcCp3ghY/uCLNbyPBhwGGg4mFC/YEzX1smJVtzB4U4zWxAAPCF5ODoL+HxQNxeIH/C9405NMYjCS5rBFt073/d" +
                "BwweqfIu+gIOU9J3kRgqim7WfafvplpFuIzgv6T1CNf2iBhFWCJqTGYyL0CSXoItm+4G+edMsZhnkceoXTS3cuIjfpjPAungOqwkD2elNcRA3Pp85nCyovQ3dtYTOKEvr82osdrGU+a8cultq+LeTvLe0Lt8sHZudnoIUeUMghRS5W/pxqRxxzyCEt3MR+praul+5WTSlc" +
                "7Uni3b5DhMKsqu48vef63APRPfkKGPqY5C4gO5uDBRbgH1BA4nlabI70hEpS4QQz6iokp9KTRrqQUkJPyrh8s4+RhCdaRShsUSKwksW9p8VgqXaQTzZMwWOh75k8YTDp1hJswUFQc9kPwt2taKHCXEhaWQSLmjp8XNp2SQcl4Vwyz3TKa8KVRzo7VLrOIXs7mlkhVSN8RK" +
                "qiyP+1c/xxKuCt1gfNfr8yESRrtpMxBpd1B4Kg1aQqu8VxFmSZd7EuKG/UIi9F/wTWW3YsH4VKb8Jd3NKx3d1eN8ctCyQ5W7G3b162MxRH9T3FSAUFFaAMOu87+D4f/HGZG0t+3IJPJhoI/y94Udw+03a4lMmd760she9dApFODdqG8OGSjEmwB4KKhfszydT2zhHby/qW" +
                "8RQVp9U5kw0+lIEMKQnti2N9h9wL0JASP7F0Zgapj/GugtMX67+gZ9Di9WzfFWczVwv+BXv4ivElebebCgx9gW4bk4c7WasSIkgPlHrEMWVV0n9y8YUIRUhEx7zSohGNtR4H9lGJD9kb30NkaRut2scFVm8CbA1oZ3uDDBEYMszzE9hfsJ/lOEit3SCRh/8PHMTPirppXW" +
                "t/EOULKVMPb0LW+6Vir0y+JSkcJojE906bajBrYhzIGDAi/GaMYTVEAfy7nw0uUnYpY0Mqlz181LfD7EK+bKftiWrPwU4+DBxNdaeADw5T99jyWgEqnWo79lTLF6B8oyGf/7j2fROhusK+DnGMqgV5oTEV58rntozyhpFR5FjFAFhHF4h00aivV2iLz9mc7CZc78RtpERC" +
                "/Mn7g5OSJZjFWsqaeCkaH2+lBlmbJnWm6tNdaAiPJ8lsuGMV1SR+S8+eAcGM/sb6KgQ3iBywTDbwryjCprMKK1FeTBVV+zANIRN4L3tQjiuXMKNtg5VxNEgnAHxJW5U8VaPBQbRTIEeCSSxoX/bDtJVeWQQIjV/ZqpWme3eBYFNC6nLK9wAEQPTPa1ZHzRNnqiTBLhZ3Uy" +
                "UtzY6W6i6zl3T3rQfgHldRcyyXsJcVSPJgQ670zvD3JSKMeEaqx3uCdQ6Y9C7+z9/UU+EMZpc5lbcLYxJGQMl45WJUcK4izVZqhxUntBlAkSrXB0YLjXhJpLWssU6MV5iqzz+paXpztGVQ6pjHkQAH11VeBw24yVzuyJmOiw6RSxGC4iqxILbFfw1xYLAAaXV/Scx6k8Vc" +
                "63gDF8kb23+YcDpE1LxysomWrIL2hjcUySQBUBxrBrvfwvgtVwKo68QvHzyFNfg9c6ML2XKhIVXqXqu4ExADMDPPD3kLyMeGUMUpNVlUoMbJGsANTUTIL1VYdxGTZYl+GzNrnaULjjRUzVElsZLbIfdzjmKSHTp1KxA4udNmSLJiG5glqRPzK8uUuThLWDE1iTp0i4Y3Ev" +
                "adZaJDVKZ2T7GkirzjetgckDvZZN6dLefuqDqDaitvT5uTXDJDTnfpyWWMLT3aKFUPhR8Q4+hZv3DPgL830OO73uhyQAz8okK/tC/4NNFUOEYR6OqnFTd+S2GhNi9YoozfsHQc+bTbpGrHDtroXwcr8J8uaLCxeXECTIYQtUBCaSOEva7Gocbyg3lUiCny9NKp43No0WP3" +
                "KRpkLJyJrdVoeaNGtxYRZJ1W4wG7nKByoctgPaRIpDpwISpb7T6bxm5HS5LcED9XSKb4Nblra7Klpd1+PeAq3aFTjnFMrG6Afpu/D6+ACmPdQI+qVNhqsKryTMBLojMiX8IFj/bi5n2Set6QEHwOVD8eDwZd/VM8XM6kYvLekGJZ5wMVamOVrN9ydIVC6Qo/+FYPjWPy0P" +
                "2SxczBHQrprQ+sJaSAx/Dkwpg/QMWF1Csuxa1hOBloEAUokLAdKKkopyTMZTzjpHCYtAb224MAlbOwqxoylE57m+2Mo1FmaXyljohdpiIyHSloeRuBJ7q8gaAvesSgOxJ5pADoG1xOu34PIOl73v3HGMqwl86/cALdbpfM4Z7Mx/u4CpzvsO5kDzWx+cNsGRyw8ZsUHRXY" +
                "ywJ+VJErrtsuvXHH87k5HdX+ycpPdbqc/9bBSfr0tIggdLPoiCfVws6s3MFbH8LEjHwKBNfiAfjVxlxCqySOut+YnQwdxEUgOiGeTDNzFCBZcfEWkh/2CTyP38aH72PwY+ObU7Y121FKNv9aKD1onG44NofkMYpW2cnO3ynaC82L2Fp6mbzIEm3F0YqHZXUWwfr0lzyXrD" +
                "NlXIZcFzf5hLhUT2uiWzDtXV/8XWyprzCCEw/YUhrZtGqST2KtnAyGuBHpThUpaEgXPxhmoImAcHUs7GODYb9fTOM3Broe59WnLRVLbqk/GSmvnunU/JTB4TKGRa+bF8hRQaEJdR9V3jqnkLJncdtSKANunf6xiwnSyDfi9VIQiiuLIHSvy9kwmPWzPmtFYRJBXJKepApl" +
                "VKXGe7CH4C9oSQtvCZq52SDDP/rt19y9NmzkR9rhgKZieO0PDeHouHMFRn6HV39/blOMyvawNbybIaiW5tXvevKxp7GNe5bdV26WDwwmZO/OPh8ylf7mntnUtl9HWAr61lkEGpjU4ChTWbRMVOsfEkJPqn34rRMtgjYapbz+ogC8y4Z3Oqq2j5fE6IMkHYrp3zQAVitFkJ" +
                "XXW5aDiww877y7feEC6WEqjIa4fOow4xB5SEZE5jrU6IPqDOJmBdcyAVmxzkKgAPIGhAIpeBmP3SX6Ju1IKYVaXUfCcnLcEJrtLYowd8q/0nD3MbxcduzZyKkbTUfq0EvisBYxa4XlIMsRyJEJAcZtgzMTj0BcVuYKyUcA8XG12okcJE/xrTbvVLrL1DZizhannUo6iopJ" +
                "emuM3yS1M0qulQCNMxX/LKg34e9YScBeSUxWaemhPlwfxPAQxcrZst5BElHTLUUac+jZZN4QzmjdLh0Bgv1BD8mzXFN+t3PyGvS10FKGLbfq3yWotKy/ktJbhm5v/SeoDdZDB/OCsaSpjzTusut9TsQjNPHyIsixB9LemE0dgArsIro+Wft9zmcVy6AQTZPqSvX5aKB00w" +
                "CrotyyBD54dLSuLl2q7rNDRg2TRVn2zv9OSwG39IquWQTo2dZTWg8ecfj6t0x8yonySh4Kl5naCJq3Kn9MBkAT22saVE0agnbMLVcTvl/xFR7w1cFG1Dmcq/Qy58wytCHyynnlY8MX6INscyr0lqAQW60U0bzpzIOFOcXVENw8FSEJ6dhwL/p63JDAoGUDTO1qfuI1jXzR" +
                "QtX5ykH6c7L0LHcs/kzgH9TeMafi3aUOA6tZxEqmJR8xS3rL0QNl+NfX3CecuwZ6YKoWSBJFcWFIFJNytRbxvMUlNXSWob8ywN8NvUf6AZE7DS/5I8iZT9vDM7CGyEFph9b0uFHpCPPssqIGUO3HQ+VcpK8aF+NH4yOL1zSThBjhN6K0DNdebttHeLDRbbXtqczzuG8G+R" +
                "VuJbgfJE4L/gXcJSxbTVT/DLoBH7y7Pe5+NPuGSG6qr6WY3HCmLBKDb40dNnbzyo7FI1nTS0B4LbFsZ4zY7VyoRzi7w52nIN626D/qbVwrvNGZZNFAUKz4MMvfzw9n/GRL21BmqNHjNlFy1EvjwmVW3a0US6W9/SpYCEOlIxs6Zp/Yy22TNupPPiP5S7LziTd4e+W6Q8zi" +
                "ZJ6M8sehJfx3b1UK9RhmW0UozBq7LbfwNuDkTR9C0OuZjMXBaK8T3F9Vehn6k6lcl/gjcICyAtf1Ux55U0UnHan78qGJCJid7sIG+o+aGIU/o6VfprBMANM3FJ2nWfXW25ubRTFLgMud+CAUlNzgNw48kYoqkmMQsEEH9e0uNIfrEMwfYiLZ2Rscg4crCatCFmenRqpCMr" +
                "45+f0LhIIhltKzR7fqTXJoM8BppEkWnU6NWSTlMn3KcNzxEluJsEEgiQwpCbUW/Jat9bWJNl40p1mZpbvWmgN/qsZwO5Nr0HJrgkMRv3vt56fBGwlEWLCRJK9vKrOMONaNcMqyXb8Pc2qltRPTxUd6Y1tntG27W1Vl58Wc/QG6sDUncsp2WFlmE8lOm8khuz2hSWY1Fzjv" +
                "Oz2qqi/GWdyYkq/8RlObjyImqCzvrKzxTw70bWMn6LyH4PkcAKqqW4CfJOSI6stFFsFnSU52Okkb9+RAz/731CtdB1Yz90TZmM9HFyXTVjk/XKC4YhNAVaTmpf1GWcvK8pOSgQMK7vRcp3GgY1x1kEjkpllDGhWOw73Y38pTINrmm0bv8j1wXwM2+kivBOakRKc96482VV" +
                "zX4oUT1LKgkIfhyjbfKP3kLefCq8/e6MjmNZ0JECj2GjnfBwjid4bBgmWuCkF+VrPPmwVqMSm2nqwIIMqDfKfHnfQ3X9C9lXTc47If2OD9T6BNlP8flhjZdXf61td0x9SXVJvQpa3sguRue5Mqev2eVfiIaEYECJ0JTBuh645rGcozGCWL9I6bhYW3BjSoilsMpueEpqIJ" +
                "NWc2aFKVngy73OX/pOpl65Csm9bRurEQbfamZqXwSt+4Vy90S8lR4WP44CMlPxJVUjNDhzjCSNp81/IPm03SsnwokYVN4EuF+F15PqVEwUSrTRT1kN5fOGpcBm0rkBeTstmH32mdi+6/ahC045i2sNYi6PNTH/BFet2P/rGFAPKj7fn8v1T0tEhxvC7q5A4gMqUOnj1Men" +
                "zBGb4rwJhDIkZwiR8EEqEbvCrch0tfkMJRBQbCRs2r/x9jaeKJG4hraTNd+1iaeoRfqTiPgOOwZAH0T9RJQED8DxSSPneBmSeKoBsVirS2fN1E6Vt58ksL2wxW2C7WClA+iJ9toGDlQAhP4Mjoj5yhPJ9Ks+M9tyigc+yA/9L3zyl36dhYuqkPKy5z/DQ/RNJMRmBKQDSf" +
                "353sSCpHy4tt1r7pX2KEVj7lSLijK2gKoM6JuvqMRLKXOjpkxcfPqlBusFTl7uuzPo38gBfmZYHv1uMQLLCEMYvgiBG71WwNVgpuhyyBCj8NiE0YPbsoSYE7urz0K1yxQg94mnMjqltuYhcdWhE2t5r0iQ/grAvqQhfININ+X8Yks0tmqmigMAq7zMugl5Dd6HFxull/S7" +
                "Iq0+/Q/DKqMuldgBbwiIJ3zhDA0M2L8cR2EZaJRinAD8lIRfCxt8qVrao+/8hT+CNtiOY0LNavoQq/VNsQSje+uuFF+QZ8x6Suj+b3WXwPBkc3KWLol3kPN11kNiFPwf3isaOQ8eIMFI/gLz1c4T6bybVBMWDCXFGxVzCr0hh6Dsnxzt+53RQ8CYM3Ieor8p+pTO2TocB0" +
                "CQYZ94JwilI2sTCHwll2UDIALzQZuclHdk0VEHbzjzh4O2Xzrhfi4ekt+Fu+AR99Ch+EYxh9TF+wqiurwbugjmLvVL+nwAwpnowOIRpKFkgc0ngdm6bhpyEyfKhcegYrLaFgoVWzNOgQFW4FD+6jW6xJXIvwveDBuuWnzvgqqGu8B22b00O+JSF8lOPxFSY+r6618k/U14" +
                "fDVa0URqLY5UISbchCYS4BmVEYtgP0X+uw42AKoO+YxG5qO0juQPjGwmONYB+Fauuw06eDFVIsiN0GiRxDdnpavqTzHBz5LATftFn3+o8fK9uTc5MGS8nY23YLSjOZMT/VuRt9b6fe5EsWQ8IfTSK4WoBbHYeAwRZsUW/4qQYCBd4y9ArFwN+a3LkxTqbUNIOSSNAdLGaW" +
                "fiCfOHM2iu8cwx0RgJGE4xJjKv3M7/lYjr/jDnP1WF4MmNutUbZUfPhEX348QKj/cTgO5YluPuv300Rm5nTnZ+as6kCgbPqs7pG5s8olTRaBO1jV8VOQ2mgDgeVy7eO0Tnz1e939aJa5XnrKaPQjM0sJHjBW3aiao8LfLE4199VzIKS8Xhn5sKA5iV2AqVqzjJNZ969wem" +
                "kkmbHHLBLk8m0B+pTSJgETUEZuEZPWUCHQBaRH3M/9ikAbq+LKhXZsUeX6WuaqolI92DXzFO8Yww3wBhkgcBY/u2hSZW9C/P8w1+np+PUZHBLxIYvgTGfBikNsoI9Mzb/pnDeed7nIOt60UkFOiQJ5UJVcrBlJpz0g69o+Rjz7aj/fUEqfmMNr2kU9SKLhM5fwAviP9TyL" +
                "g+M2OnOlWQAP3RV4pn/vsbAfOu6rpLYfcnuymcld7vRw/rQt3KXJ8k4X0Ysrnxw2n2uoAuq8vYEZFcBj610OxorbHg89+CMp5plmUlg4VfwXtEVBygs3bOrU2/d5VSBhEJWkUGN4QXbfcLljH5DKbQd1oEdbIlLXnBJjgecCf2ACuxp9EEApZub4lcPRTFD90xXZCKyG0x" +
                "UkyZK2wbFlvqdLb7bOsFXmjBxeKnuPEWIY1tNe4AXkpaYCqdpEv3WsQmtqrHidb5h878zCRRcRdMp/9MzItbRpgEcp/vdkXoKwY467XF2NEX4dpUphjiT6D9D5iGxT4VVGjF5B+29lft0mHeEH0GF0QuE82gjsE4G5cLPpqWkJW8ibUlKsmvTjKljJS5IuNmv2HLye4ZdF" +
                "BrB5ho5VTiqDOnKRAsSFzn63vNKd4spI8nhbrwGzw6FeYUAK0m+Kbig7j8HkqlQMWBPlbP5N7Jp4cdm7pTi4y1TwNXFYSzjlEulx24DQPY/ryUuxd6yi53dXc7g+eof4ai2ORKOS5iDzo4yLRMLOIxZfkffvTbr8Szsg8D9X/hwJ4O5zQk//3Dy4ydhIgDjwsCtccfc8so" +
                "g4cOcBBfB45WDf5ElbCD9qN2RUaNTUsvZlyPAZCCDpg3tgQGQrv7FWZyDIObUE2ksE3EQjAqeYrrYyY4OXjzGD1XKBOZVpt1TICeME8189GwZ5ARYpxAx6+r9XHT45Y9ZuDTkdZU9JFLvjfcLZ2z728pxT4zkO/dyTHAW38I9R0oZqSh21JJtrkdGJtpvUqW8NP0vUm/QL" +
                "vktFxPJF39/SDK6DvMdOQdEc5dLiidI94jYX3fH3m5aUVmGWjythqtb0s8tqWft98AbrLZ+px2IaShmV8bZhX0HROpOFybF5pExaSdEoFVhtIumN7cR3QQiQdaJGB3dFwQ8d79cOZ7cRMVu/cZ8Ce1NpICq3dxSUM2ZM8VwZpcXJUYebNzI3f8ak42s7TEI3eZy4illnGn" +
                "4yH0E+NLMpLig5p0HkF+6oGgIRQsRn/dRdH4hCHNhZTS+4tgFEABI6ItrDq7kwDGZZqEswG58Omj46r8wGFqmMxT/VjmGZotfeWI+z8tqNC9AydRFCKPb4RXJ8vOP8ooVME/6Cp62FZoiNPc+2FQlh0cENQqvVDFBL398DqAIr2ujI6u8CSg3KIHmw1naoGsd78rvyQvhg" +
                "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\",\"sign\":\"103cfa05810545a4f8a133c6654d965e\",\"encryptType\":\"true\"}";
        JSONObject result = JSONObject.parseObject(input);
        String content = result.get("encrypt").toString();
        String encryptType = result.get("encryptType").toString();

        String decrpted = DataConvertor.convertUnicode(new RSAUtil().decryptByPublicKey(puk, content));

        QucentOrderResponse qucentOrderResponse = JSONObject.parseObject(decrpted, QucentOrderResponse.class);

        DataConvertor.convertToAntQueenReport(qucentOrderResponse);


        System.out.println(decrpted);
        System.out.println(qucentOrderResponse);

    }
}
