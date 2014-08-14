package com.dhenry.lendingclubscraper.app.persistence.models;

/**
 * Author: Dave
 */
public class AccountDetailsData {

    private double weightedAvgRate;
    private double accruedInterest;
    private double paymentsToDate;
    private double principal;
    private double interest;
    private double lateFeesReceived;


    public AccountDetailsData(double weightedAvgRate, double accruedInterest, double paymentsToDate,
                              double principal, double interest, double lateFeesReceived) {

        this.weightedAvgRate = weightedAvgRate;
        this.accruedInterest = accruedInterest;
        this.paymentsToDate = paymentsToDate;
        this.principal = principal;
        this.interest = interest;
        this.lateFeesReceived = lateFeesReceived;
    }

    public double getWeightedAvgRate() {
        return weightedAvgRate;
    }

    public double getAccruedInterest() {
        return accruedInterest;
    }

    public double getPaymentsToDate() {
        return paymentsToDate;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getInterest() {
        return interest;
    }

    public double getLateFeesReceived() {
        return lateFeesReceived;
    }
}
