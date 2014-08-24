package com.dhenry.lendingclubscraper.app.connectors;

import android.util.Pair;

import com.dhenry.lendingclubscraper.app.exceptions.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class LendingClubRESTConnector extends LendingClubConnector {

    public NARCalculationData viewNetAnnualizedReturnDetails(String email, String password)
            throws IOException, JSONException, LendingClubException {

        Map<String, Cookie> loginCookies = getLoginResponseCookies(email, password);

        HttpGet httpGet = new HttpGet(BASE_URL + CALCULATE_NAR_URI_SUFFIX);

        for(Header header : getHeaders()) {
            httpGet.addHeader(header);
        }

        Pair<HttpResponse, List<Cookie>> response = doRequest(httpGet, loginCookies.values());

        if (response != null) {

            StringBuilder responseBody = getResponseBody(response.first.getEntity());

            JSONObject jsonObject = new JSONObject(responseBody.toString());

            JSONObject model = jsonObject.getJSONObject("model");

            Double adjustedNetAnnualizedReturn = model.getDouble("lcAdjustedCombinedNar");
            Double weightedAverageRate = model.getDouble("wtdAvgIntRate");

            NARCalculationData narCalculationData = new NARCalculationData(email, adjustedNetAnnualizedReturn, weightedAverageRate);

            return narCalculationData;
        }

        throw new LendingClubException("OMG no data");
    }

    public NotesPagedResult viewPaginatedAvailableNotes(int startIndex, int pagesize)
                                                                throws IOException, JSONException, LendingClubException {

        List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
        requestBody.add(new BasicNameValuePair("method", "getResultsInitial"));
        requestBody.add(new BasicNameValuePair("startindex", String.valueOf(startIndex)));
        requestBody.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));

        HttpPost httpPost = new HttpPost(BASE_URL + BROWSE_NOTES_URI_SUFFIX);

        httpPost.setEntity(new UrlEncodedFormEntity(requestBody));

        Pair<HttpResponse, List<Cookie>> response = doRequest(httpPost, Collections.<Cookie>emptyList());

        if (response != null) {

            StringBuilder responseBody = getResponseBody(response.first.getEntity());

            JSONObject jsonObject = new JSONObject(responseBody.toString());
            JSONObject searchResult = jsonObject.getJSONObject("searchresult");

            JSONArray notesJSONArray = searchResult.getJSONArray("loans");

            List<NoteData> notes = Arrays.asList(new ObjectMapper().readValue(notesJSONArray.toString(), NoteData[].class));

            int totalRecords = searchResult.getInt("totalRecords");

            return new NotesPagedResult(notes, totalRecords);
        }

        throw new LendingClubException("OMG no data");

    }

    private StringBuilder getResponseBody(final HttpEntity responseEntity) throws IOException {

        InputStream instream = responseEntity.getContent();
        Header contentEncoding = responseEntity.getContentEncoding();
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream);
        }

        // json is UTF-8 by default
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"), 8);
        StringBuilder content = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
        {
            content.append(line).append("\n");
        }
        return content;
    }

}
