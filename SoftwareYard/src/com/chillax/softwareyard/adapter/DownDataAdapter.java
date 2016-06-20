package com.chillax.softwareyard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.chillax.softwareyard.App;
import com.chillax.softwareyard.R;
import com.chillax.softwareyard.model.Doc;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Chillax on 2015/7/28.
 */
public class DownDataAdapter extends BaseAdapter {
    private Context mContext;
    private List<Doc> dataList = App.docList;

    public DownDataAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView,
                R.layout.download_item, position, parent);
        Doc doc = dataList.get(position);
        holder.setText(R.id.name, doc.getName());
        int pro = Integer.valueOf(doc.getProgress());
        if (pro == 100) {
            holder.setText(R.id.progress, "下载完成").setText(R.id.size, doc.getSize()+"kb");
        } else {
            holder.setText(R.id.progress, pro + "%").setText(R.id.size, doc.getSize()+"kb");
        }
        setImageResource(holder.getView(R.id.image), doc.getName().split("\\.")[1]);
        return holder.getConvertView();
    }

    private void setImageResource(ImageView iv, String suf) {
        switch (suf) {
            case "docx":
                iv.setImageResource(R.drawable.file_word);
                break;
            case "xlsx":
                iv.setImageResource(R.drawable.file_excel);
                break;
            case "pptx":
                iv.setImageResource(R.drawable.file_ppt);
                break;
            default:
                iv.setImageResource(R.drawable.file_word);
                break;
        }
    }
}
