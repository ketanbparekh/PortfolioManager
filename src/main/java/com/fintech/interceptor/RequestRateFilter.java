package com.fintech.interceptor;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class RequestRateFilter implements Filter {

    private int MAX_REQUESTS_PER_SECOND = 2;
    
    private LoadingCache<String, Integer> requestCounts = null;
    
    public RequestRateFilter() {
        requestCounts = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
//        String clientIpAddress = getClientIP((HttpServletRequest) request);
//        if(maxRequestsExceeded(clientIpAddress)) {
//            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            httpResponse.getWriter().write("Too many requests");
//            return;
//        }
        chain.doFilter(request, response);
    }
    
    private Boolean maxRequestsExceeded(String clientIpAddress) {
        
        int requests = 0;
        try {
            requests = requestCounts.get(clientIpAddress);
            if(requests > MAX_REQUESTS_PER_SECOND) {
                requestCounts.put(clientIpAddress , requests);
                return true;
            }
        }catch(ExecutionException ee) {
            requests=0;
        }
        requests++;
        requestCounts.put(clientIpAddress, requests);
        return false;
    }
    
    public String getClientIP(HttpServletRequest request) {
        String headerValue = request.getHeader("X-Forwarded-For");
        if(headerValue == null)
            request.getRemoteAddr();
        return headerValue.split(",")[0];
    }

}
