package com.chillax.softwareyard.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chillax.softwareyard.R;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.bottommenu)
public class BottomMenu extends LinearLayout {
	private onBottomMenuClickedListener listener;
	@ViewById(R.id.table)
	ImageView iv_table;
	@ViewById(R.id.zhouzhi)
	ImageView iv_zhouzhi;
	@ViewById(R.id.self)
	ImageView iv_self;
	@ViewById
	TextView tv_zhouzhi;
	@ViewById
	TextView tv_table;
	@ViewById
	TextView tv_self;
	@ViewById
	LinearLayout layout1;
	@ViewById
	LinearLayout layout2;
	@ViewById
	LinearLayout layout3;
	private int selectedColor = Color.parseColor("#55a6ff");
	public BottomMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Click
	public void layout1(View view) {
		setTabSelection(0);
		if (listener != null) {
			listener.setTabSelection(0);
		}
	}

	@Click
	public void layout2(View view) {
		setTabSelection(1);
		if (listener != null) {
			listener.setTabSelection(1);
		}
	}

	@Click
	public void layout3(View view) {
		setTabSelection(2);
		if (listener != null) {
			listener.setTabSelection(2);
		}
	}

	public void setTabSelection(int curr) {
		switch (curr) {
		case 0:
			tv_zhouzhi.setTextColor(selectedColor);
			tv_table.setTextColor(Color.BLACK);
			tv_self.setTextColor(Color.BLACK);
			iv_zhouzhi.setBackgroundResource(R.drawable.zhouzhi_selected);
			iv_table.setBackgroundResource(R.drawable.table_unselected);
			iv_self.setBackgroundResource(R.drawable.self_unselected);
			break;
		case 1:
			tv_table.setTextColor(selectedColor);
			tv_zhouzhi.setTextColor(Color.BLACK);
			tv_self.setTextColor(Color.BLACK);
			iv_table.setBackgroundResource(R.drawable.table_selected);
			iv_zhouzhi.setBackgroundResource(R.drawable.zhouzhi_unselected);
			iv_self.setBackgroundResource(R.drawable.self_unselected);
			break;
		case 2:
			tv_self.setTextColor(selectedColor);
			tv_zhouzhi.setTextColor(Color.BLACK);
			tv_table.setTextColor(Color.BLACK);
			iv_self.setBackgroundResource(R.drawable.self_selected);
			iv_zhouzhi.setBackgroundResource(R.drawable.zhouzhi_unselected);
			iv_table.setBackgroundResource(R.drawable.table_unselected);
			break;
		}
	}

	public void setOnMenuClickedListener(onBottomMenuClickedListener listener) {
		this.listener = listener;
	}

	public interface onBottomMenuClickedListener {
		void setTabSelection(int index);
	}
}
