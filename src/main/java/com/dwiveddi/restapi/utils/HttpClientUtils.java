package com.dwiveddi.restapi.utils;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dwiveddi on 2/6/2018.
 */
public class HttpClientUtils {

     public static HttpClient getHttpClient() {
        RequestConfig globalConfig =  RequestConfig.custom()
                        .setConnectTimeout(1000*10) //10 sec, connection establishment timeout
                        .setConnectionRequestTimeout(1000 * 10) //10sec , connect request time, ping response from server
                        .setSocketTimeout(6000 * 10) //60 secs, wait for the response to come from server
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

    public static HttpRequestBase getHTTPBase(String path, String httpMethod,String queryParams,Map<String,String> headers, String payload) throws UnsupportedEncodingException {
        HttpRequestBase httpRequestBase = null;
        if(null!= queryParams){
            if(queryParams.startsWith("?")){
                path = path + queryParams;
            }else {
                path = path + "?" + queryParams;
            }
        }
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
        if(null != headers) {
            httpRequestBase.setHeaders(convertHeaderMapToList(headers));
        }

        return httpRequestBase;
    }

    public static Map<String, String> convertHeadersListToMap(Header[] headers){
        Map<String, String> map = new HashMap<>();
        for (Header header : headers) {
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    public static Header[] convertHeaderMapToList(Map<String, String> map){
        Header[] headers = new Header[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry :  map.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return headers;
    }


}
