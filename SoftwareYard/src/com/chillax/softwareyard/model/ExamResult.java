package com.chillax.softwareyard.model;

/**
 * Created by Xiao on 2015/9/18.
 */
public class ExamResult {
    private String name;    //课程名
    private String category;//课程属性（必修，选修）
    private String num;     //课序号
    private String credit;  //学分
    private String score;   //成绩

    public ExamResult(String name, String category, String num, String credit, String score) {
        this.name = name;
        this.category = category;
        this.num = num;
        this.credit = credit;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String get(int position) {
        System.out.println("pos:"+position);
        switch (position){
            case 0:
                return name;
            case 1:
                return category;
            case 2:
                return num;
            case 3:
                return credit;
            case 4:
                return score;
        }
        return null;
    }

    @Override
    public String toString() {
        return name+"::"+category+"::"+num+"::"+credit+"::"+score;
    }
}
