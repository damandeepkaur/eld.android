package com.bsmwireless.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.bsmwireless.common.App;
import com.bsmwireless.data.network.RetrofitException;

import java.util.Locale;

import app.bsmuniversal.com.R;


public class NetworkUtils {

    public static boolean isOnlineMode() {
        ConnectivityManager cm = (ConnectivityManager) App.getComponent().context().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static CharSequence getErrorMessage(RetrofitException error, Context context) {
        String message;
        switch (error.getCode()) {
            case RetrofitException.NETWORK_ERROR_CODE:
                return context.getString(R.string.error_network);

            case RetrofitException.UNEXPECTED_ERROR_CODE:
                return context.getString(R.string.error_unexpected);

            case 400:
                message = context.getString(R.string.error_bad_request);
                break;

            case 401:
                message = context.getString(R.string.error_not_authenticated);
                break;

            case 408:
                message = context.getString(R.string.error_timeout);
                break;

            case 500:
                message = context.getString(R.string.error_internal_server);
                break;

            default:
                message = context.getString(R.string.error_unknown);
                break;
        }

        String text = String.format(Locale.getDefault(), "(%s %d) %s", context.getString(R.string.error), error.getCode(), message);
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_light)),
                0,
                text.length() - message.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }
}