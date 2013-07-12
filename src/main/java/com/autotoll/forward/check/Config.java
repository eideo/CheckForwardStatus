package com.autotoll.forward.check;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class Config {
    private static Properties configProp = new Properties();
    static {
        InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("config.properties");
        try {
            configProp.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Config() {
    }

    public static String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return configProp.getProperty(key, defaultValue);
    }
    public static int getInt(String key,int defaultValue){
        String value = getProperty(key);
        if(value == null || value.trim().length() == 0)
            return defaultValue;
        return Integer.parseInt(value);
    }
}
