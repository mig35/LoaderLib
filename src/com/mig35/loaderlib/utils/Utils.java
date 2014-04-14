package com.mig35.loaderlib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.mig35.loaderlib.exceptions.NoNetworkException;

import java.io.IOException;

/**
 * Date: 1/30/14
 * Time: 5:51 PM
 *
 * @author MiG35
 */
public final class Utils {

	private Utils() {
	}

	public static void checkInternet(final Context context) throws IOException {
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			throw new NoNetworkException();
		}
	}
}