package com.lagg.frontend.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lagg.frontend.ArrayListMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Product implements JsonSerializeable {
		public Integer id;

		public String title;

		public BigDecimal price;

		public String description;

		// TODO SAVE THE IMAGES IN THE DATABASE AND MAKE A CONTROLLER
		public String image;

		public Category category;
		@Override
		public JSONObject toJson() throws JSONException {
				JSONObject result = new JSONObject();
				result.put("id", this.id);
				result.put("title", this.title);
				result.put("price", this.price);
				result.put("description", this.description);
				result.put("image", this.image);
				result.put("category", this.category);
				return result;
		}
		@Override
		public void loadJson(JSONObject jsonObject) throws JSONException {
				this.id = jsonObject.getInt("id");
				this.title = jsonObject.getString("title");
				this.price = BigDecimal.valueOf(jsonObject.getDouble("price"));
				this.description = jsonObject.getString("description");
				this.image = jsonObject.getString("image");
				this.category = new Category();
				this.category.loadJson(jsonObject.getJSONObject("category"));
		}
		@Override
		public void loadJson(String jsonString) throws JSONException {
				JSONObject jsonObject = new JSONObject(jsonString);
				this.loadJson(jsonObject);
		}
		@NonNull
		@Override
		public String toString() {
				ArrayList<String> fields = new ArrayList<>();

				fields.add("\"id\": " + this.id.toString());
				fields.add("\"title\": \"" + this.title + '"');
				fields.add("\"price\": " + this.price.toString());
				fields.add("\"description\": \"" + this.description + '"');
				fields.add("\"image\": \"" + this.image + '"');
				fields.add("\"category: " + this.category.toString());

				return "{" + String.join(", ", fields) + "}";
		}
		public static Optional<Product> fromJSONObject(JSONObject jsonObject) {
				try {
						Product result = new Product();
						result.loadJson(jsonObject);
						return Optional.of(result);
				} catch (Exception e) {
						String jsonString = jsonObject == null ? "null" : jsonObject.toString();
						Log.e("Product", "fromJSONObject: could not parse jsonObject: " + jsonString + " as Product", e);
						return Optional.empty();
				}
		}
}
