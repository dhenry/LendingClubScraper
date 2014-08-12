package com.dhenry.lendingclubscraper.app.view;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.loader.AccountDetailScraperTask;
import com.dhenry.lendingclubscraper.app.orm.model.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.orm.LendingClubResolver;
import com.dhenry.lendingclubscraper.app.util.NumberFormats;

import java.text.NumberFormat;

/**
 * Author: Dave
 */
public class AccountDetailsActivity extends ListActivity implements ScraperUser<AccountDetailsData> {

    private LendingClubResolver resolver;
    private KeyValueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        resolver = new LendingClubResolver(this);
        adapter = new KeyValueAdapter(this);
        setListAdapter(adapter);

        AccountSummaryData accountSummaryData = resolver.getAccountSummaryData();

        if (accountSummaryData == null) {
            Toast.makeText(this, "Account summary information missing. Try login again... :/", Toast.LENGTH_LONG).show();
        } else {
            new AccountDetailScraperTask(this).execute(
                    new Pair<String, String>(accountSummaryData.getUserEmail(), "C4hauc6p"));
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setEmptyView(empty);
    }

    /**
     * Indicate that details retrieval failed.
     * @param exception the exception that caused the failure
     */
    @Override
    public void onScraperFailure(Exception exception) {
        Toast.makeText(this,"Details retrieval failed:" + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Display the account details.
     * @param accountDetailsData the resulting AccountDetailsData
     */

    public void onScraperSuccess(AccountDetailsData accountDetailsData) {

        NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;
        NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;

        adapter.add(new Pair<String, String>("Weighted Average Rate",
                percentFormat.format(accountDetailsData.getWeightedAvgRate())));
        adapter.add(new Pair<String, String>("Accrued Interest",
                currencyFormat.format(accountDetailsData.getAccruedInterest())));
        adapter.add(new Pair<String, String>("Payments to Date",
                currencyFormat.format(accountDetailsData.getPaymentsToDate())));
        adapter.add(new Pair<String, String>("Principal",
                currencyFormat.format(accountDetailsData.getPrincipal())));
        adapter.add(new Pair<String, String>("Interest",
                currencyFormat.format(accountDetailsData.getInterest())));
        adapter.add(new Pair<String, String>("Late Fees Received",
                currencyFormat.format(accountDetailsData.getLateFeesReceived())));
    }
}
