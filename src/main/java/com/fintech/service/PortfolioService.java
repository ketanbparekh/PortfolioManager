package com.fintech.service;

import com.fintech.json.PortfolioAllocation;

public interface PortfolioService {
    
    public PortfolioAllocation getPortfolioForClient(String riskLevel);

}
