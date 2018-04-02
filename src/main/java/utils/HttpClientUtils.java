package utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dwiveddi on 2/6/2018.
 */
public class HttpClientUtils {

    public static HttpClient getHttpClient() {
        RequestConfig globalConfig =  RequestConfig.custom()
                        .setConnectTimeout(1000)
                        .setConnectionRequestTimeout(1000)
                        .setSocketTimeout(5000)
                        .setCookieSpec(CookieSpecs.DEFAULT)
                        .setRedirectsEnabled(false)
                        .build();

        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .setMaxConnTotal(500)
                .setMaxConnPerRoute(100)
                .setConnectionTimeToLive(600, TimeUnit.SECONDS)
                .evictIdleConnections(600, TimeUnit.SECONDS)
                .build();
        return httpClient;
    }

    public static HttpRequestBase getHTTPBase(String path, String httpMethod, String payload) throws UnsupportedEncodingException {
        HttpRequestBase httpRequestBase = null;
        switch (httpMethod){
            case "GET" :  httpRequestBase =  new HttpGet(path); break;
            case "POST": httpRequestBase = new HttpPost(path); break;
            case "PUT": httpRequestBase =  new HttpPut(path); break;
            case "PATCH": httpRequestBase = new HttpPatch(path); break;
            case "DELETE" : httpRequestBase =  new HttpDelete(path); break;
            default: throw new IllegalArgumentException("Unhandled httpMethod " + httpMethod);
        }
        if(httpRequestBase instanceof HttpEntityEnclosingRequestBase){
            HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpRequestBase;
            if(null != payload) {
                httpEntityEnclosingRequestBase.setEntity(new StringEntity(payload));
            }
        }
        return httpRequestBase;
    }


}
