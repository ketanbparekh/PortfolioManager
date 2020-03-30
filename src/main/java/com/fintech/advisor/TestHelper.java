package com.fintech.advisor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TestHelper {

    public static void main(String args[]) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet("http://localhost:8080/portfolio/v1/riskscore/2");
        CloseableHttpResponse httpresponse = httpclient.execute(getRequest);
        System.out.println("Response " + httpresponse.getStatusLine().toString());
    }
}
