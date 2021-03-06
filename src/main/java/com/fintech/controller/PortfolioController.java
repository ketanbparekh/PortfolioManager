package com.fintech.controller;

import com.fintech.exception.FintechException;
import com.fintech.json.AssetAllocation;
import com.fintech.json.PortfolioAllocation;
import com.fintech.service.PortfolioService;
import com.google.common.util.concurrent.RateLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/portfolio/v1")
public class PortfolioController {

    protected final static Logger LOGGER = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;
    
    private final double PERMITS_PER_SECOND = 2.0;
    
    RateLimiter rateLimiter = RateLimiter.create(PERMITS_PER_SECOND);

    @GetMapping(path = "/riskscore/{riskLevel}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PortfolioAllocation riskAppetite(@PathVariable String riskLevel) {
        rateLimiter.acquire();
        validateInput(riskLevel);
        return portfolioService.getPortfolioForClient(riskLevel);
    }

    @PostMapping(path = "/rebalance", produces = MediaType.APPLICATION_JSON_VALUE)
    public AssetAllocation recommendAllocation(@RequestBody AssetAllocation asset) {
        rateLimiter.acquire();
        validateAssetAllocation(asset);
        return portfolioService.reBalancePortfolio(asset);
    }

    private void validateAssetAllocation(AssetAllocation asset) {
        if (null == asset || null == asset.getBonds() || null == asset.getForeign() || null == asset.getLargeCap()
                || null == asset.getMidCap() || null == asset.getSmallCap())
            throw new FintechException("Input cannot be empty");
    }

    private void validateInput(String riskLevel) {

        if (StringUtils.isEmpty(riskLevel)) {
            throw new FintechException("Input cannot be empty");
        }

        Integer riskScore = null;
        try {
            riskScore = Integer.parseInt(riskLevel);
        } catch (NumberFormatException nfe) {
            throw nfe;
        }

        if (riskScore <= 0 || riskScore > 10) {
            throw new FintechException("Invalid Input, risk level should be between 1 and 10");
        }
    }
}
