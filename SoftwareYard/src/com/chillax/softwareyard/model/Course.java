package com.chillax.softwareyard.model;

public class Course {
	private String name;
	private String room;
	private String day;
	private String order;

	public Course() {
		super();
	}

	public Course(String name, String room, String day, String order) {
		super();
		this.name = name;
		this.room = room;
		this.day = day;
		this.order = order;
	}

	/**
	 * 课程名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 课程名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 房间号
	 */
	public String getRoom() {
		return room;
	}

	/**
	 * 房间号
	 */
	public void setRoom(String room) {
		this.room = room;
	}

	/**
	 * 哪一天上课
	 */
	public String getDay() {
		return day;
	}

	/**
	 * 哪一天上课
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * 一天中五节课中的第几节课（0—4）
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * 一天中五节课中的第几节课（0—4）
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "Course [name=" + name + ", room=" + room + ", day=" + day
				+ ", order=" + order + "]";
	}

}
