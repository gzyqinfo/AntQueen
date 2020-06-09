package com.chetiwen.server;

import com.chetiwen.cache.*;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.rest.RestApplication;
import com.chetiwen.util.PropertyUtil;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;


public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Application stopped.")));

        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            String hostName = InetAddress.getLocalHost().getHostName();
            String canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
            logger.info("current IP address : {}/{}/{}", ipAddress, hostName, canonicalHostName);
        } catch (UnknownHostException e) {
            logger.error("Cannot get IP address.");
            e.printStackTrace();
        }

        String restHostName = "http://localhost:";
        int restPort = Integer.valueOf(PropertyUtil.readValue("app.port")).intValue();
        String restServiceRootPath = "/";
        String restUri = restHostName+String.valueOf(restPort)+restServiceRootPath;
        JettyHttpContainerFactory.createServer(URI.create(restUri), new RestApplication());
        logger.info("RESTful Server started at : " + restUri);
        logger.info("AntQueen started...");

        //Cache init

        try {
            UserCache.getInstance();
            SaveOrderCache.getInstance();
            OrderMapCache.getInstance();
            GetOrderCache.getInstance();
            BrandCache.getInstance();
            UserRateCache.getInstance();
            VinBrandCache.getInstance();
            DebitLogCache.getInstance();
        } catch (DBAccessException e) {
            e.printStackTrace();
            logger.error("Error while init system cache , {}", e.getMessage());
        }

    }
}
