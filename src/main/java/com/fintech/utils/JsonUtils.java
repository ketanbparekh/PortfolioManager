package com.fintech.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.json.PortfolioAllocation;

public class JsonUtils {

    public Map<Integer, PortfolioAllocation> readFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = getClass().getClassLoader().getResource("json/data.json");
        File jsonfile = new File(resource.getFile());
        Map<Integer, PortfolioAllocation> portfolioMap = new HashMap<>();
        
        try {
            String jsonString = FileUtils.readFileToString(jsonfile, StandardCharsets.UTF_8);
            System.out.println(jsonString);

            PortfolioAllocation[] address = objectMapper.readValue(jsonString, PortfolioAllocation[].class);
            System.out.println(address);
            for(PortfolioAllocation allocation : address) {
                System.out.println(allocation.getRiskLevel());
                System.out.println(allocation.getMidCap());
                portfolioMap.put(allocation.getRiskLevel(),allocation);
            }
          
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return portfolioMap;
    }
    
    public static void main(String args[]) {
        
        JsonUtils utils = new JsonUtils();
        utils.readFile();
    }
}
