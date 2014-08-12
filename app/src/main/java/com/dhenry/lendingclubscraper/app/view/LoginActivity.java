package com.dhenry.lendingclubscraper.app.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.loader.AccountSummaryScraperTask;
import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.orm.LendingClubResolver;


/**
 * Displays inputs for entering credentials. Tries to login and scrape the resulting html data
 * on login success.
 */
public class LoginActivity extends Activity implements ScraperUser<AccountSummaryData> {

    public final static String LOG_TAG = LoginActivity.class.getCanonicalName();

    private LendingClubResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        resolver = new LendingClubResolver(this);

        final TextView usernameInput = (TextView) this.findViewById(R.id.usernameInput);
        final EditText passwordInput = (EditText) this.findViewById(R.id.passwordInput);
        Button loginButton = (Button) this.findViewById(R.id.loginButton);

        AccountSummaryData accountSummaryData = resolver.getAccountSummaryData();

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

        // insert the result through Resolver to be put into ContentProvider
        try {
            resolver.deleteByUserEmail(result);
            resolver.insert(result);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Caught RemoteException => " + e.getMessage());
        }

        // begin the activity
        Intent accountOverviewIntent = new Intent(this, AccountOverviewActivity.class);
        startActivity(accountOverviewIntent);
    }
}