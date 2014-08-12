package com.dhenry.lendingclubscraper.app.loader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Dave
 */
public class LendingClubConnector {

    private static final String BASE_URL = "https://www.lendingclub.com/";
    private static final String LC_FIRSTNAME_KEY = "LC_FIRSTNAME";
    private static final String LC_PROD_GROUP_KEY = "www.lendingclub.com-prod_lcui_grp";
    private static final String SESSION_ID_KEY = "JSESSIONID-lcui-prod_nevada";
    private static final String LC_EMAIL_KEY = "LC_EMAIL";

    private static Connection getConnection(String urlSuffix) {
        Connection connection = Jsoup.connect(BASE_URL + urlSuffix);
        connection.header("Referer", "https://www.lendingclub.com/");
        connection.header("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

        return connection;
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

    public Document viewAccountDetails(String email, String password) throws IOException {

        Connection.Response loginResponse = getLoginResponse(email, password);

        String url = "account/lenderAccountDetail.action";
        Connection connection = getConnection(url);

        //connection.cookie("c_OnOffCount", "0");
        //connection.cookie("s_vnum", "0");
        connection.cookie(LC_EMAIL_KEY, loginResponse.cookie(LC_EMAIL_KEY));
        connection.cookie(LC_FIRSTNAME_KEY, loginResponse.cookie(LC_FIRSTNAME_KEY));
        connection.cookie(SESSION_ID_KEY, loginResponse.cookie(SESSION_ID_KEY));
        //connection.cookie("s_cc", "true");
        //connection.cookie("s_vi", "[CS]v1|29D1DA5B050108E3-60000115C00FC548[CE]");
        //connection.cookie("OMNITURE_USER_TRACKING2", "orderedInvestor");
        connection.cookie(LC_PROD_GROUP_KEY, loginResponse.cookie(LC_PROD_GROUP_KEY));
        //connection.cookie("s_invisit", "true");
        //connection.cookie("s_sq", "lcprod%3D%2526pid%253DAccount%252520%25253A%252520My%252520%25253A%252520Summary%252520%25253A%252520Investing%2526pidt%253D1%2526oid%253Dhttps%25253A%25252F%25252Fwww.lendingclub.com%25252Faccount%25252FlenderAccountDetail.action_1%2526oidt%253D1%2526ot%253DA%2526oi%253D1");



        Document doc = connection.get();

        return doc;
    }
}
