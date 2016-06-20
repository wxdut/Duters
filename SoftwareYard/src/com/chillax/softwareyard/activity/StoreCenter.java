package com.chillax.softwareyard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chillax.config.URL;
import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.adapter.StoreDataAdapter;
import com.chillax.softwareyard.customview.ActionBar;
import com.chillax.softwareyard.customview.ActionBar.onTopBarClickedListener;
import com.chillax.softwareyard.customview.TopBar;
import com.chillax.softwareyard.dao.StoreDBDao;
import com.chillax.softwareyard.model.News;
import com.chillax.swipelistview.SwipeListView;
import com.chillax.swipelistview.SwipeListViewListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.store_center__layout)
public class StoreCenter extends BaseActivity implements SwipeListViewListener,
		TopBar.onTopClickedListener {


	@ViewById(R.id.slv)
	SwipeListView mSlv;
	AlertDialog dialog;
	@ViewById(R.id.topBar)
	TopBar mTopbar;
	ArrayList<News> storeList = App.storeList;
	StoreDBDao mDao;
	StoreDataAdapter mAdapter;
	public static final String TAG = "StoreCenter";
	@AfterViews
	void inits() {
		mTopbar.setTopListener(this);
		mDao = new StoreDBDao(this);
		mSlv.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
		mSlv.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
		mSlv.setSwipeListViewListener(this);
		if (storeList.size() == 0) {
			Toast.makeText(this, "没有任何收藏~", Toast.LENGTH_SHORT).show();
		}
		mAdapter = new StoreDataAdapter(this);
		mSlv.setAdapter(mAdapter);
		dialog = new AlertDialog.Builder(this).setTitle("确定要删除吗？").setMessage("注意：本地文件也会同时删除")
				.setPositiveButton("确认", (dialog,witch)->{
					Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
					for (int temp : reverseSortedPositions) {
						storeList.remove(temp);
					}
					mAdapter.notifyDataSetChanged();
				}).setNegativeButton("取消", (dialog, withc) -> dialog.dismiss()).create();
	}

	@Override
	public void onOpened(int position, boolean toRight) {

	}

	@Override
	public void onClosed(int position, boolean fromRight) {

	}

	@Override
	public void onListChanged() {

	}

	@Override
	public void onMove(int position, float x) {

	}

	@Override
	public void onStartOpen(int position, int action, boolean right) {

	}

	@Override
	public void onStartClose(int position, boolean right) {

	}

	@Override
	public void onClickFrontView(int position) {
		openUrl(position);
	}

	@Override
	public void onClickBackView(int position) {

	}
	private int [] reverseSortedPositions;
	@Override
	public void onDismiss(int[] reverseSortedPositions) {
		this.reverseSortedPositions=reverseSortedPositions;
		dialog.show();
	}

	@Override
	public int onChangeSwipeMode(int position) {
		return SwipeListView.SWIPE_MODE_DEFAULT;
	}

	@Override
	public void onChoiceChanged(int position, boolean selected) {

	}

	@Override
	public void onChoiceStarted() {

	}

	@Override
	public void onChoiceEnded() {

	}

	@Override
	public void onFirstListItem() {

	}

	@Override
	public void onLastListItem() {

	}

	@Override
	protected void onDestroy() {
		mDao.clear();
		for (News news : storeList) {
			mDao.insert(news);
		}
		super.onDestroy();
	}

	private void openUrl(int position) {
		News news = storeList.get(position);
		Intent i = NewsDetail_.intent(this).get();
		i
				.putExtra("title", news.getTitle())
				.putExtra("time", news.getTime())
				.putExtra("address", news.getAddress());
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
	}

	@Override
	public void onBack(View view) {
		onBackPressed();
	}

	@Override
	public void onMore(View view) {

	}

	@Override
	public void onInit(ImageView back, TextView title, ImageView moreIv, TextView moreTv) {
		title.setText("收藏中心");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_right);
	}
}
