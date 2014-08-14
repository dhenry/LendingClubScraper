package com.dhenry.lendingclubscraper.app.persistence.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "narCalculation")
public class NARCalculationData {

    @DatabaseField(id = true)
    private String userEmail;

    @DatabaseField
    private double adjustedNetAnnualizedReturn;

    @DatabaseField
    private double weightedAverageRate;

    public NARCalculationData() {
    }

    public NARCalculationData(final String userEmail,
                              final double adjustedNetAnnualizedReturn,
                              final double weightedAverageRate) {

        this.userEmail = userEmail;
        this.adjustedNetAnnualizedReturn = adjustedNetAnnualizedReturn;
        this.weightedAverageRate = weightedAverageRate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getAdjustedNetAnnualizedReturn() {
        return adjustedNetAnnualizedReturn;
    }

    public double getWeightedAverageRate() {
        return weightedAverageRate;
    }
}
