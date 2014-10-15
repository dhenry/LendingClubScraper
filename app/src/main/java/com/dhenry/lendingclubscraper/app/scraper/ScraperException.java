package com.dhenry.lendingclubscraper.app.scraper;

public class ScraperException extends Exception {

    public ScraperException() {
    }

    public ScraperException(String detailMessage) {
        super(detailMessage);
    }

    public ScraperException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ScraperException(Throwable throwable) {
        super(throwable);
    }

}
