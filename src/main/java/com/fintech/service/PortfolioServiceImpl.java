package com.fintech.service;

import com.fintech.controller.PortfolioController;
import com.fintech.json.AssetAllocation;
import com.fintech.json.PortfolioAllocation;
import com.fintech.utils.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import static com.fintech.utils.PortfolioConstants.*;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    
    protected final static Logger LOGGER = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private static Map<Integer, PortfolioAllocation> map = new HashMap<>();

    public PortfolioAllocation getPortfolioForClient(String riskLevel) {
        Integer level = Integer.valueOf(riskLevel);
        return map.get(level);
    }

    public AssetAllocation reBalancePortfolio(AssetAllocation assetAllocation) {

        Map<String, Integer> underAllocationMap = new HashMap<>();
        Map<String, Integer> portfolioMap = new HashMap<String, Integer>();

        Integer riskLevel = assetAllocation.getRiskLevel();
        PortfolioAllocation portfolio = map.get(riskLevel);
        Map<String, Integer> assetAllocationMap = prepareAssetMap(assetAllocation);

        Integer total = getTotal(assetAllocation);
        Integer bondAllocation = (total * portfolio.getBonds()) / 100;
        Integer foreignAllocation = (total * portfolio.getForeign()) / 100;
        Integer largeCapAllocation = (total * portfolio.getLargeCap()) / 100;
        Integer midCapAllocation = (total * portfolio.getMidCap()) / 100;
        Integer smallCapAllocation = (total * portfolio.getSmallCap()) / 100;
        
        preparePortfolioMap(portfolioMap, bondAllocation, foreignAllocation, largeCapAllocation, midCapAllocation,
                smallCapAllocation);

        if (assetAllocation.getBonds() - bondAllocation < 0) {
            underAllocationMap.put(BONDS, Math.abs(assetAllocation.getBonds() - bondAllocation));
        }

        if (assetAllocation.getForeign() - foreignAllocation < 0) {
            underAllocationMap.put(FOREIGN, Math.abs(assetAllocation.getForeign() - foreignAllocation));
        }

        if (assetAllocation.getLargeCap() - largeCapAllocation < 0) {
            underAllocationMap.put(LARGECAP, Math.abs(assetAllocation.getLargeCap() - largeCapAllocation));
        }

        if (assetAllocation.getMidCap() - midCapAllocation < 0) {
            underAllocationMap.put(MIDCAP, Math.abs(assetAllocation.getMidCap() - midCapAllocation));
        }

        if (assetAllocation.getSmallCap() - smallCapAllocation < 0) {
            underAllocationMap.put(SMALLCAP, Math.abs(assetAllocation.getSmallCap() - smallCapAllocation));
        }

        if (underAllocationMap.size() == 0) { //no rebalance required.
             return assetAllocation;
        }

        for (String underAllocateCategory : underAllocationMap.keySet()) {
            for(String categoryKey: portfolioMap.keySet()) {
                Integer categoryAllocation = portfolioMap.get(categoryKey);
                if (!underAllocateCategory.equals(categoryKey) && assetAllocationMap.get(categoryKey) > categoryAllocation) {
                    rebalanceAllocation(underAllocationMap, assetAllocationMap, categoryAllocation, underAllocateCategory,
                            categoryKey, assetAllocation);
                }
            }
        }

        updateAssetAllocation(assetAllocation, assetAllocationMap);
        return assetAllocation;

    }

    private void preparePortfolioMap(Map<String, Integer> portfolioMap, Integer bondAllocation,
            Integer foreignAllocation, Integer largeCapAllocation, Integer midCapAllocation,
            Integer smallCapAllocation) {
        portfolioMap.put(BONDS, bondAllocation);
        portfolioMap.put(FOREIGN, foreignAllocation);
        portfolioMap.put(LARGECAP, largeCapAllocation);
        portfolioMap.put(MIDCAP, midCapAllocation);
        portfolioMap.put(SMALLCAP, smallCapAllocation);
    }

    private void updateAssetAllocation(AssetAllocation assetAllocation, Map<String, Integer> assetAllocationMap) {
        assetAllocation.setBonds(assetAllocationMap.get(BONDS));
        assetAllocation.setForeign(assetAllocationMap.get(FOREIGN));
        assetAllocation.setLargeCap(assetAllocationMap.get(LARGECAP));
        assetAllocation.setMidCap(assetAllocationMap.get(MIDCAP));
        assetAllocation.setSmallCap(assetAllocationMap.get(SMALLCAP));
    }

    private void rebalanceAllocation(Map<String, Integer> underAllocation, Map<String, Integer> assetAllocationMap,
            Integer currentCategoryAllocation, String underAllocateCategory, String currentAssetCategory, AssetAllocation assetAllocation) {

        Integer lowerMapValue = underAllocation.get(underAllocateCategory);
        Integer assetAmtDiff = assetAllocationMap.get(currentAssetCategory) - currentCategoryAllocation;

        if (assetAmtDiff.compareTo(lowerMapValue) == 0) {
            assetAllocationMap.put(currentAssetCategory, assetAllocationMap.get(currentAssetCategory) - assetAmtDiff);
            assetAllocationMap.put(underAllocateCategory, assetAllocationMap.get(underAllocateCategory) + assetAmtDiff);
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory) - assetAmtDiff);
            assetAllocation.setTransactionsToReBalance(assetAllocation.getTransactionsToReBalance()+1);
;
        } else if (lowerMapValue > assetAmtDiff) {
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory) - assetAmtDiff);
            assetAllocationMap.put(currentAssetCategory, assetAllocationMap.get(currentAssetCategory) - assetAmtDiff);
            assetAllocationMap.put(underAllocateCategory, assetAllocationMap.get(underAllocateCategory) + assetAmtDiff);
            assetAllocation.setTransactionsToReBalance(assetAllocation.getTransactionsToReBalance()+1);
        } else if (assetAmtDiff > lowerMapValue) {
            underAllocation.put(underAllocateCategory, underAllocation.get(underAllocateCategory) - lowerMapValue);
            assetAllocationMap.put(currentAssetCategory, assetAllocationMap.get(currentAssetCategory) - lowerMapValue);
            assetAllocationMap.put(underAllocateCategory,
                    assetAllocationMap.get(underAllocateCategory) + lowerMapValue);
            assetAllocation.setTransactionsToReBalance(assetAllocation.getTransactionsToReBalance()+1);
        }
    }

    private Map<String, Integer> prepareAssetMap(AssetAllocation assetAllocation) {
        Map<String, Integer> assetMap = new HashMap<>();
        assetMap.put(BONDS, assetAllocation.getBonds());
        assetMap.put(FOREIGN, assetAllocation.getForeign());
        assetMap.put(LARGECAP, assetAllocation.getLargeCap());
        assetMap.put(MIDCAP, assetAllocation.getMidCap());
        assetMap.put(SMALLCAP, assetAllocation.getSmallCap());
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
