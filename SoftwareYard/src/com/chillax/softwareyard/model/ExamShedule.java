package com.chillax.softwareyard.model;

/**
 * Created by Xiao on 2015/9/28.
 */
public class ExamShedule {
    /**
     * 考试名
     */
    private String examName;
    /**
     * 校区
     */
    private String schoolArea;
    /**
     * 教学楼
     */
    private String teachBuilding;
    /**
     * 教室
     */
    private String room;
    /**
     * 课程名
     */
    private String courseName;
    /**
     * 考试周次
     */
    private String week;
    /**
     * 考试星期
     */
    private String day;
    /**
     * 考试时间
     */
    private String time;
    /**
     * 准考证号
     */
    private String number;

    public ExamShedule(String examName, String schoolArea, String teachBuilding, String room, String courseName, String week, String day, String time, String number) {
        this.examName = examName;
        this.schoolArea = schoolArea;
        this.teachBuilding = teachBuilding;
        this.room = room;
        this.courseName = courseName;
        this.week = week;
        this.day = day;
        this.time = time;
        this.number = number;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSchoolArea() {
        return schoolArea;
    }

    public void setSchoolArea(String schoolArea) {
        this.schoolArea = schoolArea;
    }

    public String getTeachBuilding() {
        return teachBuilding;
    }

    public void setTeachBuilding(String teachBuilding) {
        this.teachBuilding = teachBuilding;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String get(int position) {
        switch (position) {
            case 0:
                return examName;
            case 1:
                return schoolArea;
            case 2:
                return teachBuilding;
            case 3:
                return room;
            case 4:
                return courseName;
            case 5:
                return week;
            case 6:
                return day;
            case 7:
                return time;
            case 8:
                return number;
        }
        return null;
    }

    @Override
    public String toString() {
        return examName + dot + schoolArea + dot + teachBuilding + dot + room + dot + courseName + dot + week + dot + day + dot + time + dot + number;
    }

    private String dot = "::";
}
