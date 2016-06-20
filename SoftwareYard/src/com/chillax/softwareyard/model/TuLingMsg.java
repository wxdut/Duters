package com.chillax.softwareyard.model;

/**
 * Created by Chillax on 2015/8/13.
 */
public class TuLingMsg<T> {
    /**
     code	说明
     100000	文本类数据
     305000	列车
     306000	航班
     200000	网址类数据
     302000	新闻
     308000	菜谱、视频、小说
     40001	key的长度错误（32位）
     40002	请求内容为空
     40003	key错误或帐号未激活
     40004	当天请求次数已用完
     40005	暂不支持该功能
     40006	服务器升级中
     40007	服务器数据格式异常
     */
    public String code;
    /**
     * text 文本内容
     */
    public String text;
    /**
     * url  链接地址
     */
    public String url;
    /**
     * list json字符串,列表信息
     */
    public T list;

    @Override
    public String toString() {
        StringBuffer out=new StringBuffer();
        out.append(url==null?text:text+"\n\t"+"为您找到以下链接："+"\n\t"+url+"\n\t");
        if(list==null){
            return out.toString();
        }
        for (TuLingList item:(TuLingList[])list){
            out.append(item).append("\n\t");
        }
        return out.toString();
    }
    public void reset(){
        code=null;
        text=null;
        url=null;
        list=null;
    }
}
