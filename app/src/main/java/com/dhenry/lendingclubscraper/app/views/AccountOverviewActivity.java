package com.dhenry.lendingclubscraper.app.views;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.lendingClub.LendingClubAPI;
import com.dhenry.lendingclubscraper.app.lendingClub.ResponseHandler;
import com.dhenry.lendingclubscraper.app.lendingClub.impl.LendingClubAPIClient;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;
import com.dhenry.lendingclubscraper.app.views.adapters.KeyValueAdapter;
import com.github.mttkay.memento.Memento;
import com.github.mttkay.memento.MementoCallbacks;
import com.github.mttkay.memento.Retain;

import java.text.NumberFormat;

/**
 * Author: Dave
 */
public class AccountOverviewActivity extends ListActivity implements MementoCallbacks {

    private KeyValueAdapter adapter;

    private Button accountDetailsButton;
    private Button browseNotesButton;

    @Retain AccountSummaryResponseHandler accountSummaryResponseHandler;
    @Retain NetAnnualizedReturnHandler narCalculationResponseHandler;
    @Retain UserData currentUser;

    @Override
    public void onLaunch() {
        currentUser = getIntent().getParcelableExtra(LendingClubConstants.CURRENT_USER);

        LendingClubAPI lendingClubAPI = new LendingClubAPIClient(this);

        accountSummaryResponseHandler = new AccountSummaryResponseHandler();
        narCalculationResponseHandler = new NetAnnualizedReturnHandler();

        lendingClubAPI.getAccountSummary(currentUser, accountSummaryResponseHandler);
        lendingClubAPI.getNetAnnualizedReturnData(currentUser, narCalculationResponseHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_overview);

        accountDetailsButton = (Button)findViewById(R.id.account_details_button);
        browseNotesButton = (Button)findViewById(R.id.browse_notes_button);

        adapter = new KeyValueAdapter(this);
        setListAdapter(adapter);

        Memento.retain(this);   // retrieve or cache reusable information

        addAccountSummaryDataToAdapter(accountSummaryResponseHandler.getResult());
        addNARCalculationDataToAdapter(narCalculationResponseHandler.getResult());

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

    class NetAnnualizedReturnHandler implements ResponseHandler<NARCalculationData> {

        private NARCalculationData result;

        @Override
        public void onTaskError(Exception exception) {
            Toast.makeText(AccountOverviewActivity.this,"Net annualized return retrieval failed: "
                    + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(NARCalculationData result) {
            this.result = result;
            addNARCalculationDataToAdapter(result);
        }

        @Override
        public NARCalculationData getResult() {
            return result;
        }
    }

    private void addNARCalculationDataToAdapter(NARCalculationData narCalculationData) {

        if (narCalculationData == null) return;

        NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;

        adapter.add(new Pair<String, String>("Adjusted Net Annualized Return",
                percentFormat.format(narCalculationData.getAdjustedNetAnnualizedReturn())));
        adapter.add(new Pair<String, String>("Weighted Average Rate",
                percentFormat.format(narCalculationData.getWeightedAverageRate())));
    }

    class AccountSummaryResponseHandler implements ResponseHandler<AccountSummaryData> {

        private AccountSummaryData result;

        @Override
        public void onTaskError(Exception exception) {
            Toast.makeText(AccountOverviewActivity.this,"Account summary retrieval failed: "
                    + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(AccountSummaryData result) {
            this.result = result;
            addAccountSummaryDataToAdapter(result);
        }

        @Override
        public AccountSummaryData getResult() {
            return result;
        }
    }

    private void addAccountSummaryDataToAdapter(AccountSummaryData accountSummaryData) {

        if (accountSummaryData == null) return;

        NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;

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

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(android.R.id.list);
        list.setEmptyView(empty);
    }
}
