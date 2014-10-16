package com.dhenry.lendingclubscraper.app.lendingClub;

public interface ResponseHandler<ResultType> {

    public void onTaskError(Exception exception);

    public void onTaskSuccess(ResultType result);

    public ResultType getResult();
}
