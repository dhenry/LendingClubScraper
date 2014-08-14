package com.dhenry.lendingclubscraper.app.tasks;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.connectors.LendingClubJSoupConnector;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;

/**
 * Author: Dave
 */
public class AccountDetailScraperTask extends RemoteTask<Pair, Void, AccountDetailsData> {

    public final static String LOG_TAG = AccountDetailScraperTask.class.getCanonicalName();

    public AccountDetailScraperTask(RemoteTaskCallback<AccountDetailsData> parent) {
        super(new WeakReference<RemoteTaskCallback<AccountDetailsData>>(parent));
    }

    @Override
    protected RemoteTaskResult<AccountDetailsData> doInBackground(Pair... credentials) {
        try {
            LendingClubJSoupConnector connector = new LendingClubJSoupConnector();

            Document doc = connector.getAccountDetailsDocument(credentials[0].first.toString(), credentials[0].second.toString());

            Elements detailsTableRows = doc.select("#account-details2").select(".smallModule2Adj").select("td");

            Double weightedAvgRate = percentFormat.parse(detailsTableRows.get(0).text()).doubleValue();

            Double accruedInterest = currencyFormat.parse(detailsTableRows.get(1).text()).doubleValue();
            Double paymentsToDate = currencyFormat.parse(detailsTableRows.get(2).text()).doubleValue();
            Double principal = currencyFormat.parse(detailsTableRows.get(3).text()).doubleValue();
            Double interest = currencyFormat.parse(detailsTableRows.get(4).text()).doubleValue();
            Double lateFeesReceived = currencyFormat.parse(detailsTableRows.get(5).text()).doubleValue();

            AccountDetailsData accountDetailsData = new AccountDetailsData(weightedAvgRate, accruedInterest,
                    paymentsToDate, principal, interest, lateFeesReceived);

            return new RemoteTaskResult<AccountDetailsData>(accountDetailsData);

        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Caught IOException => " + ioException.getMessage());
            return new RemoteTaskResult<AccountDetailsData>(ioException);
        } catch (ParseException parseException) {
            Log.e(LOG_TAG, "Caught ParseException => " + parseException.getMessage());
            return new RemoteTaskResult<AccountDetailsData>(parseException);
        }
    }

}
