package com.dhenry.lendingclubscraper.app.views;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.lendingClub.LendingClubAPI;
import com.dhenry.lendingclubscraper.app.lendingClub.ResponseHandler;
import com.dhenry.lendingclubscraper.app.lendingClub.impl.LendingClubAPIClient;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;
import com.dhenry.lendingclubscraper.app.views.adapters.KeyValueAdapter;

import java.text.NumberFormat;

/**
 * Author: Dave
 */
public class AccountDetailsActivity extends ListActivity {

    private KeyValueAdapter<String, String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        adapter = new KeyValueAdapter<String, String>(this);
        setListAdapter(adapter);

        UserData user = getIntent().getParcelableExtra(LendingClubConstants.CURRENT_USER);

        if (user == null) {
            Toast.makeText(this, "User information missing. Try getAccountSummaryDocument again... :/", Toast.LENGTH_LONG).show();
        } else {
            new LendingClubAPIClient(this).getAccountDetails(user, new AccountDetailsResponseHandler());
        }
    }

    private class AccountDetailsResponseHandler implements ResponseHandler<AccountDetailsData> {

        @Override
        public void onTaskError(Exception exception) {
            Toast.makeText(AccountDetailsActivity.this, "Details retrieval failed:"
                    + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(AccountDetailsData accountDetailsData) {
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

        @Override
        public AccountDetailsData getResult() {
            return null; // TODO
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setEmptyView(empty);
    }
}
