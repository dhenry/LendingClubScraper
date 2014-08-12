package com.dhenry.lendingclubscraper.app.view;

/**
 * Author: Dave
 */
public interface ScraperUser<ResultType> {

    public void onScraperFailure(Exception exception);

    public void onScraperSuccess(ResultType result);
}
