package com.dhenry.lendingclubscraper.app.view;

/**
 * Author: Dave
 */
public interface ScraperCallback<ResultType> {

    public void onScraperFailure(Exception exception);

    public void onScraperSuccess(ResultType result);
}
