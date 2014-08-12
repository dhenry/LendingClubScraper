package com.dhenry.lendingclubscraper.app.loader;

/**
 * Author: Dave
 */
public class ScraperResult<T> {
    T result;
    Exception error;

    public ScraperResult(Exception exception) {
        this.error = exception;
    }

    public ScraperResult(T result) {
        this.result = result;
    }

    public boolean hasError() {
        return (error != null);
    }

    public Exception getError() {
        return error;
    }

    public T getResult() {
        return result;
    }
}