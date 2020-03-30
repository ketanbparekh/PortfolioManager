package com.fintech.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.json.PortfolioAllocation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    public static final String DIR_PATH = "json/data.json";
    
    public Map<Integer, PortfolioAllocation> readFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = getClass().getClassLoader().getResource(DIR_PATH);
        File jsonfile = new File(resource.getFile());
        Map<Integer, PortfolioAllocation> portfolioMap = new HashMap<>();
        
        try {
            String jsonString = FileUtils.readFileToString(jsonfile, StandardCharsets.UTF_8);

            PortfolioAllocation[] address = objectMapper.readValue(jsonString, PortfolioAllocation[].class);
            for(PortfolioAllocation allocation : address) {
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
