package com.lagg.frontend;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lagg.frontend.model.Category;
import com.lagg.frontend.net.DataFetcher;
import com.lagg.frontend.net.HttpRequest;
import com.lagg.frontend.net.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {
		private static final String TAG = "MainActivity";
		private Executor executor = new ThreadPerTaskExecutor();
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
						Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
						v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
						return insets;
				});

				this.init();
		}

		private ViewGroup pageLayout;
		private void init() {
				DataFetcher.init(findViewById(R.id.main).getContext());
				this.pageLayout = (ViewGroup) findViewById(R.id.pageLayout);
				this.displayPageCategories();
		}

		private static List<Category> parseCategoryArray(String jsonString) {
				ArrayList<Category> result = new ArrayList<>();

				Log.d(TAG, "parseCategoryArray: " + jsonString);
				JSONArray jsonArray;
				try {
						jsonArray = new JSONArray(jsonString);
				} catch (JSONException je) {
						Log.e(TAG, "parseAllCategories: could not parse '" + jsonString + "' as a JSON array", je);
						return Collections.emptyList();
				}

				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
						try {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								Optional<Category> categoryOptional = Category.fromJSONObject(jsonObject);
								if (categoryOptional.isPresent()) {
										Category category = categoryOptional.get();
										if (category.id != null && category.id > 0) {
												result.add(category);
										}
								} else {
										Log.e(TAG, "parseCategoryArray: could not convert JSONObject '" + jsonObject.toString() +
														"' to Category");
								}
						} catch (JSONException je) {
								Log.d(TAG, "parseCategoryArray: could not get JSON object at index " + i);
						}
				}
				return result;
		}
		private void displayPageCategories() {
				Log.i(getLocalClassName(), "Request");

				Resources resources = getResources();
				String url = Utils.getUrlString(resources.getString(R.string.serverHostName),
								resources.getString(R.string.categoryHttpPath), false);

				RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
				queue.start();
				queue.add(new StringRequest(
								Request.Method.GET,
								url,
								new Response.Listener<String>() {
										@Override
										public void onResponse(String response) {
												Log.i(getLocalClassName(), "Response Received");
												pageLayout.removeAllViews();
												for (Category category : parseCategoryArray(response)) {
														Log.d(TAG, "displayPageCategories: Creating banner for category " + category.toString());
														CategoryBanner banner = new CategoryBanner(pageLayout.getContext(), category);
														pageLayout.addView(banner);
												}
												Log.i(getLocalClassName(), "Response Done");
										}
								},
								new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
												Log.e(getLocalClassName(), "Error");
										}
								}));

				Log.i(getLocalClassName(), "Awaiting completion");
		}
}