package com.dhenry.lendingclubscraper.app.persistence.models;

public class CheckInOrderResult {

    private String message;
    private String result;
    private RiskSummaryData riskSummary;
    private Double current_available_balance;

    public CheckInOrderResult() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public RiskSummaryData getRiskSummary() {
        return riskSummary;
    }

    public void setRiskSummary(RiskSummaryData riskSummary) {
        this.riskSummary = riskSummary;
    }

    public Double getCurrent_available_balance() {
        return current_available_balance;
    }

    public void setCurrent_available_balance(Double current_available_balance) {
        this.current_available_balance = current_available_balance;
    }
}
