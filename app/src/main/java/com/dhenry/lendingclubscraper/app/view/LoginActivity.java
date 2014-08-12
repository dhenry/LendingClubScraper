package com.dhenry.lendingclubscraper.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.loader.AccountSummaryScraperTask;
import com.dhenry.lendingclubscraper.app.orm.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.sql.SQLException;
import java.util.List;


/**
 * Displays inputs for entering credentials. Tries to login and scrape the resulting html data
 * on login success.
 */
public class LoginActivity extends OrmLiteBaseActivity<DatabaseHelper> implements ScraperUser<AccountSummaryData> {

    public final static String LOG_TAG = LoginActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView usernameInput = (TextView) this.findViewById(R.id.usernameInput);
        final EditText passwordInput = (EditText) this.findViewById(R.id.passwordInput);
        Button loginButton = (Button) this.findViewById(R.id.loginButton);

        AccountSummaryData accountSummaryData = getFirstUsersAccountSummary();

        if (accountSummaryData != null) {
            usernameInput.setText(accountSummaryData.getUserEmail());
            passwordInput.setText("C4hauc6p"); // TODO users table
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSummaryScraperTask accountSummaryDownloadTask = new AccountSummaryScraperTask(LoginActivity.this);
                accountSummaryDownloadTask.execute(new Pair<String, String>(
                        usernameInput.getText().toString(),
                        passwordInput.getText().toString()));
            }
        });
    }

    private AccountSummaryData getFirstUsersAccountSummary() {
        AccountSummaryData accountSummaryData = null;

        try {
            List<AccountSummaryData> accountSummaryDataList = getHelper().getDao(AccountSummaryData.class).queryForAll();
            if (accountSummaryDataList.size() > 0) {
                accountSummaryData = accountSummaryDataList.get(0);
            }
        } catch(SQLException e) {
            Log.e(LOG_TAG, "Caught SQLException => " + e.getMessage());
        }

        return accountSummaryData;
    }

    /**
     * Indicate that login failed.
     * @param exception the exception that caused the login failure
     */
    public void onScraperFailure(final Exception exception) {
        Toast.makeText(this,"Login Failed:" + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Login was successful! Cache the AccountSummaryData in the db and start {@link AccountSummaryData}
     * @param result the resulting {@link AccountSummaryData}
     */
    public void onScraperSuccess(final AccountSummaryData result) {

        // insert or update the result
        try {
            getHelper().getDao(AccountSummaryData.class).createOrUpdate(result);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Caught SQLException => " + e.getMessage());
        }

        // begin the activity
        Intent accountOverviewIntent = new Intent(this, AccountOverviewActivity.class);
        startActivity(accountOverviewIntent);
    }
}