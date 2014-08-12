package com.dhenry.lendingclubscraper.app.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.orm.LendingClubResolver;
import com.dhenry.lendingclubscraper.app.util.NumberFormats;

import java.text.NumberFormat;

/**
 * Author: Dave
 */
public class AccountOverviewActivity extends ListActivity {

    private LendingClubResolver resolver;
    private KeyValueAdapter adapter;

    private Button accountDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_overview);

        accountDetailsButton = (Button)findViewById(R.id.account_details_button);

        resolver = new LendingClubResolver(this);

        adapter = new KeyValueAdapter(this);
        setListAdapter(adapter);

        populateAccountSummaryList();

        accountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountDetailsIntent = new Intent(AccountOverviewActivity.this, AccountDetailsActivity.class);
                startActivity(accountDetailsIntent);
            }
        });
    }

    /**
     * Grab the AccountSummaryData from the ContentResolver and turn each field into a row in the
     * listView.
     */
    private void populateAccountSummaryList() {
        AccountSummaryData accountSummaryData = resolver.getAccountSummaryData();

        if (accountSummaryData == null) {
            Toast.makeText(this, "No Account Summary Information available to display..", Toast.LENGTH_LONG).show();
            return;
        }

        NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;
        NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;

        adapter.add(new Pair<String, String>("Adjusted Net Annualized Return",
                percentFormat.format(accountSummaryData.getAdjustedNetAnnualizedReturn())));
        adapter.add(new Pair<String, String>("Net Annualized Return",
                percentFormat.format(accountSummaryData.getNetAnnualizedReturn())));
        adapter.add(new Pair<String, String>("Total Payments",
                currencyFormat.format(accountSummaryData.getTotalPayments())));
        adapter.add(new Pair<String, String>("Account Value",
                currencyFormat.format(accountSummaryData.getAccountValue())));
        adapter.add(new Pair<String, String>("Outstanding Principle",
                currencyFormat.format(accountSummaryData.getOutstandingPrinciple())));
        adapter.add(new Pair<String, String>("Available Cash",
                currencyFormat.format(accountSummaryData.getAvailableCash())));
        adapter.add(new Pair<String, String>("In Funding Notes",
                currencyFormat.format(accountSummaryData.getInFundingNotes())));
        adapter.add(new Pair<String, String>("Adjusted Account Value",
                currencyFormat.format(accountSummaryData.getAdjustedAccountValues())));
        adapter.add(new Pair<String, String>("Interest Received",
                currencyFormat.format(accountSummaryData.getInterestReceived())));
        adapter.add(new Pair<String, String>("Adjustment for Past-Due Notes",
                currencyFormat.format(accountSummaryData.getPastDueNotesAdjustment())));
    }
}
