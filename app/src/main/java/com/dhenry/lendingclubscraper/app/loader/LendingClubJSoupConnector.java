package com.dhenry.lendingclubscraper.app.loader;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LendingClubJSoupConnector extends LendingClubConnector {

    private static Connection getConnection(String urlSuffix) {
        Connection connection = Jsoup.connect(BASE_URL + urlSuffix);
        connection.header("Referer", "https://www.lendingclub.com/");
        connection.header("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

        return connection;
    }

    public Document viewAccountDetails(String email, String password) throws IOException {

        Map<String, Cookie> cookieMap = getLoginResponseCookies(email, password);

        String url = "account/lenderAccountDetail.action";
        Connection connection = getConnection(url);

//        connection.cookie(LC_EMAIL_KEY, cookieMap.get(LC_EMAIL_KEY).getValue());
        connection.cookie(LC_FIRSTNAME_KEY, cookieMap.get(LC_FIRSTNAME_KEY).getValue());
        connection.cookie(SESSION_ID_KEY, cookieMap.get(SESSION_ID_KEY).getValue());
        connection.cookie(LC_PROD_GROUP_KEY, cookieMap.get(LC_PROD_GROUP_KEY).getValue());

        Document doc = connection.get();

        return doc;
    }

    public Document login(String email, String password) throws IOException {
        Connection.Response response = getLoginResponse(email, password);

        Document doc = response.parse();
        return doc;
    }

    private Connection.Response getLoginResponse(String email, String password) throws IOException {
        String authenticateUrl = "account/login.action";
        Connection connection = getConnection(authenticateUrl);

        Map<String, String> keyVals = new HashMap<String, String>();
        keyVals.put("login_email", email);
        keyVals.put("login_password", password);
        connection.data(keyVals);
        connection.method(Connection.Method.POST);

        return connection.execute();
    }
}
