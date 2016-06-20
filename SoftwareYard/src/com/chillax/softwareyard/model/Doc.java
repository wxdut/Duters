package com.chillax.softwareyard.model;

/**
 * 这是对下载的学生周知里的文件的一个JavaBean
 * Created by Chillax on 2015/7/27.
 */
public class Doc {
    //文件名(含后缀名)
    private String name;
    //文件大小
    private String size;
    //文件当前下载进度
    private String progress;
    //文件的下载URL地址
    private String url;
    //文件的本地保存路径
    private String local;
    public Doc(String name, String size, String progress, String url, String local) {
        this.name = name;
        this.size = size;
        this.progress = progress;
        this.url = url;
        this.local = local;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        int pro=Integer.valueOf(progress);
        if(pro>100){
            progress="100";
        }else if(pro<0){
            progress="0";
        }
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    @Override
    public boolean equals(Object o) {
        return ((Doc)o).getName().equals(name);
    }
}
