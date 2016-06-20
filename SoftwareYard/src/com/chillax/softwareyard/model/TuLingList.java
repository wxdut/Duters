package com.chillax.softwareyard.model;

/**
 * Created by Chillax on 2015/8/14.
 */
public class TuLingList {
    private StringBuffer out;
    /**
     * 新闻类：
     text       内容
     article	标题
     source	    来源
     detailurl	详情地址
     icon	    图标地址
     */
    public String text,article,source,detailurl,icon;
    /**
     * 列车类：
     trainnum	车次
     start	    起始站
     terminal	到达站
     starttime	开车时间
     endtime	到达时间
     detailurl	详情地址
     icon	    图标地址
     */
    public String trainnum,start,terminal,starttime,endtime,detail;
    /**
     * 航班类：
     flight	    航班
     route	    航班路线
     starttime	起飞时间
     endtime	到达时间
     state	    航班状态
     detailurl	详情
     icon	    图标地址
     */
    public String flight,route,state;
    /**
     * 菜谱类:
     name	    名称
     info	    详情
     detailurl	详情链接
     icon	    图标地址
     */
    public String name,info;

    @Override
    public String toString() {
        out=new StringBuffer();
        append("内容",text);
        out.append("\n\t\n\t");
        append("名称", name);
        append("详情",info);
        append("标题",article);
        append("来源",source);
        append("车次",trainnum);
        append("起始站",start);
        append("到达站",terminal);
        append("航班",flight);
        append("航班路线",route);
        append("开车时间",starttime);
        append("到达时间",endtime);
        append("航班状态",state);
        append("详情地址",detailurl);
        return out.toString();
    }
    private void append(String name,String value){
        if(value!=null){
            out.append(name + ":" + value + "\n\t");
        }
    }
}
