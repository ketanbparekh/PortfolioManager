package com.fintech.service;

import com.fintech.json.AssetAllocation;
import com.fintech.json.PortfolioAllocation;
import com.fintech.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private static Map<Integer, PortfolioAllocation> map = new HashMap<>();

    public PortfolioAllocation getPortfolioForClient(String riskLevel) {
        Integer level = Integer.valueOf(riskLevel);
        return map.get(level);
    }

    public void reBalancePortfolio(AssetAllocation assetAllocation) {

        Map<String, Integer> underAllocation = new HashMap<>();
        Integer riskLevel = assetAllocation.getRiskLevel();
        PortfolioAllocation portfolio = map.get(riskLevel);
        Map<String, Integer> assetAllocationMap = prepareAssetMap(assetAllocation);

        Integer total = getTotal(assetAllocation);
        Integer bondAllocation = (total * portfolio.getBonds()) / 100;
        Integer foreignAllocation = (total * portfolio.getForeign()) / 100;
        Integer largeCapAllocation = (total * portfolio.getLargeCap()) / 100;
        Integer midCapAllocation = (total * portfolio.getMidCap()) / 100;
        Integer smallCapAllocation = (total * portfolio.getSmallCap()) / 100;

//        if (assetAllocation.getBonds() - bondAllocation > 0) {
//            overAllocation.put("Bonds", assetAllocation.getBonds() - bondAllocation);
//        } else 
        if (assetAllocation.getBonds() - bondAllocation < 0) {
            underAllocation.put("Bonds", Math.abs(assetAllocation.getBonds() - bondAllocation));
        }

//        if (assetAllocation.getForeign() - foreignAllocation > 0) {
//            overAllocation.put("Foreign", assetAllocation.getForeign() - foreignAllocation);
//        } else 
            
        if (assetAllocation.getForeign() - foreignAllocation < 0) {
            underAllocation.put("Foreign", Math.abs(assetAllocation.getForeign() - foreignAllocation));
        }

//        if (assetAllocation.getLargeCap() - largeCapAllocation > 0) {
//            overAllocation.put("LargeCap", assetAllocation.getLargeCap() - largeCapAllocation);
//        } else 
        if (assetAllocation.getLargeCap() - largeCapAllocation < 0) {
            underAllocation.put("LargeCap", Math.abs(assetAllocation.getLargeCap() - largeCapAllocation));
        }

//        if (assetAllocation.getMidCap() - midCapAllocation > 0) {
//            overAllocation.put("MidCap", assetAllocation.getMidCap() - midCapAllocation);
//        } else 
        if (assetAllocation.getMidCap() - midCapAllocation < 0) {
            underAllocation.put("MidCap", Math.abs(assetAllocation.getMidCap() - midCapAllocation));
        }

//        if (assetAllocation.getSmallCap() - smallCapAllocation > 0) {
//            overAllocation.put("SmallCap", assetAllocation.getSmallCap() - smallCapAllocation);
//        } else 
            
        if (assetAllocation.getSmallCap() - smallCapAllocation < 0) {
            underAllocation.put("SmallCap", Math.abs(assetAllocation.getSmallCap() - smallCapAllocation));
        }

        if (underAllocation.size() == 0) {
            // return
        }

//        for (String category : underAllocation.keySet()) {
//            Integer lowerMapValue = underAllocation.get(category);
//            Integer higherMapValue;
//            for (String key : overAllocation.keySet()) {
//                higherMapValue = overAllocation.get(key);
//                if (higherMapValue > lowerMapValue && higherMapValue - Math.abs(lowerMapValue) >= 0) {
//                    underAllocation.put(category, 0);
//                    overAllocation.put(key, overAllocation.get(key) - Math.abs(lowerMapValue));
//                    //
//                } else if (higherMapValue < lowerMapValue) {
//                    underAllocation.put(category, lowerMapValue - higherMapValue);
//                    overAllocation.put(key, 0);
//                    //
//                }
//            }
//        }
        
        for (String underAllocateCategory : underAllocation.keySet()) {
            if(!underAllocateCategory.equals("Bonds") && assetAllocationMap.get("Bonds") > bondAllocation) {
                rebalanceAllocation(underAllocation, assetAllocationMap, bondAllocation, underAllocateCategory, "Bonds");
            }
            
            if(!underAllocateCategory.equals("Foreign") && assetAllocationMap.get("Foreign") > foreignAllocation) {
                rebalanceAllocation(underAllocation, assetAllocationMap, foreignAllocation, underAllocateCategory, "Foreign");
            }
            
            if(!underAllocateCategory.equals("LargeCap") && assetAllocationMap.get("LargeCap") > largeCapAllocation) {
                rebalanceAllocation(underAllocation, assetAllocationMap, largeCapAllocation, underAllocateCategory, "LargeCap");
            }
            
            if(!underAllocateCategory.equals("MidCap") && assetAllocationMap.get("MidCap") > midCapAllocation) {
                rebalanceAllocation(underAllocation, assetAllocationMap, midCapAllocation, underAllocateCategory, "MidCap");
            }
            
            if(!underAllocateCategory.equals("SmallCap") && assetAllocationMap.get("SmallCap") > smallCapAllocation) {
                rebalanceAllocation(underAllocation, assetAllocationMap, smallCapAllocation, underAllocateCategory, "SmallCap");
            }
        }

    }

    private void rebalanceAllocation(Map<String, Integer> underAllocation, Map<String, Integer> assetAllocationMap,
            Integer categoryAllocation, String underAllocateCategory, String assetCategory) {
        
        Integer lowerMapValue = underAllocation.get(underAllocateCategory);
        Integer bondAmtDiff = assetAllocationMap.get(assetCategory) - categoryAllocation;
        
        if(bondAmtDiff.compareTo(lowerMapValue) == 0) {
            assetAllocationMap.put(assetCategory, assetAllocationMap.get(assetCategory)-bondAmtDiff);
            assetAllocationMap.put(underAllocateCategory, assetAllocationMap.get(underAllocateCategory)+bondAmtDiff);
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory)-bondAmtDiff);
        } else  if(lowerMapValue > bondAmtDiff){
            int diff = lowerMapValue - bondAmtDiff;
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory)-bondAmtDiff);
            assetAllocationMap.put(assetCategory, assetAllocationMap.get(assetCategory)-bondAmtDiff);
            assetAllocationMap.put(underAllocateCategory, assetAllocationMap.get(underAllocateCategory)+bondAmtDiff);
        } else if(bondAmtDiff > lowerMapValue) {
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory)-lowerMapValue);
            assetAllocationMap.put(assetCategory, assetAllocationMap.get(assetCategory)-lowerMapValue);
            assetAllocationMap.put(underAllocateCategory, assetAllocationMap.get(underAllocateCategory)+lowerMapValue);
        }
    }

    private Map<String, Integer> prepareAssetMap(AssetAllocation assetAllocation) {
        Map<String, Integer> assetMap = new HashMap<>();
        assetMap.put("Bonds", assetAllocation.getBonds());
        assetMap.put("Foreign", assetAllocation.getForeign());
        assetMap.put("LargeCap", assetAllocation.getLargeCap());
        assetMap.put("MidCap", assetAllocation.getMidCap());
        assetMap.put("SmallCap", assetAllocation.getSmallCap());
        return assetMap;
    }

    private Integer getTotal(AssetAllocation assertAllocation) {
        Integer total = Integer.valueOf(0);
        total += assertAllocation.getBonds();
        total += assertAllocation.getForeign();
        total += assertAllocation.getSmallCap();
        total += assertAllocation.getMidCap();
        total += assertAllocation.getLargeCap();
        return total;
    }

    @PostConstruct
    public void initPortfolioLoadData() {
        map = new JsonUtils().readFile();
    }
}
