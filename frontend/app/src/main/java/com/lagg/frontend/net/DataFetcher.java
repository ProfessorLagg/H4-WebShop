package com.lagg.frontend.net;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;
import com.lagg.frontend.R;
import com.lagg.frontend.Utils;
import com.lagg.frontend.model.Category;
import com.lagg.frontend.model.Product;

import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.*;

// https://www.baeldung.com/java-http-request
public class DataFetcher {
	// VALUES
//	private static final Executor executor = Executors.newFixedThreadPool(2);
	private static RequestQueue requestQueue;
	private static Cache requestCache;
	private static Network requestNetwork;
	private static String serverHostName;
	private static boolean useHttps;
	private static long requestTimeout;

	private static String categoryHttpPath;
	private static Map<Integer, Category> categoryCache;

	private static String productHttpPath;
	private static Map<Integer, Product> productCache;

	// CONSTRUCTORS
	public static void init(Context context) {
		Resources resources = context.getResources();
		serverHostName = resources.getString(R.string.serverHostName);
		useHttps = resources.getBoolean(R.bool.use_https);
		requestTimeout = resources.getInteger(R.integer.requestTimeout);
		requestCache = new NoCache();
		requestNetwork = new BasicNetwork(new HurlStack());
		requestQueue = new RequestQueue(requestCache, requestNetwork);

		categoryHttpPath = resources.getString(R.string.categoryHttpPath);
		categoryCache = new HashMap<>();

		productHttpPath = resources.getString(R.string.productHttpPath);
		productCache = new HashMap<>();
	}

	// LOGGING
	private static final String TAG = "DataFetcher";

	private static void LogDebug(String method, UrlRequest request, UrlResponseInfo info) {
		String msg = method + ':';
		if (info != null) {
			msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
		}
		if (request != null) {
			msg += "\n\trequest: " + request;
		}
		Log.d(TAG, msg);
	}

	private static void LogError(String method, UrlResponseInfo info, Exception e) {
		String msg = method + ':';
		if (info != null) {
			msg += "\n\tinfo: " + info.getUrl() + ' ' + info.getHttpStatusText() + "\n";
		}
		Log.e(TAG, msg, e);
	}

	// NETWORKING

	private static class RequestErrorHandler implements Response.ErrorListener {
		private boolean handled = false;
		private final String requestUrl;

		public RequestErrorHandler(String requestUrl) {
			this.requestUrl = requestUrl;
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, "error in request to " + this.requestUrl, error);
		}
	}

	private static class StringResponseHandler implements Response.Listener<String> {
		public boolean handled;
		private final Response.Listener<String> listener;

		public StringResponseHandler(Response.Listener<String> listener) {
			this.handled = false;
			this.listener = listener;
		}

		@Override
		public void onResponse(String response) {
			this.listener.onResponse(response);
			this.handled = true;
		}
	}

	// CATEGORY
	private static void parseCategory(String jsonString) {
		try {
			Category category = new Category();
			category.loadJson(jsonString);
			categoryCache.put(category.id, category);
			Log.d(TAG, "parseCategory: parsed json string '" + jsonString + "' as Category");
		} catch (Exception e) {
			Log.e(TAG, "parseCategory: could not parse json string '" + jsonString + "' as Category", e);
		}
	}

	public static Optional<Category> loadCategory(Integer id) {
		if (id == null || id <= 0) {
			return Optional.empty();
		}
		String url = Utils.getUrlString(serverHostName, categoryHttpPath + "/" + id, useHttps);
		Log.d(TAG, "loadCategory: " + url);
		RequestErrorHandler errorHandler = new RequestErrorHandler(url);
		StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseCategory);
		StringRequest request = new StringRequest(url, responseHandler, errorHandler);
		requestQueue.add(request);
		requestQueue.start();
		while (!errorHandler.handled && !responseHandler.handled) {
			// WAITING FOR THE REQUEST TO FINISH
		}

		Category result = categoryCache.get(id);
		if (result == null) {
			return Optional.empty();
		}
		return Optional.of(result);
	}

	public static Optional<Category> getCategory(Integer id) {
		Log.d(TAG, "getCategory: " + id);
		Optional<Category> result = Optional.empty();

		if (categoryCache.containsKey(id)) {
			Category category = categoryCache.get(id);
			if (category != null) {
				result = Optional.of(category);
			} else {
				result = loadCategory(id);
			}
		} else {
			result = loadCategory(id);
		}

		if (result.isPresent()) {
			Log.d(TAG, "getCategory: found category " + result.get());
		} else {
			String id_str = id == null ? "null" : id.toString();
			Log.d(TAG, "getCategory: could not find category with id = " + id_str);
		}
		return result;
	}

	private static void parseAllCategories(String body) {
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(body);
		} catch (JSONException je) {
			Log.e(TAG, "parseAllCategories: could not parse '" + body + "' as a JSON array", je);
			return;
		}

		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Optional<Category> categoryOptional = Category.fromJSONObject(jsonObject);
				if (categoryOptional.isPresent()) {
					Category category = categoryOptional.get();
					if (category.id != null && category.id > 0) {
						categoryCache.put(category.id, category);
					}
				} else {
					Log.e(TAG, "parseAllCategories: could not convert JSONObject '" + jsonObject.toString() + "' to Category");
				}
			} catch (JSONException je) {
				Log.d(TAG, "parseAllCategories: could not get JSON object at index " + i);
			}
		}
	}

	public static List<Category> loadAllCategories() {
		Log.d(TAG, "getAllCategories");
		String url = Utils.getUrlString(serverHostName, categoryHttpPath, useHttps);
		RequestErrorHandler errorHandler = new RequestErrorHandler(url);
		StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseAllCategories);
		StringRequest request = new StringRequest(url, responseHandler, errorHandler);
		requestQueue.add(request);
		requestQueue.start();
		while (!errorHandler.handled && !responseHandler.handled) {
			// WAITING FOR THE REQUEST TO FINISH
		}

		return new ArrayList<>(categoryCache.values());
	}

	// PRODUCT
	public static void getProduct(Integer id){

	}
}
