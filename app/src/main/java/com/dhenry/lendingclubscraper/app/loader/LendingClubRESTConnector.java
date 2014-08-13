package com.dhenry.lendingclubscraper.app.loader;

import android.util.Pair;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LendingClubRESTConnector extends LendingClubConnector {


    public void viewNetAnnualizedReturnDetails(String email, String password) throws IOException {

        String url = "account/calculateNar.action";

        Map<String, Cookie> loginCookies = getLoginResponseCookies(email, password);

        List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
        requestBody.add(new BasicNameValuePair("login_email", email));
        requestBody.add(new BasicNameValuePair("login_password", password));

        HttpGet httpGet = new HttpGet(BASE_URL + url);

        for(Header header : getHeaders()) {
            httpGet.addHeader(header);
        }

        Pair<HttpResponse, List<Cookie>> response = doRequest(httpGet, loginCookies.values());

        // TODO: use jackson to retrieve the JSON response :D

        System.out.print(response.first);
    }

}
