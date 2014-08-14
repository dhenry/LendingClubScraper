package com.dhenry.lendingclubscraper.app.tasks;

/**
 * Author: Dave
 */
public class RemoteTaskResult<T> {
    T result;
    Exception error;

    public RemoteTaskResult(Exception exception) {
        this.error = exception;
    }

    public RemoteTaskResult(T result) {
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