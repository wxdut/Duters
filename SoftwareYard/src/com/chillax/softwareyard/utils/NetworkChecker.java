package com.chillax.softwareyard.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class NetworkChecker {

	private static ConnectivityManager manager;

	public static boolean IsNetworkAvailable(Context context) {
		manager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() != null) {
			return manager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static boolean IsWifiAvailable(Context context) {
		manager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	public static boolean IsMobileAvailable(Context context) {
		manager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	public static int getConnectedType(Context context) {
		manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return info.getType();
		}
		return -1;
	}
}
