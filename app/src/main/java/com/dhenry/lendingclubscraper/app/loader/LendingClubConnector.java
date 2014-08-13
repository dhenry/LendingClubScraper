package com.dhenry.lendingclubscraper.app.loader;

import android.util.Pair;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Dave
 */
public abstract class LendingClubConnector {

    protected static final String BASE_URL = "https://www.lendingclub.com/";
    protected static final String LC_FIRSTNAME_KEY = "LC_FIRSTNAME";
    protected static final String LC_PROD_GROUP_KEY = "www.lendingclub.com-prod_lcui_grp";
    protected static final String SESSION_ID_KEY = "JSESSIONID-lcui-prod_nevada";
    protected static final String LC_EMAIL_KEY = "LC_EMAIL";

    protected static List<Header> getHeaders() {
        List<Header> headers = new ArrayList<Header>();

        headers.add(new BasicHeader("Referer", "https://www.lendingclub.com/"));
        headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"));

        return headers;
    }

    protected static List<Cookie> getLoginResponseCookies(Connection.Response loginResponse) {
        List<Cookie> cookies = new ArrayList<Cookie>();

        cookies.add(new BasicClientCookie(LC_EMAIL_KEY, loginResponse.cookie(LC_EMAIL_KEY)));
        cookies.add(new BasicClientCookie(LC_FIRSTNAME_KEY, loginResponse.cookie(LC_FIRSTNAME_KEY)));
        cookies.add(new BasicClientCookie(SESSION_ID_KEY, loginResponse.cookie(SESSION_ID_KEY)));
        cookies.add(new BasicClientCookie(LC_PROD_GROUP_KEY, loginResponse.cookie(LC_PROD_GROUP_KEY)));

        return cookies;
    }

    protected Pair<HttpResponse, List<Cookie>> doRequest(final HttpUriRequest request,
                                                         final Collection<Cookie> cookies) throws IOException {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        addCookies(cookies, httpClient);

        try {
            HttpResponse response = httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                return new Pair<HttpResponse, List<Cookie>>(response, httpClient.getCookieStore().getCookies());
            } else{
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return null;
    }

    private void addCookies(final Collection<Cookie> cookies, final DefaultHttpClient httpClient) {
        CookieStore store = new BasicCookieStore();
        for (Cookie cookie : cookies) {
            store.addCookie(cookie);
        }
        httpClient.setCookieStore(store);
    }

    protected Map<String, Cookie> getLoginResponseCookies(String email, String password) throws IOException {
        String authenticateUrl = "account/login.action";

        List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
        requestBody.add(new BasicNameValuePair("login_email", email));
        requestBody.add(new BasicNameValuePair("login_password", password));

        HttpPost httpPost = new HttpPost(BASE_URL + authenticateUrl);

        for(Header header : getHeaders()) {
            httpPost.addHeader(header);
        }
        httpPost.setEntity(new UrlEncodedFormEntity(requestBody));

        Pair<HttpResponse, List<Cookie>> response = doRequest(httpPost, Collections.<Cookie>emptyList());
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();

        if (response != null) {
            for(Cookie cookie: response.second) {
                cookieMap.put(cookie.getName(), cookie);
            }
            response.first.getEntity().getContent().close();
        }
        return cookieMap;
    }
}
