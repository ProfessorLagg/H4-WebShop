package com.lagg.frontend.model;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonSerializeable {
	JSONObject toJson() throws JSONException;

	void loadJson(JSONObject jsonObject) throws JSONException;
}
