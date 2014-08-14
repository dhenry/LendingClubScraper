package com.dhenry.lendingclubscraper.app.connectors;

import android.util.Pair;

import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LendingClubRESTConnector extends LendingClubConnector {

    public NARCalculationData viewNetAnnualizedReturnDetails(String email, String password)
            throws IOException, JSONException, LendingClubException {

        Map<String, Cookie> loginCookies = getLoginResponseCookies(email, password);

        List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
        requestBody.add(new BasicNameValuePair("login_email", email));
        requestBody.add(new BasicNameValuePair("login_password", password));

        HttpGet httpGet = new HttpGet(BASE_URL + CALCULATE_NAR_URI_SUFFIX);

        for(Header header : getHeaders()) {
            httpGet.addHeader(header);
        }

        Pair<HttpResponse, List<Cookie>> response = doRequest(httpGet, loginCookies.values());

        if (response != null) {

            JSONObject jsonObject = getJSONResponseContent(response.first);

            JSONObject model = jsonObject.getJSONObject("model");

            Double adjustedNetAnnualizedReturn = model.getDouble("lcAdjustedCombinedNar");
            Double weightedAverageRate = model.getDouble("wtdAvgIntRate");

            NARCalculationData narCalculationData = new NARCalculationData(email, adjustedNetAnnualizedReturn, weightedAverageRate);

            return narCalculationData;
        }

        throw new LendingClubException("OMG no data");
    }

    private JSONObject getJSONResponseContent(HttpResponse response) throws IOException, JSONException {

        // json is UTF-8 by default
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
        StringBuilder content = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
        {
            content.append(line).append("\n");
        }

        JSONObject json = new JSONObject(content.toString());

        return json;
    }

}
