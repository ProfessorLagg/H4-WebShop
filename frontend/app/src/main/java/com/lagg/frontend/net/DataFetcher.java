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

import java.util.*;
import java.util.stream.Collectors;

// https://www.baeldung.com/java-http-request
public class DataFetcher {
		// VALUES
		//	private static final Executor executor = Executors.newFixedThreadPool(2);
		private static Cache requestCache;
		private static Network requestNetwork;
		private static RequestQueue requestQueue;
		private static String serverHostName;
		private static boolean useHttps;
		private static long requestTimeout;

		private static String categoryHttpPath;
		private static Map<Integer, Category> categoryCache;

		private static String productHttpPath;
		private static String productByCategoryHttpPath;
		private static Map<Integer, Product> productCache;

		// CONSTRUCTORS
		public static void init(Context context) {
				Log.d(TAG, "init: started");

				Resources resources = context.getResources();
				serverHostName = resources.getString(R.string.serverHostName);
				useHttps = resources.getBoolean(R.bool.use_https);
				requestTimeout = resources.getInteger(R.integer.requestTimeout);

				requestCache = new NoCache();
				requestNetwork = new BasicNetwork(new HurlStack());
				requestQueue = new RequestQueue(requestCache, requestNetwork);
				requestQueue.start();

				categoryHttpPath = resources.getString(R.string.categoryHttpPath);
				categoryCache = new HashMap<>();

				productHttpPath = resources.getString(R.string.productHttpPath);
				productByCategoryHttpPath = resources.getString(R.string.productByCategoryHttpPath);
				productCache = new HashMap<>();

				Log.d(TAG, "init: finished");
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
						Log.e(TAG, "onErrorResponse: error in request to " + this.requestUrl, error);
				}
		}

		private static class StringResponseHandler implements Response.Listener<String> {
				public boolean handled;
				private final Response.Listener<String> listener;
				private final String url;
				public StringResponseHandler(Response.Listener<String> listener, String url) {
						this.handled = false;
						this.listener = listener;
						this.url = url;
				}

				@Override
				public void onResponse(String response) {
						Log.d(TAG, "onResponse: Received response from: " + url + ":\n" + response);
						this.listener.onResponse(response);
						this.handled = true;
				}
		}

		// CATEGORY
		private static void parseCategoryArray(String jsonString) {
				Log.d(TAG, "parseCategoryArray: " + jsonString);
				JSONArray jsonArray;
				try {
						jsonArray = new JSONArray(jsonString);
				} catch (JSONException je) {
						Log.e(TAG, "parseAllCategories: could not parse '" + jsonString + "' as a JSON array", je);
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
										Log.e(TAG, "parseCategoryArray: could not convert JSONObject '" + jsonObject.toString() +
														"' to Category");
								}
						} catch (JSONException je) {
								Log.d(TAG, "parseCategoryArray: could not get JSON object at index " + i);
						}
				}
		}
		/** Gets all the categories from the webserver */
		public static List<Category> getAllCategories() {

				String url = Utils.getUrlString(serverHostName, categoryHttpPath, useHttps);
				RequestErrorHandler errorHandler = new RequestErrorHandler(url);
				StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseCategoryArray, url);
				StringRequest request = new StringRequest(url, (String response) -> { Log.d(TAG, "onResponse: Received response: " + response); }, errorHandler);

				requestQueue.add(request);
				requestQueue.start();

				Log.d(TAG, "getAllCategories: waiting for get all categories request to " + url);
