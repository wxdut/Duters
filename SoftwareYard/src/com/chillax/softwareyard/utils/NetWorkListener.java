package com.chillax.softwareyard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;
import com.lidroid.xutils.util.LogUtils;

public class NetWorkListener extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			boolean isConnected = NetworkChecker.IsNetworkAvailable(context);
		}
	}

}
