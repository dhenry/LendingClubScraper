package com.dhenry.lendingclubscraper.app.persistence.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "narCalculation")
public class NARCalculationData implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userEmail);
        dest.writeDouble(adjustedNetAnnualizedReturn);
        dest.writeDouble(weightedAverageRate);
    }

    /**
     * Used for writing a copy of this object to a Parcel, do not manually call.
     */
    public static final Parcelable.Creator<NARCalculationData> CREATOR = new Parcelable.Creator<NARCalculationData>() {
        public NARCalculationData createFromParcel(Parcel in) {
            return new NARCalculationData(in);
        }

        public NARCalculationData[] newArray(int size) {
            return new NARCalculationData[size];
        }
    };

    /**
     * Used for writing a copy of this object to a Parcel, do not manually call.
     */
    private NARCalculationData(Parcel in) {
        userEmail = in.readString();
        adjustedNetAnnualizedReturn = in.readDouble();
        weightedAverageRate = in.readDouble();
    }
}
