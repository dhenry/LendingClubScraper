package com.dhenry.lendingclubscraper.app.views;

public interface RemoteTaskCallback<ResultType> {

    public void onTaskError(Exception exception);

    public void onTaskSuccess(ResultType result);
}
