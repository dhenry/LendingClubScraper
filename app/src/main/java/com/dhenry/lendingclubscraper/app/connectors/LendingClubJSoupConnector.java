package com.dhenry.lendingclubscraper.app.connectors;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LendingClubJSoupConnector extends LendingClubConnector {

    private static Connection getConnection(String urlSuffix) {
        Connection connection = Jsoup.connect(BASE_URL + urlSuffix);

        for(Header header : getHeaders()) {
            connection.header(header.getName(), header.getValue());
        }

        return connection;
    }

    public Document getAccountDetailsDocument(String email, String password) throws IOException {

        Map<String, Cookie> cookieMap = getLoginResponseCookies(email, password);

        Connection connection = getConnection(ACCOUNT_DETAIL_URI_SUFFIX);

        connection.cookie(LC_FIRSTNAME_KEY, cookieMap.get(LC_FIRSTNAME_KEY).getValue());
        connection.cookie(SESSION_ID_KEY, cookieMap.get(SESSION_ID_KEY).getValue());
        connection.cookie(LC_PROD_GROUP_KEY, cookieMap.get(LC_PROD_GROUP_KEY).getValue());

        Document doc = connection.get();

        return doc;
    }

    public Document getAccountSummaryDocument(String email, String password) throws IOException {
        Connection connection = getConnection(LOGIN_URI_SUFFIX);

        Map<String, String> keyVals = new HashMap<String, String>();
        keyVals.put("login_email", email);
        keyVals.put("login_password", password);
        connection.data(keyVals);
        connection.method(Connection.Method.POST);

        return connection.execute().parse();
    }
}
