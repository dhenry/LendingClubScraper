package com.dhenry.lendingclubscraper.app.utilities;

import android.content.res.Resources;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Author: Dave
 */
public class NumberFormats {

    public static final NumberFormat NUMBER_FORMAT;
    public static final NumberFormat CURRENCY_FORMAT;
    public static final NumberFormat PERCENT_FORMAT;
    public static final NumberFormat DECIMAL_FORMAT;

    static {
        Resources systemResources = Resources.getSystem();
        assert systemResources != null;

        Locale currentLocale = systemResources.getConfiguration().locale;
        NUMBER_FORMAT = NumberFormat.getInstance(currentLocale);
        CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(currentLocale);
        PERCENT_FORMAT = NumberFormat.getPercentInstance(currentLocale);
        PERCENT_FORMAT.setMinimumFractionDigits(2);
        DECIMAL_FORMAT = NumberFormat.getPercentInstance(currentLocale);
    }
}
