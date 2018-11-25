package com.wmlive.networklib.okhttp;


import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 认证拦截器
 */
public class AuthenticatorManager implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        System.out.println("Authenticating for response: " + response);
        System.out.println("Challenges: " + response.challenges());
        if (responseCount(response) >= 3) {
            return null;
        }

        //以下是示例
        String userName = "name";
        String authString = "authString";
        String authenticate = "user=\"" + userName + "\",response=\"" + authString + "\"";
        return response.request().newBuilder()
                .header("Authorization", authenticate)
                .build();
    }

    /**
     * 重复请求次数限制
     */
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
