package com.dhenry.lendingclubscraper.app.tasks;

import android.os.AsyncTask;

import com.dhenry.lendingclubscraper.app.utilities.NumberFormats;
import com.dhenry.lendingclubscraper.app.views.RemoteTaskCallback;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;

public abstract class RemoteTask<Params, Progress, ResultType> extends AsyncTask<Params, Progress, RemoteTaskResult<ResultType>> {

    protected final WeakReference<RemoteTaskCallback<ResultType>> parent;
    protected final NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;
    protected final NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;

    protected RemoteTask(WeakReference<RemoteTaskCallback<ResultType>> parent) {
        this.parent = parent;
    }

    @Override
    protected void onPostExecute(RemoteTaskResult<ResultType> taskResult) {
        RemoteTaskCallback<ResultType> parent = this.parent.get();

        if (parent != null) {
            if (taskResult.hasError()) {
                parent.onTaskError(taskResult.getError());
            } else {
                parent.onTaskSuccess(taskResult.getResult());
            }
        }
    }
}
