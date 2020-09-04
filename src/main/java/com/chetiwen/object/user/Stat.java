package com.chetiwen.object.user;

public class Stat {
    private String partnerId;
    private String userName;

    private float balance;          //余额
    private int totalChargeCount;   //充值总次数
    private float totalChargeAmount;//充值总金额
    private float avgChargeAmount;  //平均每次充值金额
    private float maxChargeAmount;  //单笔充值最大金额
    private int totalPlaceOrder;    //总下单次数
    private int successPlaceOrder;  //下单成功数
    private int failPlaceOrder;     //下单失败数
    private int querySuccessTimes;  //查询成功数（含已计费，待计费)
    private int queryFailTimes;     //查询无数据 （含已退费）
    private String successPlaceRatio;//下单成功率
    private String querySuccessRatio;//数据返回成功率
    private String totalSuccessRatio;//总查询成功率
    private String avgQueryTime;       //出单平均时长
    private String maxQueryTime;       //最慢出单时长
    private String maxQueryBrand;   //最慢时长单的品牌
    private String maxQueryVin;     //最慢时长单的VIN
    private String minQueryTime;       //最快出单时长
    private String minQueryBrand;   //最快时长单的品牌
    private String minQueryVin;     //最快时长单的VIN
    private String avgCancelTime;      //无数据退单平均时间
    private String maxCancelTime;      //无数据退单最长时间
    private String minCancelTime;      //无数据退单最短时间
    private String mostFrequentQueryBrandName;  //最常查询品牌名称
    private int mostFrequentQueryBrandTimes;    //最常查询品牌次数
    private int specialBrandCount;     //查询特殊品牌次数

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalChargeCount() {
        return totalChargeCount;
    }

    public void setTotalChargeCount(int totalChargeCount) {
        this.totalChargeCount = totalChargeCount;
    }

    public float getTotalChargeAmount() {
        return totalChargeAmount;
    }

    public void setTotalChargeAmount(float totalChargeAmount) {
        this.totalChargeAmount = totalChargeAmount;
    }

    public float getAvgChargeAmount() {
        return avgChargeAmount;
    }

    public void setAvgChargeAmount(float avgChargeAmount) {
        this.avgChargeAmount = avgChargeAmount;
    }

    public float getMaxChargeAmount() {
        return maxChargeAmount;
    }

    public void setMaxChargeAmount(float maxChargeAmount) {
        this.maxChargeAmount = maxChargeAmount;
    }

    public int getTotalPlaceOrder() {
        return totalPlaceOrder;
    }

    public void setTotalPlaceOrder(int totalPlaceOrder) {
        this.totalPlaceOrder = totalPlaceOrder;
    }

    public int getSuccessPlaceOrder() {
        return successPlaceOrder;
    }

    public void setSuccessPlaceOrder(int successPlaceOrder) {
        this.successPlaceOrder = successPlaceOrder;
    }

    public int getFailPlaceOrder() {
        return failPlaceOrder;
    }

    public void setFailPlaceOrder(int failPlaceOrder) {
        this.failPlaceOrder = failPlaceOrder;
    }

    public int getQuerySuccessTimes() {
        return querySuccessTimes;
    }

    public void setQuerySuccessTimes(int querySuccessTimes) {
        this.querySuccessTimes = querySuccessTimes;
    }

    public int getQueryFailTimes() {
        return queryFailTimes;
    }

    public void setQueryFailTimes(int queryFailTimes) {
        this.queryFailTimes = queryFailTimes;
    }

    public String getSuccessPlaceRatio() {
        return successPlaceRatio;
    }

    public void setSuccessPlaceRatio(String successPlaceRatio) {
        this.successPlaceRatio = successPlaceRatio;
    }

    public String getQuerySuccessRatio() {
        return querySuccessRatio;
    }

    public void setQuerySuccessRatio(String querySuccessRatio) {
        this.querySuccessRatio = querySuccessRatio;
    }

    public String getTotalSuccessRatio() {
        return totalSuccessRatio;
    }

    public void setTotalSuccessRatio(String totalSuccessRatio) {
        this.totalSuccessRatio = totalSuccessRatio;
    }

    public String getAvgQueryTime() {
        return avgQueryTime;
    }

    public void setAvgQueryTime(String avgQueryTime) {
        this.avgQueryTime = avgQueryTime;
    }

