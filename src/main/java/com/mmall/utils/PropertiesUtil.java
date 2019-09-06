package com.mmall.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties properties;
    static {
        String fileName="mmall.properties";
        properties=new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getProperty(String key){
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }
    public static String getProperty(String key,String defaultValue){
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)){
            value=defaultValue;
            return value.trim();
        }
        return value.trim();
    }
}
