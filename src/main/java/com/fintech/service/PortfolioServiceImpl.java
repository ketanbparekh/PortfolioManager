package com.fintech.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fintech.json.PortfolioAllocation;
import com.fintech.utils.JsonUtils;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private static Map<Integer, PortfolioAllocation> map = new HashMap<>();
    
    static {
        PortfolioAllocation allocation = new PortfolioAllocation();
        allocation.setBonds(80);
        allocation.setForeign(20);
//        allocation.setLargeCap(largeCap);
//        allocation.setMidCap(midCap);
//        allocation.setSmallCap(smallCap);
        allocation.setRiskLevel(Integer.valueOf(1));
        map.put(allocation.getRiskLevel(), allocation);//imm
    }
     
    public PortfolioAllocation getPortfolioForClient(String riskLevel) {
        Integer level = Integer.valueOf(riskLevel);
        return map.get(level);
    }
    
    @PostConstruct
    public void initPortfolioLoadData() {
        map = new JsonUtils().readFile(); 
    }
}
