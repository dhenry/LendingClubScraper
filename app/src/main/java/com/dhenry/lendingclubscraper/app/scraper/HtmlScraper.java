package com.dhenry.lendingclubscraper.app.scraper;

import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;

import java.text.NumberFormat;

public interface HtmlScraper {

    final NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;
    final NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;

    AccountSummaryData scrapeAccountSummary(final String html, final UserData userData) throws ScraperException;

    AccountDetailsData scrapeAccountDetails(final String response, final UserData user) throws ScraperException;

    Boolean welcomeMessagePresent(final String html);
}
