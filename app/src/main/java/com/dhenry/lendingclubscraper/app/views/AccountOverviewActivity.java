package com.dhenry.lendingclubscraper.app.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.persistence.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;
import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.NumberFormat;

/**
 * Author: Dave
 */
public class AccountOverviewActivity extends OrmLiteBaseListActivity<DatabaseHelper> {

    private KeyValueAdapter adapter;

    private Button accountDetailsButton;
    private Button browseNotesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_overview);

        accountDetailsButton = (Button)findViewById(R.id.account_details_button);
        browseNotesButton = (Button)findViewById(R.id.browse_notes_button);

        adapter = new KeyValueAdapter(this);
        setListAdapter(adapter);

        final UserData currentUser = getIntent().getParcelableExtra(LendingClubConstants.CURRENT_USER);

        populateAccountSummaryList(currentUser);

        accountDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountDetailsIntent = new Intent(AccountOverviewActivity.this, AccountDetailsActivity.class);
                accountDetailsIntent.putExtra(LendingClubConstants.CURRENT_USER, currentUser);
                startActivity(accountDetailsIntent);
            }
        });

        browseNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseNotesIntent = new Intent(AccountOverviewActivity.this, BrowseNotesActivity.class);
                browseNotesIntent.putExtra(LendingClubConstants.CURRENT_USER, currentUser);
                startActivity(browseNotesIntent);
            }
        });
    }

    /**
     * Grab the AccountSummaryData associated with the currently logged in user and render a list row
     * for each data point.
     *
     * @param currentUser the currently logged in user
     */
    private void populateAccountSummaryList(final UserData currentUser) {

        RuntimeExceptionDao<AccountSummaryData, String> accountSummaryDataDAO = getHelper().getRuntimeExceptionDao(AccountSummaryData.class);
        RuntimeExceptionDao<NARCalculationData, String> NARCalculationDataDAO = getHelper().getRuntimeExceptionDao(NARCalculationData.class);

        AccountSummaryData accountSummaryData = accountSummaryDataDAO.queryForId(currentUser.getUserEmail());
        NARCalculationData NARCalculationData = NARCalculationDataDAO.queryForId(currentUser.getUserEmail());

        if (accountSummaryData == null) {
            Toast.makeText(this, "No Account Summary Information available to display..", Toast.LENGTH_LONG).show();
            return;
        }

        NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;
        NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;

        adapter.add(new Pair<String, String>("Adjusted Net Annualized Return",
                percentFormat.format(NARCalculationData.getAdjustedNetAnnualizedReturn())));
        adapter.add(new Pair<String, String>("Weighted Average Rate",
                percentFormat.format(NARCalculationData.getWeightedAverageRate())));
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
