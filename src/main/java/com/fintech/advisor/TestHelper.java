package com.fintech.advisor;

import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.util.concurrent.RateLimiter;

public class TestHelper {

    public static void main(String args[]) throws Exception {
        //call in loop
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet getRequest = new HttpGet("http://localhost:8080/portfolio/v1/riskscore/2");
//        CloseableHttpResponse httpresponse = httpclient.execute(getRequest);
//        System.out.println("Response " + httpresponse.getStatusLine().toString());
        
        //Test RateLimiter to allow max two requests per second.
        testRateLimiter();
    }
    
   public static void testRateLimiter() {
        RateLimiter limiter = RateLimiter.create(2.0);
        for (int i = 0; i < 10; i++) {
            performOperation(limiter);
        }
    }
    
    private static void performOperation(RateLimiter limiter) {
        limiter.acquire();
        System.out.println(new Date() + ": Beep");
    }
}
