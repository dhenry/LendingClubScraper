package com.dhenry.lendingclubscraper.app.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.dhenry.lendingclubscraper.app.constants.LendingClubConstants;
import com.dhenry.lendingclubscraper.app.lendingClub.ResponseHandler;
import com.dhenry.lendingclubscraper.app.lendingClub.impl.LendingClubAPIClient;
import com.dhenry.lendingclubscraper.app.persistence.DatabaseHelper;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * App entry point.
 */
public class LoginActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    @InjectView(R.id.usernameInput) AutoCompleteTextView usernameInput;
    @InjectView(R.id.passwordInput) EditText passwordInput;
    @InjectView(R.id.loginButton) Button loginButton;
    @InjectView(R.id.saveUser) CheckBox saveUser;
    @InjectView(R.id.progressIndicator) ProgressBar progressIndicator;

    private RuntimeExceptionDao<UserData, String> userDao;

    @OnClick(R.id.loginButton) void doLogin() {
        String userEmail = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();


        if (StringUtil.isBlank(userEmail) || StringUtil.isBlank(password)) {
            Toast.makeText(LoginActivity.this,"Please enter a username and password", Toast.LENGTH_LONG).show();
            return;
        }

        showLoadingIndicator(true);

        final UserData userData = new UserData(userEmail, password);

        new LendingClubAPIClient(this).login(userData, new LoginHandler(userData));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        userDao = getHelper().getRuntimeExceptionDao(UserData.class);
    }

    private class LoginHandler implements ResponseHandler<Boolean> {

        private final UserData userData;

        public LoginHandler(UserData userData) {
            this.userData = userData;
        }

        @Override
        public void onTaskError(Exception exception) {
            showLoadingIndicator(false);
            Toast.makeText(LoginActivity.this,"Login Failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onTaskSuccess(Boolean result) {

            if (result == Boolean.FALSE) {
                showLoadingIndicator(false);
                Toast.makeText(LoginActivity.this,"Login Failed. Please ensure that your credentials are correct ",
                        Toast.LENGTH_LONG).show();

                return;
            }

            // store the user details
            if (saveUser.isChecked()) {
                userDao.createOrUpdate(userData);
            }

            showLoadingIndicator(false);

            // begin the activity
            Intent accountOverviewIntent = new Intent(LoginActivity.this, AccountOverviewActivity.class);
            accountOverviewIntent.putExtra(LendingClubConstants.CURRENT_USER, userData);
            startActivity(accountOverviewIntent);
        }

        @Override
        public Boolean getResult() {
            return null; // TODO
        }
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
}