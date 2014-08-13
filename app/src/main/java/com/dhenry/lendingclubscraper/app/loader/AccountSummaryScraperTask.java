package com.dhenry.lendingclubscraper.app.loader;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.view.ScraperCallback;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;

/**
 * Author: Dave
 */
public class AccountSummaryScraperTask extends ScraperTask<Pair, Void, AccountSummaryData> {

    public final static String LOG_TAG = AccountSummaryScraperTask.class.getCanonicalName();

    public AccountSummaryScraperTask(ScraperCallback<AccountSummaryData> parent) {
        super(new WeakReference<ScraperCallback<AccountSummaryData>>(parent));
    }

    @Override
    protected ScraperResult<AccountSummaryData> doInBackground(Pair... credentials) {
        try {

            LendingClubJSoupConnector jsoupConnector = new LendingClubJSoupConnector();
            LendingClubRESTConnector restConnector = new LendingClubRESTConnector();

            Document accountSummaryDoc = jsoupConnector.login(credentials[0].first.toString(), credentials[0].second.toString());

            restConnector.viewNetAnnualizedReturnDetails(credentials[0].first.toString(), credentials[0].second.toString());

            Double netAnnualizedReturns = 0.0;
            Double adjustedNetAnnualizedReturns = 0.0;

            try {
                netAnnualizedReturns = percentFormat.parse(accountSummaryDoc.select("#unadjFirstBox_nar").text()).doubleValue();
                adjustedNetAnnualizedReturns = percentFormat.parse(accountSummaryDoc.select("#adjFirstBox_nar").text()).doubleValue();
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Caught ParseException => " + e.getMessage());
            }

            Double adjustedAccountValue = currencyFormat.parse(accountSummaryDoc.select("#adjBottomBox2").select(":containsOwn($)").text()).doubleValue();
            Double inFundingNotes = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module3").select(":containsOwn($)").text()).doubleValue();
            Double outstandingPrinciple = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module4").select(":containsOwn($)").text()).doubleValue();
            Double accountValue = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module6").select(":containsOwn($)").text()).doubleValue();
            Double pastDueNotesAdjustment = currencyFormat.parse(accountSummaryDoc.select("#adjBottomBox1").select(":containsOwn($)").text().replaceAll("[^0-9$.]", "")).doubleValue();

            Double availableCash = currencyFormat.parse(accountSummaryDoc.select(".available-cash-link").get(0).html().replaceAll("[^0-9$.]", "")).doubleValue();

            Elements boxValues = accountSummaryDoc.select(".box-module");

            Double totalPayments = 0d;
            Double interestReceived  = 0d;

            for(Element element : boxValues) {
                if (element.html().contains("Interest Received")) {
                    interestReceived = currencyFormat.parse(element.select(":containsOwn($)").text()).doubleValue();
                    totalPayments = currencyFormat.parse(element.select("label").get(2).html().replaceAll("[^0-9$.]", "")).doubleValue();
                }
            }

            AccountSummaryData accountSummaryData = new AccountSummaryData(credentials[0].first.toString(), netAnnualizedReturns,
                    adjustedNetAnnualizedReturns, adjustedAccountValue,interestReceived ,
                    availableCash, inFundingNotes, outstandingPrinciple, accountValue,
                    pastDueNotesAdjustment, totalPayments);

            return new ScraperResult<AccountSummaryData>(accountSummaryData);

        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Caught IOException => " + ioException.getMessage());
            return new ScraperResult<AccountSummaryData>(ioException);
        } catch (ParseException parseException) {
            Log.e(LOG_TAG, "Caught ParseException => " + parseException.getMessage());
            return new ScraperResult<AccountSummaryData>(parseException);
        }
    }
}