package com.wp.demo.psbcdemo1.demo;

/**
 * Created by wangpeng on 15-4-2.
 */
public class TokenHelper {
    private static TokenHelper instance;
    private static String TOKEN;

    private TokenHelper(){};

    public static TokenHelper getInstance() {
        if (null == instance) {
            instance = new TokenHelper();
        }
        return instance;
    }

    public void setToken(String obj) {
        TOKEN = obj;
    }

    public String getToken() {
        return TOKEN;
    }
}