    public String getMaxQueryTime() {
        return maxQueryTime;
    }

    public void setMaxQueryTime(String maxQueryTime) {
        this.maxQueryTime = maxQueryTime;
    }

    public String getMaxQueryBrand() {
        return maxQueryBrand;
    }

    public void setMaxQueryBrand(String maxQueryBrand) {
        this.maxQueryBrand = maxQueryBrand;
    }

    public String getMaxQueryVin() {
        return maxQueryVin;
    }

    public void setMaxQueryVin(String maxQueryVin) {
        this.maxQueryVin = maxQueryVin;
    }

    public String getMinQueryTime() {
        return minQueryTime;
    }

    public void setMinQueryTime(String minQueryTime) {
        this.minQueryTime = minQueryTime;
    }

    public String getMinQueryBrand() {
        return minQueryBrand;
    }

    public void setMinQueryBrand(String minQueryBrand) {
        this.minQueryBrand = minQueryBrand;
    }

    public String getMinQueryVin() {
        return minQueryVin;
    }

    public void setMinQueryVin(String minQueryVin) {
        this.minQueryVin = minQueryVin;
    }

    public String getAvgCancelTime() {
        return avgCancelTime;
    }

    public void setAvgCancelTime(String avgCancelTime) {
        this.avgCancelTime = avgCancelTime;
    }

    public String getMaxCancelTime() {
        return maxCancelTime;
    }

    public void setMaxCancelTime(String maxCancelTime) {
        this.maxCancelTime = maxCancelTime;
    }

    public String getMinCancelTime() {
        return minCancelTime;
    }

    public void setMinCancelTime(String minCancelTime) {
        this.minCancelTime = minCancelTime;
    }

    public String getMostFrequentQueryBrandName() {
        return mostFrequentQueryBrandName;
    }

    public void setMostFrequentQueryBrandName(String mostFrequentQueryBrandName) {
        this.mostFrequentQueryBrandName = mostFrequentQueryBrandName;
    }

    public int getMostFrequentQueryBrandTimes() {
        return mostFrequentQueryBrandTimes;
    }

    public void setMostFrequentQueryBrandTimes(int mostFrequentQueryBrandTimes) {
        this.mostFrequentQueryBrandTimes = mostFrequentQueryBrandTimes;
    }

    public int getSpecialBrandCount() {
        return specialBrandCount;
    }

    public void setSpecialBrandCount(int specialBrandCount) {
        this.specialBrandCount = specialBrandCount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "partnerId='" + partnerId + '\'' +
                ", userName='" + userName + '\'' +
                ", balance=" + balance +
                ", totalChargeCount=" + totalChargeCount +
                ", totalChargeAmount=" + totalChargeAmount +
                ", avgChargeAmount=" + avgChargeAmount +
                ", maxChargeAmount=" + maxChargeAmount +
                ", totalPlaceOrder=" + totalPlaceOrder +
                ", successPlaceOrder=" + successPlaceOrder +
                ", failPlaceOrder=" + failPlaceOrder +
                ", querySuccessTimes=" + querySuccessTimes +
                ", queryFailTimes=" + queryFailTimes +
                ", successPlaceRatio='" + successPlaceRatio + '\'' +
                ", querySuccessRatio='" + querySuccessRatio + '\'' +
                ", totalSuccessRatio='" + totalSuccessRatio + '\'' +
                ", avgQueryTime='" + avgQueryTime + '\'' +
                ", maxQueryTime='" + maxQueryTime + '\'' +
                ", maxQueryBrand='" + maxQueryBrand + '\'' +
                ", maxQueryVin='" + maxQueryVin + '\'' +
                ", minQueryTime='" + minQueryTime + '\'' +
                ", minQueryBrand='" + minQueryBrand + '\'' +
                ", minQueryVin='" + minQueryVin + '\'' +
                ", avgCancelTime='" + avgCancelTime + '\'' +
                ", maxCancelTime='" + maxCancelTime + '\'' +
                ", minCancelTime='" + minCancelTime + '\'' +
                ", mostFrequentQueryBrandName='" + mostFrequentQueryBrandName + '\'' +
                ", mostFrequentQueryBrandTimes=" + mostFrequentQueryBrandTimes +
                ", specialBrandCount=" + specialBrandCount +
                '}';
    }
}
