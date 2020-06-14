package com.chetiwen.quanxun;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public class HttpUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static final int timeout = 120 * 1000;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();

    private static X509TrustManager x509mgr = new X509TrustManager() {

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub
			
		}

		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}

    };

    private static CloseableHttpClient getClient() {
        SSLConnectionSocketFactory sslCSF = null;
        try {
            // 信任�?��
            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            sslContext.init(null, new TrustManager[] { x509mgr }, null);
            sslCSF = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return HttpClients.custom().setSSLSocketFactory(sslCSF).build();
    }

    /**
     * 
     * @param url 请求url
     * @param json 请求参数 json报文
     */
    public static String doPost(String url, String json) {
        String res = null;
        URI uri = generateURL(url);
        HttpPost post = new HttpPost(uri);
        HttpEntity entity = null;
        try {
            post.setConfig(requestConfig);
            entity = new StringEntity(json, Charset.forName("UTF-8"));
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setHeader("Authorization","TFhEYXRhc2VhbjpzZWFu");
            res = execute(post);
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return res;
        }
        return res;
    }
    
    
    /**
     * 
     * @param url 请求url
     * @param json 请求参数 json报文
     */
    public static String doPost(String url, List<NameValuePair> params) {
        String res = null;
        URI uri = generateURL(url);
        HttpPost post = new HttpPost(uri);
//        HttpEntity entity = null;
        try {
            post.setConfig(requestConfig);
//            entity = new StringEntity(json, Charset.forName("UTF-8"));
            
            // Post请求

            // 设置参数
            post.setEntity(new UrlEncodedFormEntity( params, "UTF-8"));
            
            
//            post.setEntity(entity);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
//            post.setHeader("Authorization","TFhEYXRhc2VhbjpzZWFu");
            res = execute(post);
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return res;
        }
        return res;
    }

    /**
     * @param url 请求接口url
     * @return URI对象
     * @Description: 生成请求的URI对象
     * 
     */
    private static URI generateURL(String url) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return uri;
    }

    /**
     * @param request 请求信息
     * @Description: 执行http请求并关闭资源（HttpPost �?HttpGet）返回请求结�?     */
    public static String execute(HttpUriRequest request) {
        String responseStr = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = getClient().execute(request);
            responseStr = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return responseStr;
    }

}
