package com.example.milano;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

import android.net.Uri;

public class Post implements Serializable{
	private int id, price;
	private String title, description, location, category, user, email;
	private Date date;
	private ArrayList<String> images = new ArrayList<String>();
	boolean starred = false;
	public Post() { price = 0; }
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}
	
	public ArrayList<String> getImages() {
		return images;
	}
	
	public void addImage(String uri) {
		images.add(uri);
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean equals(Post other) {
		return title.equals(other.title);
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
}