//				while (!errorHandler.handled && !responseHandler.handled) {
//						// WAITING FOR THE REQUEST TO FINISH
//				}
				Log.d(TAG, "getAllCategories: finished waiting for get all categories request to " + url);
				request.cancel();
				return new ArrayList<>(categoryCache.values());
		}

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
		/** Gets the category directly from the webserver */
		public static Optional<Category> loadCategory(Integer id) {
				if (id == null || id <= 0) {
						return Optional.empty();
				}
				String url = Utils.getUrlString(serverHostName, categoryHttpPath + "/" + id, useHttps);
				Log.d(TAG, "loadCategory: " + url);
				RequestErrorHandler errorHandler = new RequestErrorHandler(url);
				StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseCategory, url);
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
		/** Attempts to get the category from cache, otherwise gets it from the webserver */
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

		// PRODUCT

		private static void parseProduct(String jsonString) {
				Product product = new Product();
				try {
						product.loadJson(jsonString);
						if (product.id != null) {
								productCache.put(product.id, product);
						} else {
								Log.w(TAG, "parseProduct: parsing did not throw error, but product ended up with null id");
						}
				} catch (JSONException e) {
						Log.e(TAG, "parseProduct: could not parse json string '" + "' into a product", e);
				}
		}
		private static List<Product> parseProductArray(String jsonString) {
				ArrayList<Product> result = new ArrayList<>();
				JSONArray jsonArray;
				try {
						jsonArray = new JSONArray(jsonString);
				} catch (JSONException e) {
						Log.e(TAG, "parseProductArray: could not parse '" + jsonString + "' as a JSON array", e);
						return result;
				}

				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
						try {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								Optional<Product> productOptional = Product.fromJSONObject(jsonObject);
								if (productOptional.isPresent()) {
										Product product = productOptional.get();
										if (product.id != null && product.id > 0) {
												productCache.put(product.id, product);
												result.add(product);
										}
								} else {
										Log.e(TAG, "parseProductArray: could not convert JSONObject '" + jsonObject.toString() +
														"' to Product");
								}
						} catch (JSONException je) {
								Log.d(TAG, "parseProductArray: could not get JSON object at index " + i);
						}
				}
				return result;
		}
		/** Gets the product directly from the webserver */
		public static Optional<Product> loadProduct(Integer id) {
				if (id == null || id <= 0) { return Optional.empty(); }

				String url = Utils.getUrlString(serverHostName, productHttpPath + "/" + id, useHttps);
				Log.d(TAG, "loadCategory: " + url);
				RequestErrorHandler errorHandler = new RequestErrorHandler(url);
				StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseProduct, url);

				StringRequest request = new StringRequest(url, responseHandler, errorHandler);
				requestQueue.add(request);
				requestQueue.start();
				while (!errorHandler.handled && !responseHandler.handled) {/* WAITING FOR THE REQUEST TO FINISH */ }

				Product result = productCache.get(id);
				if (result == null) { return Optional.empty(); }
				return Optional.of(result);
		}
		/** Attempts to get the product from cache, otherwise gets it from the webserver */
		public static Optional<Product> getProduct(Integer id) {
				Log.d(TAG, "getProduct: " + id);
				Optional<Product> result = Optional.empty();

				if (productCache.containsKey(id)) {
						Product category = productCache.get(id);
						if (category != null) {
								result = Optional.of(category);
						} else {
								result = loadProduct(id);
						}
				} else {
						result = loadProduct(id);
				}

				if (result.isPresent()) {
						Log.d(TAG, "getProduct: found product " + result.get());
				} else {
						String id_str = id == null ? "null" : id.toString();
						Log.d(TAG, "getProduct: could not find product with id = " + id_str);
				}
				return result;
		}

		/** Gets all products in the given category from the webserver */
		public static List<Product> getProductsInCategory(Category category) {
				Log.d(TAG, "getProductsInCategory(" + category.toString() + ")");
				String url = Utils.getUrlString(serverHostName, productByCategoryHttpPath + '/' + category.id, useHttps);
				RequestErrorHandler errorHandler = new RequestErrorHandler(url);
				StringResponseHandler responseHandler = new StringResponseHandler(DataFetcher::parseProductArray, url);
				StringRequest request = new StringRequest(url, responseHandler, errorHandler);
				requestQueue.add(request);
				requestQueue.start();
				while (!errorHandler.handled && !responseHandler.handled) {/* WAITING FOR THE REQUEST TO FINISH */}

				// TODO Figure out how to get the HttpResponse instead of just reading the cache with a filter
				return productCache
								.values()
								.stream()
								.filter(p -> p.categoryId.equals(category.id))
								.collect(Collectors.toList());
		}
}
