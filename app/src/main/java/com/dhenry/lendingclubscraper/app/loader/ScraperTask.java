package com.dhenry.lendingclubscraper.app.loader;

import android.os.AsyncTask;

import com.dhenry.lendingclubscraper.app.util.NumberFormats;
import com.dhenry.lendingclubscraper.app.view.ScraperUser;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;

/**
 * Author: Dave
 */
public abstract class ScraperTask<Params, Progress, ResultType> extends AsyncTask<Params, Progress, ScraperResult<ResultType>> {

    protected final WeakReference<ScraperUser<ResultType>> parent;
    protected final NumberFormat currencyFormat = NumberFormats.CURRENCY_FORMAT;
    protected final NumberFormat percentFormat = NumberFormats.PERCENT_FORMAT;

    protected ScraperTask(WeakReference<ScraperUser<ResultType>> parent) {
        this.parent = parent;
    }

    @Override
    protected void onPostExecute(ScraperResult<ResultType> scraperResult) {
        ScraperUser<ResultType> parent = this.parent.get();

        if (parent != null) {
            if (scraperResult.hasError()) {
                parent.onScraperFailure(scraperResult.getError());
            } else {
                parent.onScraperSuccess(scraperResult.getResult());
            }
        }
    }
}
