package com.chillax.softwareyard.model;

public class Detail {
	private int id;
	private String name;
	private String num;
	private String credit;
	private String category;
	private String teacher;
	private String weeks;
	private String day;
	private String room;

	public Detail() {
		super();
	}

	public Detail(String name, String num, String credit, String category,
			String teacher, String weeks, String day, String room) {
		super();
		this.name = name;
		this.num = num;
		this.credit = credit;
		this.category = category;
		this.teacher = teacher;
		this.weeks = weeks;
		this.day = day;
		this.room = room;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 课程序号
	 * 
	 */
	public String getNum() {
		return num;
	}

	/**
	 * 课程序号
	 * 
	 */
	public void setNum(String num) {
		this.num = num;
	}

	/**
	 * 学分
	 * 
	 */
	public String getCredit() {
		return credit;
	}

	/**
	 * 具体哪一天上课
	 * 
	 */
	public String getDay() {
		return day;
	}

	/**
	 * 具体哪一天上课
	 * 
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * 学分
	 * 
	 */
	public void setCredit(String credit) {
		this.credit = credit;
	}

	/**
	 * 属性（必修或者选修）
	 * 
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 属性（必修或者选修）
	 * 
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getRoom() {
		return room;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * num:课程序号, credit：学分, category：属性（必修或者选修）, teacher: 任课老师, weeks：上课周,
	 * room：上课地点
	 */
	@Override
	public String toString() {
		return "name:" + name + "," + "credit:" + credit + "," + "category:"
				+ category + "," + "teacher:" + teacher + "," + "weeks:"
				+ weeks + "," + "room:" + room;
	}
}
