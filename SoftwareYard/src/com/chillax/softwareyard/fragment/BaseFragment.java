package com.chillax.softwareyard.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chillax.softwareyard.R;

public class BaseFragment extends Fragment {
	public static final int NET_ERROR=-1;
	public void showToast(String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	public Context context;
	public View contentView;
	public BaseFragment() {
		super();
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity) {
		context = getActivity();
		super.onAttach(activity);
	}
}
