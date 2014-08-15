package com.dhenry.lendingclubscraper.app.tasks;

import android.util.Log;
import android.util.Pair;

import com.dhenry.lendingclubscraper.app.connectors.LendingClubJSoupConnector;
import com.dhenry.lendingclubscraper.app.connectors.LendingClubRESTConnector;
import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

public class OnLoginDataRetrievalTask extends RemoteTask<Pair, Void, Pair<AccountSummaryData, NARCalculationData>> {

    public final static String LOG_TAG = OnLoginDataRetrievalTask.class.getCanonicalName();

    public OnLoginDataRetrievalTask(RemoteTaskCallback<Pair<AccountSummaryData, NARCalculationData>> parent) {
        super(new WeakReference<RemoteTaskCallback<Pair<AccountSummaryData, NARCalculationData>>>(parent));
    }

    @Override
    protected RemoteTaskResult<Pair<AccountSummaryData, NARCalculationData>> doInBackground(Pair... credentials) {

        try {
            List<NoteData> availableNotes = getAvailableNotes(0, 25);
            AccountSummaryData accountSummaryData = scrapeAccountSummaryData(credentials[0].first.toString(), credentials[0].second.toString());
            NARCalculationData narCalculationData = getNarCalculationData(credentials[0].first.toString(), credentials[0].second.toString());


            return new RemoteTaskResult<Pair<AccountSummaryData, NARCalculationData>>(
                    new Pair<AccountSummaryData, NARCalculationData>(accountSummaryData, narCalculationData));

        } catch (LendingClubException lendingClubException) {
            Log.e(LOG_TAG, "Caught lendingClubException => " + lendingClubException.getMessage());
            return new RemoteTaskResult<Pair<AccountSummaryData, NARCalculationData>>(lendingClubException);
        }
    }

    private List<NoteData> getAvailableNotes(Integer startIndex, Integer resultsPerPage) throws LendingClubException {
        try {
            LendingClubRESTConnector restConnector = new LendingClubRESTConnector();
            return restConnector.viewPaginatedAvailableNotes(startIndex, resultsPerPage);
        } catch (IOException e) {
            throw new LendingClubException(e);
        } catch (JSONException e) {
            throw new LendingClubException(e);
        }
    }

    private NARCalculationData getNarCalculationData(final String userEmail,
                                                     final String password) throws LendingClubException {
        try {
            LendingClubRESTConnector restConnector = new LendingClubRESTConnector();
            return restConnector.viewNetAnnualizedReturnDetails(userEmail, password);
        } catch (IOException e) {
            throw new LendingClubException(e);
        } catch (JSONException e) {
            throw new LendingClubException(e);
        }
    }

    private AccountSummaryData scrapeAccountSummaryData(final String userEmail,
                                                        final String password) throws LendingClubException {

        String regexCurrencyValues = "[^0-9$.()]";
        String regexNonNegativeCurrencyValues = "[^0-9$.]";

        try {
            LendingClubJSoupConnector jsoupConnector = new LendingClubJSoupConnector();
            Document accountSummaryDoc = jsoupConnector.getAccountSummaryDocument(userEmail, password);

            Double adjustedAccountValue = currencyFormat.parse(accountSummaryDoc.select("#adjBottomBox2").select(":containsOwn($)").text()).doubleValue();
            Double inFundingNotes = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module3").select(":containsOwn($)").text()).doubleValue();
            Double outstandingPrinciple = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module4").select(":containsOwn($)").text()).doubleValue();
            Double accountValue = currencyFormat.parse(accountSummaryDoc.select("#account-summary-module6").select(":containsOwn($)").text()).doubleValue();
            Double pastDueNotesAdjustment = currencyFormat.parse(accountSummaryDoc.select("#adjBottomBox1").select(":containsOwn($)").text().replaceAll(regexCurrencyValues, "")).doubleValue();

            Double availableCash = currencyFormat.parse(accountSummaryDoc.select(".available-cash-link").get(0).html().replaceAll(regexCurrencyValues, "")).doubleValue();

            Elements boxValues = accountSummaryDoc.select(".box-module");

            Double totalPayments = 0d;
            Double interestReceived = 0d;

            for (Element element : boxValues) {
                if (element.html().contains("Interest Received")) {
                    interestReceived = currencyFormat.parse(element.select(":containsOwn($)").text()).doubleValue();
                    totalPayments = currencyFormat.parse(element.select("label").get(2).html().replaceAll(regexNonNegativeCurrencyValues, "")).doubleValue();
                    break;
                }
            }

            return new AccountSummaryData(userEmail, adjustedAccountValue, interestReceived, availableCash,
                    inFundingNotes, outstandingPrinciple, accountValue, pastDueNotesAdjustment, totalPayments);

        } catch (ParseException parseException) {
            throw new LendingClubException(parseException);
        } catch (IOException IOException) {
            throw new LendingClubException(IOException);
        }
    }
}