package com.dhenry.lendingclubscraper.app.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dhenry.lendingclubscraper.app.R;
import com.dhenry.lendingclubscraper.app.consts.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.loader.AccountSummaryScraperTask;
import com.dhenry.lendingclubscraper.app.orm.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.orm.model.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.orm.model.UserData;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Displays inputs for entering credentials. Tries to login and scrape the resulting html data
 * on login success.
 */
public class LoginActivity extends OrmLiteBaseActivity<DatabaseHelper> implements ScraperUser<AccountSummaryData> {

    public final static String LOG_TAG = LoginActivity.class.getCanonicalName();

    private RuntimeExceptionDao<UserData, String> userDao = null;

    private AutoCompleteTextView usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private CheckBox saveUser;
    private ProgressBar progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = (AutoCompleteTextView) this.findViewById(R.id.usernameInput);
        passwordInput = (EditText) this.findViewById(R.id.passwordInput);
        loginButton = (Button) this.findViewById(R.id.loginButton);
        saveUser = (CheckBox) this.findViewById(R.id.saveUser);
        progressIndicator = (ProgressBar) findViewById(R.id.progressIndicator);

        userDao = getHelper().getRuntimeExceptionDao(UserData.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingIndicator(true);

                AccountSummaryScraperTask accountSummaryDownloadTask = new AccountSummaryScraperTask(LoginActivity.this);
                accountSummaryDownloadTask.execute(new Pair<String, String>(
                        usernameInput.getText().toString(),
                        passwordInput.getText().toString()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        prepareUserAutocomplete();
        passwordInput.setText("");

        usernameInput.selectAll();
        usernameInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(usernameInput.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLoadingIndicator(false);
    }

    /**
     * Set up auto complete values for the username input and add a listener for auto completing the
     * password input.
     */
    private void prepareUserAutocomplete() {

        List<UserData> users = userDao.queryForAll();

        List<String> usernames = new ArrayList<String>(users.size());

        for (UserData user: users) {
            usernames.add(user.getUserEmail());
        }

        usernameInput.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, usernames));

        usernameInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View textView, int pos, long id) {

                UserData user = userDao.queryForId( ((TextView)textView).getText().toString() );
                passwordInput.setText(user.getPassword());
            }
        });
    }

    private void showLoadingIndicator(boolean visible) {
        if (visible) {
            loginButton.setEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);
        } else {
            loginButton.setEnabled(true);
            progressIndicator.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Indicate that login failed.
     * @param exception the exception that caused the login failure
     */
    public void onScraperFailure(final Exception exception) {
        showLoadingIndicator(false);
        Toast.makeText(this,"Login Failed:" + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Login was successful! Cache the AccountSummaryData in the db and start {@link AccountSummaryData}
     * @param result the resulting {@link AccountSummaryData}
     */
    public void onScraperSuccess(final AccountSummaryData result) {
        UserData currentUser = new UserData(result.getUserEmail(), passwordInput.getText().toString());

        // insert or update the result along with the user that logged in
        try {
            getHelper().getDao(AccountSummaryData.class).createOrUpdate(result);

            // store the user details
            if (saveUser.isChecked()) {
                userDao.createOrUpdate(currentUser);
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, "Caught SQLException => " + e.getMessage());
        }

        // begin the activity
        Intent accountOverviewIntent = new Intent(this, AccountOverviewActivity.class);
        accountOverviewIntent.putExtra(LendingClubConstants.CURRENT_USER, currentUser);
        startActivity(accountOverviewIntent);
    }
}