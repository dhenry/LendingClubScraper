package com.dhenry.lendingclubscraper.app.loader;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.orm.model.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.view.ScraperUser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;

/**
 * Author: Dave
 */
public class AccountDetailScraperTask extends ScraperTask<Pair, Void, AccountDetailsData> {

    public final static String LOG_TAG = AccountDetailScraperTask.class.getCanonicalName();

    public AccountDetailScraperTask(ScraperUser<AccountDetailsData> parent) {
        super(new WeakReference<ScraperUser<AccountDetailsData>>(parent));
    }

    @Override
    protected ScraperResult<AccountDetailsData> doInBackground(Pair... credentials) {
        try {
            LendingClubConnector connector = new LendingClubConnector();

            Document doc = connector.viewAccountDetails(credentials[0].first.toString(), credentials[0].second.toString());

            Elements detailsTableRows = doc.select("#account-details2").select(".smallModule2Adj").select("td");

            Double weightedAvgRate = percentFormat.parse(detailsTableRows.get(0).text()).doubleValue();

            Double accruedInterest = currencyFormat.parse(detailsTableRows.get(1).text()).doubleValue();
            Double paymentsToDate = currencyFormat.parse(detailsTableRows.get(2).text()).doubleValue();
            Double principal = currencyFormat.parse(detailsTableRows.get(3).text()).doubleValue();
            Double interest = currencyFormat.parse(detailsTableRows.get(4).text()).doubleValue();
            Double lateFeesReceived = currencyFormat.parse(detailsTableRows.get(5).text()).doubleValue();

            AccountDetailsData accountDetailsData = new AccountDetailsData(weightedAvgRate, accruedInterest,
                    paymentsToDate, principal, interest, lateFeesReceived);

            return new ScraperResult<AccountDetailsData>(accountDetailsData);

        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Caught IOException => " + ioException.getMessage());
            return new ScraperResult<AccountDetailsData>(ioException);
        } catch (ParseException parseException) {
            Log.e(LOG_TAG, "Caught ParseException => " + parseException.getMessage());
            return new ScraperResult<AccountDetailsData>(parseException);
        }
    }

}
