package com.chillax.softwareyard.model;

public class News {
	private int id;
	private String title;
	private String time;
	private String address;
//	private boolean store;
//
//	public boolean isStore() {
//		return store;
//	}
//
//	public void setStore(boolean store) {
//		this.store = store;
//	}

	public News(String title, String time, String address) {
		this.title = title;
		this.time = time;
		this.address = address;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object o) {
		News news = (News) o;
		return title.equals(news.getTitle());
	}

	@Override
	public String toString() {
		return title+"::"+time+"::"+address;
	}
}
