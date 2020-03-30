package com.fintech.json;

public class AssetAllocation {

    private Integer riskLevel;
    private Integer transactionsToReBalance;
    private Integer bonds;
    private Integer largeCap;
    private Integer midCap;
    private Integer foreign;
    private Integer smallCap;
    
    public Integer getRiskLevel() {
        return riskLevel;
    }
    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }
    public Integer getTransactionsToReBalance() {
        return transactionsToReBalance;
    }
    public void setTransactionsToReBalance(Integer transactionsToReBalance) {
        this.transactionsToReBalance = transactionsToReBalance;
    }
    public Integer getBonds() {
        return bonds;
    }
    public void setBonds(Integer bonds) {
        this.bonds = bonds;
    }
    public Integer getLargeCap() {
        return largeCap;
    }
    public void setLargeCap(Integer largeCap) {
        this.largeCap = largeCap;
    }
    public Integer getMidCap() {
        return midCap;
    }
    public void setMidCap(Integer midCap) {
        this.midCap = midCap;
    }
    public Integer getForeign() {
        return foreign;
    }
    public void setForeign(Integer foreign) {
        this.foreign = foreign;
    }
    public Integer getSmallCap() {
        return smallCap;
    }
    public void setSmallCap(Integer smallCap) {
        this.smallCap = smallCap;
    }
}
