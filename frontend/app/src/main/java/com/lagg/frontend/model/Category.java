package com.lagg.frontend.model;

public class Category {
		public Integer id;
		public String name;
		public void cloneFrom(Category other, boolean cloneId) {
				if (cloneId && other.id != null) { this.id = other.id; }
				if (other.name != null) { this.name = other.name; }
		}
}
