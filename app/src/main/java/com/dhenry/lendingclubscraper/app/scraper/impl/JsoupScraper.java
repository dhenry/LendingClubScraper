package com.dhenry.lendingclubscraper.app.scraper.impl;

import com.dhenry.lendingclubscraper.app.scraper.ScraperException;
import com.dhenry.lendingclubscraper.app.scraper.HtmlScraper;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;

public class JsoupScraper implements HtmlScraper {

    @Override
    public AccountSummaryData scrapeAccountSummary(final String html, final UserData userData) throws ScraperException {

        String regexCurrencyValues = "[^0-9$.()]";
        String regexNonNegativeCurrencyValues = "[^0-9$.]";

        try {
            Document accountSummaryDoc = Jsoup.parse(html);

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

            return new AccountSummaryData(userData.getUserEmail(), adjustedAccountValue, interestReceived, availableCash,
                    inFundingNotes, outstandingPrinciple, accountValue, pastDueNotesAdjustment, totalPayments);

        } catch (ParseException parseException) {
            throw new ScraperException(parseException);
        }
    }

    @Override
    public Boolean welcomeMessagePresent(final String html) {

        Document doc = Jsoup.parse(html);
        Element welcomeMessage = doc.select("#master_utilities_welcome").first();

        return welcomeMessage != null;
    }

    @Override
    public AccountDetailsData scrapeAccountDetails(final String html, final UserData user) throws ScraperException {

        Document doc = Jsoup.parse(html);

        try {
            Elements detailsTableRows = doc.select("#account-details2").select(".smallModule2Adj").select("td");

            Double weightedAvgRate = percentFormat.parse(detailsTableRows.get(0).text()).doubleValue();

            Double accruedInterest = currencyFormat.parse(detailsTableRows.get(1).text()).doubleValue();
            Double paymentsToDate = currencyFormat.parse(detailsTableRows.get(2).text()).doubleValue();
            Double principal = currencyFormat.parse(detailsTableRows.get(3).text()).doubleValue();
            Double interest = currencyFormat.parse(detailsTableRows.get(4).text()).doubleValue();
            Double lateFeesReceived = currencyFormat.parse(detailsTableRows.get(5).text()).doubleValue();

            return new AccountDetailsData(weightedAvgRate, accruedInterest,
                    paymentsToDate, principal, interest, lateFeesReceived);

        } catch (ParseException parseException) {
            throw new ScraperException(parseException);
        }
    }
}
