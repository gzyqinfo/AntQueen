package com.chetiwen.util;


import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class PropertyUtil {
    private static Properties props = new Properties();

    static {
        try {
            FileInputStream propertiesFile = null;
            try {
                propertiesFile = new FileInputStream("conf/config.properties");
            } catch (FileNotFoundException fnfe) {
                propertiesFile = new FileInputStream("src/assembly/conf/config.properties");
            }

            InputStream in = new BufferedInputStream(propertiesFile);
            props.load(in);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readValue(String key) {
        String value ;
        if (key.indexOf("password") != -1 || key.indexOf("Password") != -1) {
            byte[] asBytes = Base64.getDecoder().decode(props.getProperty(key));
            try {
                value = (new String(asBytes, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("Fail to decode password for "+key);
            }
        } else {
            value = props.getProperty(key);
        }
        return value;
    }
}

