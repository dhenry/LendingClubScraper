package com.dhenry.lendingclubscraper.app.lendingClub.impl;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dhenry.lendingclubscraper.app.lendingClub.LendingClubAPI;
import com.dhenry.lendingclubscraper.app.lendingClub.LendingClubException;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountDetailsData;
import com.dhenry.lendingclubscraper.app.persistence.models.AccountSummaryData;
import com.dhenry.lendingclubscraper.app.persistence.models.CheckInOrderResult;
import com.dhenry.lendingclubscraper.app.persistence.models.NARCalculationData;
import com.dhenry.lendingclubscraper.app.persistence.models.NoteData;
import com.dhenry.lendingclubscraper.app.persistence.models.NotesPagedResult;
import com.dhenry.lendingclubscraper.app.persistence.models.UserData;
import com.dhenry.lendingclubscraper.app.scraper.HtmlScraper;
import com.dhenry.lendingclubscraper.app.scraper.ScraperException;
import com.dhenry.lendingclubscraper.app.scraper.impl.JsoupScraper;
import com.dhenry.lendingclubscraper.app.lendingClub.ResponseHandler;
import com.dhenry.lendingclubscraper.app.volley.GZipRequest;
import com.dhenry.lendingclubscraper.app.volley.VolleyRequestManager;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LendingClubAPIClient implements LendingClubAPI {

    protected static final String BASE_URI = "https://www.lendingclub.com/";

    protected static final String LOGIN_SUFFIX = "account/login.action";
    protected static final String ACCOUNT_SUMMARY_SUFFIX = "account/summary.action";
    protected static final String ACCOUNT_DETAIL_SUFFIX = "account/lenderAccountDetail.action";
    protected static final String CALCULATE_NAR_SUFFIX = "account/calculateNar.action";
    protected static final String BROWSE_NOTES_SUFFIX = "browse/browseNotesAj.action?";
    protected static final String ADD_NOTE_TO_ORDER_SUFFIX = "browse/updateLSRAj.action";
    protected static final String CHECK_IN_ORDER_SUFFIX = "data/portfolio?method=addToPortfolioNew";

    private Context appContext;

    public LendingClubAPIClient(Context context) {
        appContext = context.getApplicationContext();
    }

    @Override
    public void login(final UserData userData, final ResponseHandler<Boolean> handler) {
        final String url = BASE_URI + LOGIN_SUFFIX;

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String html) {

                // TODO: this isn't a very good way of checking whether the login succeeded
                handler.onTaskSuccess(new JsoupScraper().welcomeMessagePresent(html));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("login_email", userData.getUserEmail());
                params.put("login_password", userData.getPassword());

                return params;
            }
        };

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    @Override
    public void getAccountSummary(final UserData userData, final ResponseHandler<AccountSummaryData> handler) {

        final String url = BASE_URI + ACCOUNT_SUMMARY_SUFFIX;

        Request request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    HtmlScraper scraper = new JsoupScraper();

                    AccountSummaryData accountSummaryData = scraper.scrapeAccountSummary(response, userData);

                    handler.onTaskSuccess(accountSummaryData);

                } catch (ScraperException e) {
                    handler.onTaskError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        });

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    @Override
    public void getNetAnnualizedReturnData(final UserData userData, final ResponseHandler<NARCalculationData> handler) {
        final String url = BASE_URI + CALCULATE_NAR_SUFFIX;

        Request request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject model = response.getJSONObject("model");

                    Double adjustedNetAnnualizedReturn = model.getDouble("lcAdjustedCombinedNar");
                    Double weightedAverageRate = model.getDouble("wtdAvgIntRate");

                    NARCalculationData narCalculationData =
                            new NARCalculationData(userData.getUserEmail(), adjustedNetAnnualizedReturn, weightedAverageRate);

                    handler.onTaskSuccess(narCalculationData);

                } catch (JSONException e) {
                    handler.onTaskError(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        });

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    @Override
    public void getAccountDetails(final UserData user, final ResponseHandler<AccountDetailsData> handler) {
        final String url = BASE_URI + ACCOUNT_DETAIL_SUFFIX;

        Request request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    HtmlScraper scraper = new JsoupScraper();

                    AccountDetailsData accountDetailsData = scraper.scrapeAccountDetails(response, user);

                    handler.onTaskSuccess(accountDetailsData);

                } catch (ScraperException e) {
                    handler.onTaskError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        });

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    @Override
    public void getAvailableNotes(final int startIndex, final int numItemsPage, final ResponseHandler<NotesPagedResult> handler) {

        final String url = BASE_URI + BROWSE_NOTES_SUFFIX;

        Request request = new GZipRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String html) {

                try {
                    JSONObject jsonObject = new JSONObject(html);
                    JSONObject searchResult = jsonObject.getJSONObject("searchresult");

                    JSONArray notesJSONArray = searchResult.getJSONArray("loans");

                    List<NoteData> notes = Arrays.asList(new ObjectMapper().readValue(notesJSONArray.toString(), NoteData[].class));

                    int totalRecords = searchResult.getInt("totalRecords");

                    handler.onTaskSuccess(new NotesPagedResult(notes, totalRecords, startIndex, startIndex + numItemsPage));

                } catch (JSONException e) {
                    handler.onTaskError(e);
                } catch (JsonMappingException e) {
                    handler.onTaskError(e);
                } catch (JsonParseException e) {
                    handler.onTaskError(e);
                } catch (IOException e) {
                    handler.onTaskError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("method", "getResultsInitial");
                params.put("startIndex", String.valueOf(startIndex));
                params.put("pagesize", String.valueOf(numItemsPage));

                return params;
            }
        };

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    @Override
    public void addNotesToOrder(UserData currentUser, Collection<NoteData> notes, final ResponseHandler<CheckInOrderResult> handler) {

        final AtomicInteger requestsPending = new AtomicInteger(0);

        final ResponseHandler<Boolean> addNoteHandler = new ResponseHandler<Boolean>() {

            @Override
            public void onTaskError(Exception exception) {
                requestsPending.decrementAndGet();
                handler.onTaskError(exception);
            }

            @Override
            public void onTaskSuccess(Boolean result) {
                if (requestsPending.decrementAndGet() <= 0) {

                    // check in order once all the notes have been added
                    checkInOrder(handler);
                }
            }
        };

        for (NoteData note : notes) {
            requestsPending.getAndIncrement();
            addNoteToOrder(note.getLoanGUID(), note.getAmountToInvest(), addNoteHandler);
        }
    }


    private void addNoteToOrder(final String loanGUID, final Integer amountToInvest,
                                final ResponseHandler<Boolean> handler) {

        final String url = BASE_URI + ADD_NOTE_TO_ORDER_SUFFIX;

        Request request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String operationResult = response.getString("result");

                    if (operationResult.equals("success")) {
                        handler.onTaskSuccess(true);
                    } else {
                        handler.onTaskError(new LendingClubException("Add note to order failed"));
                    }

                } catch (JSONException e) {
                    handler.onTaskError(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("loan_id", String.valueOf(loanGUID));
                params.put("remove", "false");
                params.put("investment_amount", String.valueOf(amountToInvest));

                return params;
            }
        };

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

    private void checkInOrder(final ResponseHandler<CheckInOrderResult> handler) {

        final String url = BASE_URI + CHECK_IN_ORDER_SUFFIX;

        Request request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    handler.onTaskSuccess(new ObjectMapper().readValue(response.toString(), CheckInOrderResult.class));
                } catch (IOException e) {
                    handler.onTaskError(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onTaskError(error);
            }
        });

        VolleyRequestManager.getInstance(appContext).addToRequestQueue(request);
    }

}
