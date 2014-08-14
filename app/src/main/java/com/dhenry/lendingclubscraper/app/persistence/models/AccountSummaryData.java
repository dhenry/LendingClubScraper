package com.dhenry.lendingclubscraper.app.persistence.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "accountSummary")
public class AccountSummaryData implements Parcelable {

    @DatabaseField(id = true)
    private String userEmail;

    @DatabaseField
    private double adjustedAccountValues;

    @DatabaseField
    private double interestReceived;

    @DatabaseField
    private double availableCash;

    @DatabaseField
    private double inFundingNotes;

    @DatabaseField
    private double outstandingPrinciple;

    @DatabaseField
    private double accountValue;

    @DatabaseField
    private double pastDueNotesAdjustment;

    @DatabaseField
    private double totalPayments;

    public AccountSummaryData() {
    }

    public AccountSummaryData(String userEmail, double adjustedAccountValues, double interestReceived,
                              double availableCash, double inFundingNotes, double outstandingPrinciple,
                              double accountValue, double pastDueNotesAdjustment, double totalPayments) {
        this.userEmail = userEmail;
        this.adjustedAccountValues = adjustedAccountValues;
        this.interestReceived = interestReceived;
        this.availableCash = availableCash;
        this.inFundingNotes = inFundingNotes;
        this.outstandingPrinciple = outstandingPrinciple;
        this.accountValue = accountValue;
        this.pastDueNotesAdjustment = pastDueNotesAdjustment;
        this.totalPayments = totalPayments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userEmail);
        dest.writeDouble(adjustedAccountValues);
        dest.writeDouble(interestReceived);
        dest.writeDouble(availableCash);
        dest.writeDouble(inFundingNotes);
        dest.writeDouble(outstandingPrinciple);
        dest.writeDouble(accountValue);
        dest.writeDouble(pastDueNotesAdjustment);
        dest.writeDouble(totalPayments);
    }


    /**
     * Used for writing a copy of this object to a Parcel, do not manually call.
     */
    public static final Parcelable.Creator<AccountSummaryData> CREATOR = new Parcelable.Creator<AccountSummaryData>() {
        public AccountSummaryData createFromParcel(Parcel in) {
            return new AccountSummaryData(in);
        }

        public AccountSummaryData[] newArray(int size) {
            return new AccountSummaryData[size];
        }
    };

    /**
     * Used for writing a copy of this object to a Parcel, do not manually call.
     */
    private AccountSummaryData(Parcel in) {
        userEmail = in.readString();
        adjustedAccountValues = in.readDouble();
        interestReceived = in.readDouble();
        availableCash = in.readDouble();
        inFundingNotes = in.readDouble();
        outstandingPrinciple = in.readDouble();
        accountValue = in.readDouble();
        pastDueNotesAdjustment = in.readDouble();
        totalPayments = in.readDouble();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public double getAdjustedAccountValues() {
        return adjustedAccountValues;
    }

    public double getInterestReceived() {
        return interestReceived;
    }

    public double getAvailableCash() {
        return availableCash;
    }

    public double getInFundingNotes() {
        return inFundingNotes;
    }

    public double getOutstandingPrinciple() {
        return outstandingPrinciple;
    }

    public double getAccountValue() {
        return accountValue;
    }

    public double getPastDueNotesAdjustment() {
        return pastDueNotesAdjustment;
    }

    public double getTotalPayments() {
        return totalPayments;
    }

    public static Creator<AccountSummaryData> getCreator() {
        return CREATOR;
    }
}
