package com.lagg.frontend.model;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class Category implements JsonSerializeable {
	public Integer id;
	public String name;

	public Category() {
		this.id = -1;
		this.name = "null";
	}

	public static Optional<Category> fromJSONObject(JSONObject jsonObject) {
		try {
			Category result = new Category();
			result.loadJson(jsonObject);
			return Optional.of(result);
		} catch (Exception e) {
			String jsonString = jsonObject == null ? "null" : jsonObject.toString();
			Log.e("Category", "fromJSONObject: could not parse jsonObject: " + jsonString + " as Category", e);
			return Optional.empty();
		}
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject result = new JSONObject();
		result.put("id", this.id);
		result.put("name", this.name);
		return result;
	}

	@Override
	public void loadJson(JSONObject jsonObject) throws JSONException {
		this.id = jsonObject.getInt("id");
		this.name = jsonObject.getString("name");
	}

	@Override
	public void loadJson(String jsonString) throws JSONException {
		this.loadJson(new JSONObject(jsonString));
	}


	@NonNull
	@Override
	public String toString() {
		String id_str = this.id == null ? "null" : this.id.toString();
		String name_str = this.name == null ? "null" : this.name;
		return "{\"id\": " + id_str + ", \"name\": \"" + name_str + "\"}";
	}
}
