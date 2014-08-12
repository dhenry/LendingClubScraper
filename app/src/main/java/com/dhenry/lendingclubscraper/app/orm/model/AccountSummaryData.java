package com.dhenry.lendingclubscraper.app.orm.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.dhenry.lendingclubscraper.app.orm.AccountSummaryCreator;

/**
 * Author: Dave
 */
public class AccountSummaryData implements Parcelable {

    private final long KEY_ID;
    private String userEmail; // todo create foreign key to USER table when that table exists
    private double netAnnualizedReturn;
    private double adjustedNetAnnualizedReturn;
    private double adjustedAccountValues;
    private double interestReceived;
    private double availableCash;
    private double inFundingNotes;
    private double outstandingPrinciple;
    private double accountValue;
    private double pastDueNotesAdjustment;
    private double totalPayments;


    public AccountSummaryData(String userEmail, double netAnnualizedReturn, double adjustedNetAnnualizedReturn,
                              double adjustedAccountValues, double interestReceived,
                              double availableCash, double inFundingNotes, double outstandingPrinciple,
                              double accountValue, double pastDueNotesAdjustment, double totalPayments) {
        this.KEY_ID = -1;
        this.userEmail = userEmail;
        this.netAnnualizedReturn = netAnnualizedReturn;
        this.adjustedNetAnnualizedReturn = adjustedNetAnnualizedReturn;
        this.adjustedAccountValues = adjustedAccountValues;
        this.interestReceived = interestReceived;
        this.availableCash = availableCash;
        this.inFundingNotes = inFundingNotes;
        this.outstandingPrinciple = outstandingPrinciple;
        this.accountValue = accountValue;
        this.pastDueNotesAdjustment = pastDueNotesAdjustment;
        this.totalPayments = totalPayments;
    }

    public AccountSummaryData(long KEY_ID, String userEmail, double netAnnualizedReturn, double adjustedNetAnnualizedReturn,
                              double adjustedAccountValues, double interestReceived, double availableCash,
                              double inFundingNotes, double outstandingPrinciple, double accountValue,
                              double pastDueNotesAdjustment, double totalPayments) {
        this.KEY_ID = KEY_ID;
        this.userEmail = userEmail;
        this.netAnnualizedReturn = netAnnualizedReturn;
        this.adjustedNetAnnualizedReturn = adjustedNetAnnualizedReturn;
        this.adjustedAccountValues = adjustedAccountValues;
        this.interestReceived = interestReceived;
        this.availableCash = availableCash;
        this.inFundingNotes = inFundingNotes;
        this.outstandingPrinciple = outstandingPrinciple;
        this.accountValue = accountValue;
        this.pastDueNotesAdjustment = pastDueNotesAdjustment;
        this.totalPayments = totalPayments;
    }

    public ContentValues getCV() {
        return AccountSummaryCreator.getCVfromAccountSummary(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(KEY_ID);
        dest.writeString(userEmail);
        dest.writeDouble(netAnnualizedReturn);
        dest.writeDouble(adjustedNetAnnualizedReturn);
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
        KEY_ID = in.readLong();
        userEmail = in.readString();
        netAnnualizedReturn = in.readDouble();
        adjustedNetAnnualizedReturn = in.readDouble();
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

    public double getNetAnnualizedReturn() {
        return netAnnualizedReturn;
    }

    public double getAdjustedNetAnnualizedReturn() {
        return adjustedNetAnnualizedReturn;
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
